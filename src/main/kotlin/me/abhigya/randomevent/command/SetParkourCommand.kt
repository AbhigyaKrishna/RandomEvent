package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.LocationSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetParkourCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        when (args[0].toInt()) {
            1 -> {
                LocationSerializer(sender.getTargetBlock(10)!!.location).save(plugin.config!!, "parkour1")
                plugin.reloadConfig0()
            }
            2 -> {
                LocationSerializer(sender.getTargetBlock(10)!!.location).save(plugin.config!!, "parkour2")
                plugin.reloadConfig0()
            }
            3 -> {
                LocationSerializer(sender.location).save(plugin.config!!, "parkour-spawn")
                plugin.reloadConfig0()
            }
            else -> {
                sender.sendMessage("Invalid parkour number")
                return true
            }
        }
        sender.sendMessage("Parkour set ${args[0]}")
        return true
    }

}