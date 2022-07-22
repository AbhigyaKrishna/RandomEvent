package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSubmitLocationCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        plugin.config!!.set("submit-location", sender.location)
        plugin.reloadConfig0()
        return true
    }

}