package de.eazypaul.paulCodesAddonPack.mods

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.eazypaul.paulCodesAddonPack.AddonManager
import de.eazypaul.paulCodesAddonPack.utils.AddonMod
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.event.player.PlayerMoveEvent

class DamageOnMove : Challenge {

    private var damage: Int = 2

    override fun start(): Boolean {
        val settings = AddonManager.getSettings(AddonMod.DAMAGE_ON_MOVE).settings
        damage = settings["damage"]?.toInt()?.getValue() ?: 2
        return true
    }

    override fun register() {
        onMove.register()
    }

    override fun unregister() {
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {event ->
        if (event.from.block != event.to.block) {
            val player = event.player
            player.health = (player.health - damage).coerceAtLeast(0.0)
            player.damage(0.01)
        }
    }
}