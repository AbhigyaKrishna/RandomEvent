package me.abhigya.randomevent.troll

import org.bukkit.Bukkit
import java.util.function.Consumer

@Suppress("DEPRECATION")
class Troll<T>(val name : String, val chance : Int, private val fn : Consumer<T>) {

    fun debugExecute(t : T, str : java.util.function.Function<Troll<T>, String>) {
        Bukkit.broadcastMessage(str.apply(this))
        fn.accept(t)
    }

}