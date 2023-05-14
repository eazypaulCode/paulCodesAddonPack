@file:Suppress("UNUSED")
package de.eazypaul.paulCodesAddonPack.listener

import de.eazypaul.paulCodesAddonPack.utils.cmp
import de.eazypaul.paulCodesAddonPack.utils.decorate
import de.eazypaul.paulCodesAddonPack.utils.plus
import de.eazypaul.paulCodesAddonPack.utils.prefix
import net.axay.kspigot.event.listen
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener {

    private val onJoin = listen<PlayerJoinEvent> { event ->
        event.player.sendMessage(prefix + cmp("This server is using ") + cmp("paulCode's MUtils Extension Pack", NamedTextColor.BLUE).decorate(true).clickEvent(
            ClickEvent.openUrl("https://github.com/eazypaulCode/paulCodesAddonPack")).hoverEvent(HoverEvent.showText(cmp("Click to show Extension Pack", NamedTextColor.BLUE))) + cmp(" by ") + cmp("eazypaulCode", NamedTextColor.BLUE).decorate(true).clickEvent(
            ClickEvent.openUrl("https://eazypaul.de/")).hoverEvent(HoverEvent.showText(cmp("Click to open Portfolio", NamedTextColor.BLUE))) + cmp(".", NamedTextColor.DARK_GRAY))
    }

}