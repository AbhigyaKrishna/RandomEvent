package me.abhigya.randomevent.util

import me.abhigya.randomevent.RandomEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

object Util {

    private val key = NamespacedKey(RandomEvent.instance, "custom")

    fun isCustomDiamond(item: ItemStack): Boolean {
        return item.type == Material.DIAMOND && item.itemMeta?.persistentDataContainer?.has(key) == true
    }

    fun setCustomDiamond(item: ItemStack) {
        item.editMeta {
            it.persistentDataContainer.set(key, PersistentDataType.BYTE, 1)
        }
    }

    fun randomCircleVector(radius: Int, point: Vector): Vector {
        val circ = radius * 2 * Math.PI
        val x = cos(circ)
        val z = sin(circ)
        return Vector(x + point.x, point.y, z + point.y)
    }

}