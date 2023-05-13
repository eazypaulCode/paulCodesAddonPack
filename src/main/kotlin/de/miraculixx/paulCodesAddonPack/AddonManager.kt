package de.miraculixx.paulCodesAddonPack

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.paulCodesAddonPack.utils.AddonMod
import de.miraculixx.paulCodesAddonPack.utils.cmp
import de.miraculixx.paulCodesAddonPack.utils.plus
import de.miraculixx.paulCodesAddonPack.utils.prefix
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.axay.kspigot.extensions.console
import net.kyori.adventure.text.format.NamedTextColor
import java.io.File

object AddonManager {
    private val configFile = File("${PluginInstance.dataFolder.path}/settings.json")

    private val settings: MutableMap<AddonMod, ChallengeData> = mutableMapOf()

    fun getSettings(mod: AddonMod): ChallengeData {
        return settings.getOrPut(mod) { mod.getDefaultSetting() }
    }

    fun loadMods() {
        val api = MChallengeAPI.instance
        if (api == null) {
            console.sendMessage(prefix + cmp("Failed to connect with MUtils-Challenge API!", NamedTextColor.RED))
            return
        }

        if (configFile.exists()) {
            try {
                settings.putAll(Json.decodeFromString<Map<AddonMod, ChallengeData>>(configFile.readText()))
            } catch (e: Exception) {
                console.sendMessage(prefix + cmp("Failed to read settings!"))
                console.sendMessage(prefix + cmp(e.message ?: "Reason Unknown"))
            }
        }

        AddonMod.values().forEach { mod ->
            val prodData = api.addChallenge(mod.uuid, mod.getModData())
            if (prodData == null) {
                console.sendMessage(prefix + cmp("Failed to inject ${mod.name} to MChallenge!"))
                return@forEach
            }
            settings[mod] = prodData.data
        }

        console.sendMessage(prefix + cmp("Successfully hooked in!"))
    }

    fun saveMods() {
        configFile.writeText(Json.encodeToString(settings))
    }
}