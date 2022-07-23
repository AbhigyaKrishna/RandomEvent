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
        val guideBook = ItemStack(Material.BOOK)
        val bookMeta = guideBook.itemMeta as BookMeta

        bookMeta.author = "The Game Masters"
        bookMeta.title = "Unknown Message..."
        bookMeta.page(1, Component.text("Hello Adventurer!! \n" +
                "We are the GameMasters as thee might have guessed. \n" +
                "We welcome thee to this arena of proving where souls from around creation come prove their worth and claim the mantle of The Lord. \n" +
                "Be prepared for thee art not alone. There shall be mayhem so we advice thee, BE PREPARED \n" +
                "Best of Luck in thy endeavours, May thee bring good tidings.\n\n\n" +
                "Check next page for details of thy task."))
        bookMeta.page(2, Component.text("Your main task is to THE DIAMOND\n\n" +
                "1> Diamonds are extremely valuable resources \n" +
                "2> Diamonds were used in various applications in the old days. Such as creating tools of War and Craft.\n" +
                "3> Counterfeit Diamonds do exist in this world, be weary. \n" +
                "4> Diamonds are said to be the hardest minerals in the world. \n" +
                "5> Diamonds were used to cut glass in the old days due to their density. \n" +
                "6> The purest form of Diamonds present in this world are BLUE in colour. \n" +
                "7> Diamonds tend to have very high melting points, which while being hard achieve are not unachievable."))

        guideBook.itemMeta = bookMeta

        if (event.player.hasPlayedBefore()) return

        event.player.inventory.addItem(guideBook)
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

    @EventHandler(ignoreCancelled = true)
    fun handleInventorySwap(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        if (event.view.topInventory.type != InventoryType.FURNACE) return
        val fn = {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player == event.whoClicked) {
                    player.showTitle(Title.title(Component.text("Fine work you have done!!", NamedTextColor.AQUA), Component.text("It is in thy best interests to keep thy possession of the diamond a secret.")))
                }else {
                    player.showTitle(Title.title(Component.text("Someone got the diamond!", NamedTextColor.AQUA),
                        Component.text("Starting PvP in 10 seconds.", NamedTextColor.RED),
                        Title.Times.times(Ticks.duration(10L), Ticks.duration(50L), Ticks.duration(10L))))
                }
            }
            plugin.chaseSequence.scheduleStart(event.whoClicked as Player)
            HandlerList.unregisterAll(this)
        }
        if (event.isShiftClick) {
            if (Util.isCustomDiamond(event.currentItem) &&
                event.whoClicked.inventory.any { it == null || it.type == Material.AIR }) {
                fn()
            }
        } else {
            if (!Util.isCustomDiamond(event.cursor)) return
            if (event.clickedInventory?.type != InventoryType.PLAYER) return
            fn()
        }
    }
}