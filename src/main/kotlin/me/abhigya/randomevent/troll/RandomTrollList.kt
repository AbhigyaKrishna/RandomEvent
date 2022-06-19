package me.abhigya.randomevent.troll

import me.abhigya.randomevent.util.random.WeightedRandomList
import java.util.concurrent.ThreadLocalRandom

abstract class RandomTrollList<T> : WeightedRandomList<Troll<T>>(ThreadLocalRandom.current()) {

    init {
        for (troll in list()) {
            if (troll == null) {
                println("troll is null")
                continue
            }
            add(troll)
        }
    }

    abstract fun list(): List<Troll<T>>

}