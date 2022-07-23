package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.LocationSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSubmitLocationCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        var location: Location? = null
        for (x in (sender.location.blockX - 1) until (sender.location.blockX + 1)) {
            for (y in (sender.location.blockY - 1) until (sender.location.blockY + 1)) {
                for (z in (sender.location.blockZ - 1) until (sender.location.blockZ + 1)) {
                    val block = sender.world.getBlockAt(x, y, z)
                    if (block.type == Material.ENDER_CHEST) {
                        location = block.location
                    }
                }
            }
        }
        if (location == null) {
            sender.sendMessage("You must be standing near an ender chest.")
            return true
        }
        LocationSerializer(location).save(plugin.config!!, "submit-location")
        plugin.reloadConfig0()
        sender.sendMessage("Submit location set.")
        return true
    }

}