package de.eazypaul.paulCodesAddonPack

import de.eazypaul.paulCodesAddonPack.listener.JoinListener
import net.axay.kspigot.main.KSpigot

class PaulCodesAddonPack : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        lateinit var addonName: String
    }

    override fun load() {
        INSTANCE = this
        @Suppress("DEPRECATION")
        addonName = description.name
    }

    override fun startup() {
        AddonManager.loadMods()
        JoinListener
    }

    override fun shutdown() {
        AddonManager.saveMods()
    }
}

val PluginInstance by lazy { PaulCodesAddonPack.INSTANCE }