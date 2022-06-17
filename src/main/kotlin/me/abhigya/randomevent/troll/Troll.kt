package me.abhigya.randomevent.troll

import org.bukkit.Bukkit
import java.util.function.Consumer

class Troll<T>(val name : String, val chance : Int, private val fn : Consumer<T>) {

    fun execute(t : T) {
        fn.accept(t)
    }

    fun debugExecute(t : T, str : java.util.function.Function<Troll<T>, String>) {
        Bukkit.broadcastMessage(str.apply(this))
        fn.accept(t)
    }

}