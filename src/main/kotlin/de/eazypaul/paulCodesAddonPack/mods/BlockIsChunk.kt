package de.eazypaul.paulCodesAddonPack.mods

import de.eazypaul.paulCodesAddonPack.AddonManager
import de.eazypaul.paulCodesAddonPack.utils.AddonMod
import de.eazypaul.paulCodesAddonPack.utils.cmp
import de.eazypaul.paulCodesAddonPack.utils.plus
import de.eazypaul.paulCodesAddonPack.utils.prefix
import de.miraculixx.challenge.api.modules.challenges.Challenge
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.allBlocks
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.io.File
import java.util.*

class BlockIsChunk: Challenge {

    private val worldName = UUID.randomUUID().toString()

    private var insecureMode: Boolean = false

    private lateinit var overworld: World
    private lateinit var nether: World
    private lateinit var end: World

    override fun start(): Boolean {
        val settings = AddonManager.getSettings(AddonMod.BLOCK_IS_CHUNK).settings
        insecureMode = settings["insecure"]?.toBool()?.getValue() ?: false
        broadcast(prefix + cmp("Information", NamedTextColor.BLUE)
                + cmp(":", NamedTextColor.DARK_GRAY)
                + cmp(" ") + cmp("Your challenge is not bugged, the worlds are being generated. This can take up to ")
                + cmp("a minute", NamedTextColor.BLUE)
                + cmp(".", NamedTextColor.DARK_GRAY))
        overworld = WorldCreator.name(worldName).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NORMAL).createWorld() ?: return false
        nether = WorldCreator.name("${worldName}_nether").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NETHER).createWorld() ?: return false
        end = WorldCreator.name("${worldName}_the_end").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.THE_END).createWorld() ?: return false
        val loc = overworld.spawnLocation
        onlinePlayers.forEach { player ->
            player.teleportAsync(loc)
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 1f)
        }
        return true
    }

    override fun stop() {
        val loc = worlds[0].spawnLocation
        onlinePlayers.forEach { player ->
            player.teleportAsync(loc)
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 1f)
        }

        overworld.removeWorld()
        nether.removeWorld()
        end.removeWorld()
    }

    override fun register() {
        onBlockPlace.register()
        onRespawn.register()
    }

    override fun unregister() {
        onBlockPlace.unregister()
        onRespawn.unregister()
    }

    private val onBlockPlace = listen<BlockPlaceEvent>(register = false) { event ->
        val location = event.block.location
        if (!location.world.name.startsWith(worldName)) return@listen
        val material = event.block.type
        val chunk = location.chunk
        if (!insecureMode && isInvalidItem(material)) return@listen

        for (block in chunk.allBlocks) {
            when(block.type) {
                Material.BEDROCK, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.WATER, Material.LAVA, Material.AIR -> continue
                else -> block.type = material
            }
        }
    }

    private val onRespawn = listen<PlayerRespawnEvent>(register = false) { event ->
        val player = event.player
        if (player.potentialBedLocation != null) return@listen
        event.respawnLocation = overworld.spawnLocation
        player.sendMessage(prefix + cmp("You have been automatically respawned in the challenge world!"))
        taskRunLater(1L, false) {
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 1f)
        }
    }

    private fun isInvalidItem(material: Material): Boolean {
        val versionSplit = Bukkit.getServer().minecraftVersion.split('.')
        val majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0

        if (!material.isSolid) return true
        val name = material.name
        if (
            Tag.ALL_SIGNS.isTagged(material) || Tag.BANNERS.isTagged(material) || Tag.BANNERS.isTagged(material) ||
            Tag.BEDS.isTagged(material) || Tag.WOOL_CARPETS.isTagged(material) || Tag.SHULKER_BOXES.isTagged(material) ||
            Tag.STAIRS.isTagged(material) || Tag.SLABS.isTagged(material) || Tag.DOORS.isTagged(material) ||
            Tag.FLOWER_POTS.isTagged(material) || Tag.FLOWERS.isTagged(material) || Tag.LEAVES.isTagged(material) ||
            Tag.SAPLINGS.isTagged(material) || Tag.CORALS.isTagged(material) || Tag.WALLS.isTagged(material) ||
            Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material) || Tag.WALLS.isTagged(material) ||
            Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material) || Tag.BUTTONS.isTagged(material) ||
            Tag.TRAPDOORS.isTagged(material) || Tag.PRESSURE_PLATES.isTagged(material) || Tag.RAILS.isTagged(material) ||
            Tag.CLIMBABLE.isTagged(material) || Tag.CANDLES.isTagged(material) || Tag.CANDLE_CAKES.isTagged(material) ||
            Tag.ANVIL.isTagged(material)
        ) return true

        if (
            name.endsWith("GLASS") || name.endsWith("GLASS_PANE") || name.endsWith("HANGING_SIGN") ||
            name.endsWith("HEAD") || name.contains("CORAL")
        ) return true

        if (majorVersion < 20) {
            if (
                name.contains("CHERRY") || name.contains("BAMBOO") || name.startsWith("SUSPICIOUS") ||
                name.endsWith("_HANGING_SIGN")
            ) return true
            when (name) {
                "TORCHFLOWER", "PINK_PETALS", "CHISELED_BOOKSHELF",
                "DECORATED_POT", "TORCHFLOWER_SEEDS" -> return true
            }
        }

        return when (material) {
            Material.KELP, Material.DRAGON_EGG, Material.SOUL_LANTERN,
            Material.LANTERN, Material.TORCH, Material.SOUL_TORCH,
            Material.TURTLE_EGG, Material.AMETHYST_CLUSTER, Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD, Material.CHAIN,
            Material.BAMBOO, Material.END_ROD, Material.SEAGRASS,
            Material.TALL_SEAGRASS, Material.GRASS, Material.TALL_GRASS,
            Material.BEEHIVE, Material.LAVA, Material.WATER,
            Material.SNOW, Material.GRINDSTONE, Material.POINTED_DRIPSTONE,
            Material.BEE_NEST, Material.CHEST, Material.TRAPPED_CHEST,
            Material.DISPENSER, Material.FURNACE, Material.BREWING_STAND,
            Material.HOPPER, Material.DROPPER, Material.SHULKER_BOX,
            Material.BARREL, Material.SMOKER, Material.BLAST_FURNACE,
            Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.LECTERN,
            Material.BEACON, Material.SPAWNER, Material.JUKEBOX,
            Material.ENCHANTING_TABLE, Material.END_PORTAL, Material.ENDER_CHEST,
            Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
            Material.SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_WALL_SKULL,
            Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK,
            Material.JIGSAW, Material.DAYLIGHT_DETECTOR, Material.COMPARATOR,
            Material.CONDUIT, Material.BELL, Material.END_GATEWAY,
            Material.SCULK_CATALYST, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER,
            Material.LIGHTNING_ROD, Material.IRON_BARS, Material.DIRT_PATH -> true

            else -> false
        }
    }

    private fun World?.removeWorld() {
        this?.let { Bukkit.unloadWorld(it, false) }
        File(worldName).deleteRecursively()
    }
}