package me.abhigya.randomevent

import org.bukkit.plugin.java.JavaPlugin

class RandomEvent : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(SomeListener(), this)
    }

}