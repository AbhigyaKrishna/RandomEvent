package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.LocationSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetTntCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        if (args[0] == "debug") {
            plugin.chaseSequence.spawnTnt()
            sender.sendMessage("TNT Spawned")
            return true
        }
        when (args[0].toInt()) {
            1 -> {
                LocationSerializer(sender.getTargetBlock(10)!!.location).save(plugin.config!!, "tnt1")
                plugin.reloadConfig0()
            }
            2 -> {
                LocationSerializer(sender.getTargetBlock(10)!!.location).save(plugin.config!!, "tnt2")
                plugin.reloadConfig0()
            }
            3 -> {
                LocationSerializer(sender.location).save(plugin.config!!, "tnt-spawn")
                plugin.reloadConfig0()
            }
            else -> {
                sender.sendMessage("Invalid type")
                return true
            }
        }
        sender.sendMessage("Set ${args[0]}")
        return true
    }

}