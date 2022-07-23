package me.abhigya.randomevent.util

import me.abhigya.randomevent.RandomEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
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
    val hintBook = ItemStack(Material.WRITTEN_BOOK).apply {
        itemMeta = (itemMeta as BookMeta).apply {
            author = "The Game Masters"
            title = "Unknown Message..."
            addPages(
                MiniMessage.miniMessage().deserialize(
                    "<rainbow>Hello Adventurer!! \\n\" +\n" +
                        "We are the GameMasters as thee might have guessed. \\n\" +\n" +
                        "We welcome thee to this arena of proving where souls from around \n" +
                        "creation come prove their worth and claim the mantle of The Lord."
                ),
                MiniMessage.miniMessage().deserialize(
                    "<rainbow>Be prepared for thee art not alone. There shall be mayhem so we advice thee, BE PREPARED \n" +
                            "Best of Luck in thy endeavours, May thee bring good tidings.\n\n\n" +
                            "Check next page for details of thy task."
                ),
                MiniMessage.miniMessage().deserialize(
                    "Your main task is to THE DIAMOND\n\n" +
                            "<red>1> <blue>Diamonds are extremely valuable resources \n" +
                            "<red>2> <blue>Diamonds were used in various applications in the old days. Such as creating tools of War and Craft.\n"
                ),
                MiniMessage.miniMessage().deserialize(
                    "<red>3> <blue>Counterfeit Diamonds do exist in this world, be weary. \n" +
                            "<red>4> <blue>Diamonds are said to be the hardest minerals in the world. \n" +
                            "<red>5> <blue>Diamonds were used to cut glass in the old days due to their density."
                ),
                MiniMessage.miniMessage().deserialize(
                    "<red>6> <blue>The purest form of Diamonds present in this world are BLUE in colour. \n" +
                            "<red>7> <blue>Diamonds tend to have very high melting points, which while being hard achieve are not unachievable."
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