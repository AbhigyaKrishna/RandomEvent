package me.abhigya.randomevent

import me.abhigya.randomevent.troll.CowTroll
import me.abhigya.randomevent.troll.DiamondOreTroll
import me.abhigya.randomevent.util.Util
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Cow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.BoundingBox
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class SomeListener(private val plugin: RandomEvent) : Listener {

    private val chests: MutableMap<Player, BoundingBox> = HashMap()
    private val deadPlayer: MutableList<Player> = ArrayList()
    private val diamondTroll = DiamondOreTroll().finalize()
    private val cowTroll = CowTroll().finalize()

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        if (event.block.type == Material.DIAMOND_ORE) {
            event.isDropItems = false

            diamondTroll.randomValue().execute(event)
        }
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        if (event.player.hasPlayedBefore()) return

        event.player.inventory.addItem(Util.hintBook)
    }

    @EventHandler
    fun handlePlayerInteractWithEntity(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked is Cow) {
            event.isCancelled = true
            cowTroll.randomValue().execute(event)
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
                        if (Util.isCustomDiamond(item)) return@forEach
                        event.player.inventory.setItem(it, Util.bamboozledPotato)
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
    fun handlePlayerDeath(event: PlayerDeathEvent) {
        if (deadPlayer.contains(event.entity)) {
            event.keepInventory = false
            deadPlayer.remove(event.entity)
        } else {
            event.keepInventory = true
            event.drops.clear()
        }
    }

    fun nextSequence(player: Player) {
        for (p in Bukkit.getOnlinePlayers()) {
            if (p == player) {
                p.showTitle(Title.title(Component.text("Fine work you have done!!", NamedTextColor.AQUA), Component.text("Now this should go without saying.", NamedTextColor.RED)))
            } else {
                p.showTitle(Title.title(Component.text("Someone got the diamond!", NamedTextColor.AQUA),
                    Component.text("Starting PvP in 10 seconds.", NamedTextColor.RED),
                    Title.Times.times(Ticks.duration(10L), Ticks.duration(50L), Ticks.duration(10L))))
            }
        }
        plugin.chaseSequence.scheduleStart()
        HandlerList.unregisterAll(this)
    }

    @EventHandler(ignoreCancelled = true)
    fun handleInventorySwap(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        if (event.view.topInventory.type != InventoryType.FURNACE) return
        if (event.isShiftClick) {
            if (Util.isCustomDiamond(event.currentItem) &&
                event.whoClicked.inventory.any { it == null || it.type == Material.AIR }) {
                nextSequence(event.whoClicked as Player)
            }
        } else {
            if (!Util.isCustomDiamond(event.cursor)) return
            if (event.clickedInventory?.type != InventoryType.PLAYER) return
            nextSequence(event.whoClicked as Player)
        }
    }

    @EventHandler
    fun handlePickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        if (Util.isCustomDiamond(event.item.itemStack)) {
            nextSequence(event.entity as Player)
        }
    }
}