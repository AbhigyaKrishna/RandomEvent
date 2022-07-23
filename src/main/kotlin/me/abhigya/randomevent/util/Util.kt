package me.abhigya.randomevent.util

import me.abhigya.randomevent.RandomEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Util {

    private val key = NamespacedKey(RandomEvent.instance, "custom")
    val bamboozledPotato = ItemStack(Material.POTATO).apply {
        itemMeta = itemMeta?.apply {
            displayName(MiniMessage.miniMessage().deserialize("<rainbow>Bamboozled Potato"))
            lore(
                listOf(
                    Component.text("What did you think, is it that easy?", NamedTextColor.AQUA, TextDecoration.ITALIC),
                    Component.text("It's still out there somewhere, keep searching.")
                )
            )
        }
    }

    fun isCustomDiamond(item: ItemStack?): Boolean {
        return item != null && item.type == Material.DIAMOND && item.itemMeta?.persistentDataContainer?.has(key) == true
    }

    fun setCustomDiamond(item: ItemStack) {
        item.editMeta {
            it.persistentDataContainer.set(key, PersistentDataType.BYTE, 1)
        }
    }

    fun randomCircleVector(radius: Int, point: Vector): Vector {
        val circ = Math.random() * 2 * Math.PI
        val r = radius * sqrt(Math.random())
        val x = r * cos(circ)
        val z = r * sin(circ)
        return Vector(x + point.x, point.y, z + point.y)
    }

}