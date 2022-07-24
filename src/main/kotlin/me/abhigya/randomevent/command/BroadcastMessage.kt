package me.abhigya.randomevent.command

import me.abhigya.randomevent.RandomEvent
import me.abhigya.randomevent.util.TitleST
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BroadcastMessage(private val plugin: RandomEvent) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        val title = TitleST.valueOf(args[0])
        if (title == null) {
            sender.sendMessage("Invalid title format")
            return true
        }

        for (player in Bukkit.getOnlinePlayers()) {
            title.send(player)
        }
        return true
    }
}