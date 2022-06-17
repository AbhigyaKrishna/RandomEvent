package me.abhigya.randomevent.util.random

import me.abhigya.randomevent.troll.Troll
import java.util.Random

class WeightedRandomList<T>(private val random: Random, private val chanceMapper : java.util.function.Function<Troll<T>, Int>, vararg elements: Troll<T>) {

    private var list = elements.toMutableList()
    private var total : Int = list.sumOf { chanceMapper.apply(it) }

    fun randomValue() : Troll<T> {
        val chance = random.nextInt(total)
        var sum = 0
        for (element in list) {
            sum += element.chance
            if (chance < sum) {
                return element
            }
        }
        return list.last()
    }

    fun add(element: Troll<T>) {
        list.add(element)
        total += element.chance
    }

}