package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.LocationSerializer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetTrapLocation(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        when(args[0].toInt()) {
            1 -> {
                val block = sender.location.world.getBlockAt(sender.location)
                if (block.type != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    sender.sendMessage("You must be standing on a pressure plate to set a trap location.")
                    return false
                }
                LocationSerializer(block.location).save(plugin.config!!, "trap1")
                plugin.reloadConfig0()
            }
        }
        return true
    }

}