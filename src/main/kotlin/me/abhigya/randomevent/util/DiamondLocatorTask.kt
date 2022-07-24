package me.abhigya.randomevent.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.scheduler.BukkitRunnable

class DiamondLocatorTask(
    val item: Item
) : BukkitRunnable() {

    override fun run() {
        val component = Component.text("Diamond dropped at ", NamedTextColor.GREEN)
            .append(Component.text("x: ${item.location.blockX}, y: ${item.location.blockY}, z: ${item.location.blockZ}", NamedTextColor.YELLOW))
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendActionBar(component)
        }
    }

}