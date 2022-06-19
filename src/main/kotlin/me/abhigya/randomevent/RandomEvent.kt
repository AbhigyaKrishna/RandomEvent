package me.abhigya.randomevent

import me.abhigya.randomevent.custom.mobs.ChickenTheChad
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class RandomEvent : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(SomeListener(), this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (command.name == "chickenTheChad") {
            val chickenTheChad = ChickenTheChad(sender.location)
            val world = ((sender.world as CraftWorld).handle)
            world.addFreshEntity(chickenTheChad)
        }
        return true
    }
}