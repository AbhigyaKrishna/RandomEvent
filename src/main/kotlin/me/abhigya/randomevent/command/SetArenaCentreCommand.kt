package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.LocationSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetArenaCentreCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        LocationSerializer(sender.location).save(plugin.config!!, "arena-location")
        plugin.reloadConfig0()
        sender.sendMessage("Arena centre set!")
        return true
    }

}