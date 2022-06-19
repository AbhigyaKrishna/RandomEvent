package me.abhigya.randomevent

import me.abhigya.randomevent.troll.CowTroll
import me.abhigya.randomevent.troll.DiamondOreTroll
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Cow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox

class SomeListener : Listener {

    private val chests : MutableMap<Player, BoundingBox> = HashMap()
    private val deadPlayer : MutableList<Player> = ArrayList()
    private val diamondTroll = DiamondOreTroll()
    private val cowTroll = CowTroll()

    private val bamboozledPotato = ItemStack(Material.POTATO).apply {
        itemMeta = itemMeta?.apply {
            displayName(MiniMessage.miniMessage().deserialize("<rainbow>Bamboozled Potato"))
            lore(listOf(Component.text("What you think is it that easy?", NamedTextColor.AQUA,TextDecoration.ITALIC),
            Component.text("It's still out there somewhere, keep searching.")
                ))
        }
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        if (event.block.type == Material.DIAMOND_ORE) {
            event.isDropItems = false

            diamondTroll.randomValue().debugExecute(event) { "Executing ${it.name} for ${event.player.name}" }
        }
    }

    @EventHandler
    fun handlePlayerInteractWithEntity(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked is Cow) {
            event.isCancelled = true
            cowTroll.randomValue().debugExecute(event) { "Executing ${it.name} for ${event.player.name}" }
        }
    }

    @EventHandler
    fun handleChestOpen(event: PlayerInteractEvent) {
        if (!event.hasBlock()) return
        if (event.clickedBlock?.type == Material.CHEST) {
            if ((event.clickedBlock!!.state as Chest).blockInventory.contains(Material.DIAMOND)) {
                val loc1 = event.clickedBlock!!.location.add(10.0, 10.0, 10.0)
                val loc2 = event.clickedBlock!!.location.subtract(10.0, 10.0, 10.0)
                val box = BoundingBox(loc1.x, loc1.y, loc1.z, loc2.x, loc2.y, loc2.z)

                chests[event.player] = box
            }
        }
    }

    @EventHandler
    fun handleMovePlayer(event: PlayerMoveEvent) {
        if (event.player.inventory.contains(Material.DIAMOND)) {
            val box = chests[event.player]
            if (box != null && !box.contains(event.to.toVector())) {
                event.player.inventory.contents.size.downTo(0).forEach {
                    val item = event.player.inventory.getItem(it)
                    if (item?.type == Material.DIAMOND) {
                        event.player.inventory.setItem(it, bamboozledPotato)
                    }
                }

                chests.remove(event.player)
            }
        }
    }

    @EventHandler
    fun handleBlockExplode(event: BlockExplodeEvent) {
        event.blockList().clear()
    }

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        event.blockList().clear()
    }

    @EventHandler
    fun handlePlayerDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return
        if (event.damager !is Player) return
        val remainingHealth = (event.entity as Player).health - event.damage
        if (remainingHealth <= 0) {
            deadPlayer.add(event.entity as Player)
        }
    }

    @EventHandler
    fun  handlePlayerDeath(event: PlayerDeathEvent) {
        if (deadPlayer.contains(event.entity)) {
            event.keepInventory = false
            deadPlayer.remove(event.entity)
        } else {
            event.keepInventory = true
            event.drops.clear()
        }
    }
}