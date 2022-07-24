package me.abhigya.randomevent

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.abhigya.randomevent.event.EventWinEvent
import me.abhigya.randomevent.util.DiamondLocatorTask
import me.abhigya.randomevent.util.LocationSerializer
import me.abhigya.randomevent.util.Util
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Firework
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.BoundingBox
import java.nio.file.Files
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path

class ChaseSequence(private val plugin: RandomEvent) : Listener {

    var radius = 0
    var started = false
    var ended = false
    var chestLocation: Location? = null
    var diamondOwner: Player? = null
    var parkourBox: BoundingBox? = null
    val spawnLocations = HashMap<UUID, Location>()
    var currentBossbar: BossBar? = null
    var currentActionBar: DiamondLocatorTask? = null

    fun scheduleStart() {
        val listener = InvListener()
        plugin.server.pluginManager.registerEvents(listener, plugin)

        radius = plugin.config!!.getInt("radius")
        val bossbar = BossBar.bossBar(
            Component.text("PvP starting in ", NamedTextColor.YELLOW)
                .append(Component.text("10 seconds...", NamedTextColor.RED)),
            1.0f,
            BossBar.Color.PURPLE,
            BossBar.Overlay.NOTCHED_10,
            setOf(BossBar.Flag.PLAY_BOSS_MUSIC)
        )

        for (player in Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossbar)
            player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1.0f, 1.0f))
        }

        val ran = AtomicInteger(10)
        plugin.server.scheduler.runTaskTimer(plugin, { task ->
            if (ran.getAndDecrement() == 1) {
                task.cancel()
                for (player in Bukkit.getOnlinePlayers()) {
                    player.hideBossBar(bossbar)
                }
                HandlerList.unregisterAll(listener)
                init()
                return@runTaskTimer
            }

            bossbar.name(Component.text("PvP starting in ", NamedTextColor.YELLOW)
                .append(Component.text("${ran.get()} ", NamedTextColor.RED))
                .append(Component.text("seconds...", NamedTextColor.YELLOW)))
            bossbar.progress(ran.get() / 10.0f)
            for (player in Bukkit.getOnlinePlayers()) {
                player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1.0f, 1.0f))
            }

        }, 20L, 20L)
    }

    fun init() {
        started = true
        plugin.server.pluginManager.registerEvents(this, plugin)
        for (player in Bukkit.getOnlinePlayers()) {
            teleportInArena(player)
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 0))
            for (i in 0 until player.inventory.size) {
                val itemStack = player.inventory.getItem(i)
                if (itemStack != null && itemStack.type == Material.DIAMOND && !Util.isCustomDiamond(itemStack)) {
                    player.inventory.setItem(i, Util.bamboozledPotato)
                } else if (Util.isCustomDiamond(itemStack)) {
                    diamondOwner = player
                }
            }
            player.inventory.addItem(ItemStack(Material.COOKED_BEEF, 16))
            player.inventory.addItem(ItemStack(Material.STONE_SWORD, 1))
        }
        applyOwnerPotionEffects(diamondOwner!!)

        chestLocation = LocationSerializer(plugin.config!!, "submit-location").toLocation()
        parkourBox = BoundingBox.of(LocationSerializer(plugin.config!!, "parkour1").toLocation().toVector(), LocationSerializer(plugin.config!!, "parkour2").toLocation().toVector())

        val totalTime = plugin.config!!.getInt("arena-timer")
        val time = AtomicInteger(totalTime)
        currentBossbar = BossBar.bossBar(
            Component.text("Next Phase in", NamedTextColor.YELLOW)
                .append(Component.text("${time.get() / 60} minutes", NamedTextColor.RED)),
            1.0f,
            BossBar.Color.PURPLE,
            BossBar.Overlay.NOTCHED_10,
            setOf(BossBar.Flag.PLAY_BOSS_MUSIC)
        )
        for (player in Bukkit.getOnlinePlayers()) {
            player.showBossBar(currentBossbar!!)
        }
        plugin.server.scheduler.runTaskTimer(plugin, { task ->
            if (time.decrementAndGet() == 0) {
                task.cancel()
                spawnTnt()
                val title = Title.title(
                    Component.text("A door opened somewhere!", NamedTextColor.BLUE),
                    Component.text("Get to next zone!", NamedTextColor.YELLOW)
                )
                for (player in Bukkit.getOnlinePlayers()) {
                    player.hideBossBar(currentBossbar!!)
                    player.showTitle(title)
                }
                currentBossbar = null
                return@runTaskTimer
            }
            var timeFormat = "${time.get() / 60} minutes ${time.get() % 60} seconds"
            if (time.get() % 60 == 0) {
                timeFormat = "${time.get() / 60} minutes"
            } else if (time.get() < 60) {
                timeFormat = "${time.get()} seconds"
            }
            currentBossbar!!.name(Component.text("Next Phase in ", NamedTextColor.YELLOW)
                .append(Component.text(timeFormat, NamedTextColor.RED)))
            currentBossbar!!.progress(time.get() / totalTime.toFloat())
        }, 20L, 20L)
    }

    class InvListener : Listener {

        @EventHandler
        fun handlePlayerThrow(event: PlayerDropItemEvent) {
            if (Util.isCustomDiamond(event.itemDrop.itemStack)) {
                event.isCancelled = true
            }
        }

    }

    fun spawnTnt() {
        val loc1 = LocationSerializer(plugin.config!!, "tnt1").toLocation()
        val loc2 = LocationSerializer(plugin.config!!, "tnt2").toLocation()
        val loc3 = LocationSerializer(plugin.config!!, "tnt-spawn").toLocation()
        val box = BoundingBox.of(loc1.toVector(), loc2.toVector())
        loc3.world.spawn(loc3, TNTPrimed::class.java).apply {
            fuseTicks = 20
        }
        plugin.server.scheduler.runTaskLater(plugin, { task ->
            for (x in box.minX.toInt() until box.maxX.toInt() + 1) {
                for (y in box.minY.toInt() until box.maxY.toInt() + 1) {
                    for (z in box.minZ.toInt() until box.maxZ.toInt() + 1) {
                        loc3.world.getBlockAt(x, y, z).breakNaturally()
                    }
                }
            }
        }, 20)
    }

    private fun end(winner: Player) {
        if (ended) return
        ended = true
        plugin.server.pluginManager.callEvent(EventWinEvent(winner))
        HandlerList.unregisterAll(this)
        val title = Title.title(MiniMessage.miniMessage().deserialize("<yellow>THEE ART THE CHAMPION!"),
            MiniMessage.miniMessage().deserialize("Congratulations!! Thee has proven thy self in this land of imagination."))
        winner.showTitle(title)
        val allTitle = Title.title(Component.text("${winner.name} ", NamedTextColor.AQUA)
            .append(Component.text("is the champion!", NamedTextColor.YELLOW)),
            Component.text("Better luck next time!", NamedTextColor.GREEN)
        )
        for (player in Bukkit.getOnlinePlayers()) {
            if (player != winner) {
                player.gameMode = GameMode.SPECTATOR
                player.showTitle(allTitle)
            }
        }

        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, { _ ->
            runSong()
        }, 100)

        var i = 0
        while (i <= 20) {
            winner.world.spawn(Util.randomCircleVector(3, winner.location.toVector()).toLocation(winner.world), Firework::class.java).apply {
                ticksToDetonate = 40
            }
            i++
        }
    }

    fun runSong() {
        val fn = {
            val lines = Files.readAllLines(Path("./plugins/RandomEvent/song.txt"))
            val cursor = AtomicInteger(0)
            val sound = Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 5.0f, 1.0f)
            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, { task ->
                if (cursor.get() >= lines.size || Bukkit.getOnlinePlayers().isEmpty()) {
                    task.cancel()
                    return@runTaskTimerAsynchronously
                }
                val title = Title.title(
                    Component.text(if (cursor.get() % 2 == 0) "( ͡° ͜ʖ ͡°)╭∩╮" else "( ͡° ͜ʖ ͡°)", NamedTextColor.GREEN),
                    Component.text(lines[cursor.get()], NamedTextColor.YELLOW),
                    Times.times(
                        Ticks.duration(10L),
                        Ticks.duration(20L),
                        Ticks.duration(10L),
                    )
                )
                for (player in Bukkit.getOnlinePlayers()) {
                    player.showTitle(title)
                    player.playSound(sound)
                }
                cursor.incrementAndGet()
            }, 0L, 40L)
        }

        if (Bukkit.isPrimaryThread()) {
            plugin.server.scheduler.runTaskAsynchronously(plugin, fn)
        } else {
            fn()
        }
    }

    private fun applyOwnerPotionEffects(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        if (spawnLocations.containsKey(player.uniqueId)) return
        player.addPotionEffects(listOf(
            PotionEffect(PotionEffectType.HEALTH_BOOST, Int.MAX_VALUE, 2),
            PotionEffect(PotionEffectType.INCREASE_DAMAGE, Int.MAX_VALUE, 0),
            PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0),
            PotionEffect(PotionEffectType.REGENERATION, 200, 1)
        ))
    }

    private fun removeOwnerPotionEffects(player: Player) {
        player.removePotionEffect(PotionEffectType.HEALTH_BOOST)
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
    }

    private fun teleportInArena(player: Player) {
        val arenaLocation = LocationSerializer(plugin.config!!, "arena-location").toLocation()
//        val maxY = plugin.config!!.getInt("max-arena-y", 300)
//        var loc: Location
//        do {
//            loc = Util.randomCircleVector(radius, arenaLocation.toVector()).toLocation(arenaLocation.world).apply {
//                y = arenaLocation.world.getHighestBlockAt(x.toInt(), z.toInt()).y + 1.0
//            }
//            println(loc.y)
//        } while (loc.y > maxY)
        player.teleport(arenaLocation)
    }

    private fun sendDiamondLocation(item: Item) {
        currentActionBar = DiamondLocatorTask(item)
        currentActionBar!!.runTaskTimer(plugin, 0L, 20L)
    }

    @EventHandler
    fun handleMove(event: PlayerMoveEvent) {
        if (spawnLocations.contains(event.player.uniqueId)) return
        if (parkourBox!!.contains(event.from.toVector())) {
            spawnLocations[event.player.uniqueId] = LocationSerializer(plugin.config!!, "parkour-spawn").toLocation()
            event.player.removePotionEffect(PotionEffectType.SPEED)
            removeOwnerPotionEffects(event.player)
        }
    }

    @EventHandler
    fun handleRespawn(event: PlayerPostRespawnEvent) {
        if (!spawnLocations.containsKey(event.player.uniqueId)) {
            event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0))
        }
        if (spawnLocations.containsKey(event.player.uniqueId)) {
            event.player.teleport(spawnLocations[event.player.uniqueId]!!)
        } else {
            teleportInArena(event.player)
        }
    }

    @EventHandler
    fun handleDeath(event: PlayerDeathEvent) {
        if (event.player == diamondOwner) {
            for (drop in event.drops) {
                if (Util.isCustomDiamond(drop)) {
                    event.drops.remove(drop)
                    val item = event.player.world.dropItemNaturally(event.player.location, drop)
                    sendDiamondLocation(item)
                    break
                }
            }
        }
    }

    @EventHandler
    fun handlePickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        if (Util.isCustomDiamond(event.item.itemStack)) {
            diamondOwner = event.entity as Player
            if (spawnLocations.containsKey(diamondOwner!!.uniqueId)) return
            applyOwnerPotionEffects(diamondOwner!!)
            if (currentActionBar != null) {
                currentActionBar!!.cancel()
                currentActionBar = null
            }
        }
    }

    @EventHandler
    fun handleDrop(event: PlayerDropItemEvent) {
        if (Util.isCustomDiamond(event.itemDrop.itemStack)) {
            diamondOwner = null
            removeOwnerPotionEffects(event.player)
            if (spawnLocations.containsKey(event.player.uniqueId)) return
            event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 0))
            sendDiamondLocation(event.itemDrop)
        }
    }

    @EventHandler
    fun handlePlayerLeave(event: PlayerQuitEvent) {
        if (event.player != diamondOwner) return
        for (itemStack in event.player.inventory) {
            if (!Util.isCustomDiamond(itemStack)) continue
            event.player.inventory.remove(itemStack)
            val dropItem = event.player.location.world.dropItemNaturally(event.player.location, itemStack)
            sendDiamondLocation(dropItem)
            break
        }
        removeOwnerPotionEffects(event.player)
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        if (currentBossbar != null) event.player.showBossBar(currentBossbar!!)
        if (spawnLocations.containsKey(event.player.uniqueId)) return
        event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 0))
    }

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        event.blockList().clear()
    }

    @EventHandler
    fun handleBlockExplode(event: BlockExplodeEvent) {
        event.blockList().clear()
    }

    @EventHandler
    fun handleItemBurn(event: EntityDamageEvent) {
        if (event.entity !is Item) return
        if (!Util.isCustomDiamond((event.entity as Item).itemStack)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleInteract(event: PlayerInteractEvent) {
        if (!event.hasBlock()) return
        if (event.action == Action.PHYSICAL) {
            if (event.clickedBlock?.type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                val loc = LocationSerializer(plugin.config!!, "trap1").toLocation()
                if (event.clickedBlock!!.location == loc) {
                    event.player.velocity = event.player.location.direction.multiply(2)
                }
            }
            return
        }
        if (chestLocation == null) return
        if (event.clickedBlock?.type != Material.ENDER_CHEST) return
        if (event.clickedBlock?.location != chestLocation) return
        event.isCancelled = true
        if (!Util.isCustomDiamond(event.player.inventory.itemInMainHand)) return
        event.player.inventory.remove(event.player.inventory.itemInMainHand)
        end(event.player)
    }

}