package me.abhigya.randomevent.troll

import me.abhigya.randomevent.util.random.WeightedElement
import org.bukkit.Bukkit

@Suppress("DEPRECATION")
class Troll<T>(val name : String, private val chance : Int, private val fn : (T) -> Unit) : WeightedElement {

    fun execute(t : T) {
        fn(t)
    }

    fun debugExecute(t : T, str : (Troll<T>) -> String) {
        Bukkit.broadcastMessage(str(this))
        fn(t)
    }

    override fun chance(): Int {
        return chance
    }

}