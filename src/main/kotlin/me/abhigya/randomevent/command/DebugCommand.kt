package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.Util
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugCommand(private val plugin: RandomEvent) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        if (args[0] == "song") {
            plugin.chaseSequence.runSong()
            sender.sendMessage("Song started")
        } else if (args[0] == "book") {
            sender.inventory.addItem(Util.hintBook)
            sender.sendMessage("Book added")
        }
        return true
    }

}