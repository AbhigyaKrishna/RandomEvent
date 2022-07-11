package me.abhigya.randomevent.troll

import me.abhigya.randomevent.util.random.WeightedElement
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class Troll<T>(val name : String, private val chance : Int, private val fn : (T) -> Unit) : WeightedElement {

    fun execute(t : T) {
        fn(t)
    }

    fun debugExecute(t : T, str : (Troll<T>) -> String) {
        Bukkit.broadcast(Component.text(str(this)))
        fn(t)
    }

    override fun chance(): Int {
        return chance
    }

}