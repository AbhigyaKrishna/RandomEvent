package me.abhigya.randomevent.troll

import me.abhigya.randomevent.util.random.WeightedElement
import org.bukkit.Bukkit
import java.util.function.Consumer

@Suppress("DEPRECATION")
class Troll<T>(val name : String, private val chance : Int, private val fn : Consumer<T>) : WeightedElement {

    fun execute(t : T) {
        fn.accept(t)
    }

    fun debugExecute(t : T, str : java.util.function.Function<Troll<T>, String>) {
        Bukkit.broadcastMessage(str.apply(this))
        fn.accept(t)
    }

    override fun chance(): Int {
        return chance
    }

}