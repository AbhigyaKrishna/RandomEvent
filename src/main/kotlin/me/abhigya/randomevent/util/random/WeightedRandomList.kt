package me.abhigya.randomevent.util.random

import java.util.*

open class WeightedRandomList<T : WeightedElement>(private val random: Random, vararg elements: T) {

    private val list = elements.toMutableList()
    private var total = list.sumOf { it.chance() }

    fun randomValue() : T {
        val chance = random.nextInt(total)
        var sum = 0
        for (element in list) {
            sum += element.chance()
            if (chance < sum) {
                return element
            }
        }
        return list.last()
    }

    fun add(element: T) {
        list.add(element)
        total += element.chance()
    }

}