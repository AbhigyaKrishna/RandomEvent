package me.abhigya.randomevent

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.abhigya.randomevent.util.Util
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.atomic.AtomicInteger

class ChaseSequence(private val plugin: RandomEvent) : Listener {

    var radius = 0
    var started = false
    var diamondOwner: Player? = null

    fun scheduleStart(diamondHaver: Player) {
        radius = plugin.config!!.getInt("radius")
        plugin.server.scheduler.runTaskLater(plugin, {
            for (player in Bukkit.getOnlinePlayers()) {
                teleportInArena(player)
            }

            started = true
        } as Runnable, 200L)

        val bossbar = BossBar.bossBar(
            Component.text("PvP starting in ", NamedTextColor.YELLOW)
                .append(Component.text("10 ", NamedTextColor.RED))
                .append(Component.text("seconds...", NamedTextColor.YELLOW)),
            1.0f,
            BossBar.Color.PURPLE,
            BossBar.Overlay.NOTCHED_10,
            setOf(BossBar.Flag.PLAY_BOSS_MUSIC)
        )

        for (player in Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossbar)
            player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.NEUTRAL, 1.0f, 1.0f))
        }

        val ran = AtomicInteger(10)
        plugin.server.scheduler.runTaskTimer(plugin, { task ->
            if (ran.getAndDecrement() == 1) {
                task.cancel()
                for (player in Bukkit.getOnlinePlayers()) {
                    player.hideBossBar(bossbar)
                }
                init(diamondHaver)
                return@runTaskTimer
            }

            bossbar.name(Component.text("PvP starting in ", NamedTextColor.YELLOW)
                .append(Component.text("${ran.get()} ", NamedTextColor.RED))
                .append(Component.text("seconds...", NamedTextColor.YELLOW)))
            bossbar.progress(ran.get() / 10.0f)
            for (player in Bukkit.getOnlinePlayers()) {
                player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.NEUTRAL, 1.0f, 1.0f))
            }

        }, 20L, 20L)
    }

    fun init(diamondHaver: Player) {
        diamondOwner = diamondHaver
        plugin.server.pluginManager.registerEvents(this, plugin)
        for (player in Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1))
        }
        applyOwnerPotionEffects(diamondHaver)
    }

    private fun applyOwnerPotionEffects(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        player.addPotionEffects(listOf(
            PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 3),
            PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1),
            PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)
        ))
    }

    private fun teleportInArena(player: Player) {
        player.teleport(Util.randomCircleVector(radius, plugin.arenaLocation!!.toVector()).toLocation(plugin.arenaLocation!!.world).apply {
            y = plugin.arenaLocation!!.world.getHighestBlockAt(x.toInt(), z.toInt()).y + 1.0
        })
    }

    @EventHandler
    fun handleRespawn(event: PlayerPostRespawnEvent) {
        event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1))
        teleportInArena(event.player)
    }

    @EventHandler
    fun handlePickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        if (Util.isCustomDiamond(event.item.itemStack)) {
            diamondOwner = event.entity as Player
            applyOwnerPotionEffects(diamondOwner!!)
        }
    }

    @EventHandler
    fun handleDrop(event: PlayerDropItemEvent) {
        if (Util.isCustomDiamond(event.itemDrop.itemStack)) {
            diamondOwner = null
            event.player.removePotionEffect(PotionEffectType.HEALTH_BOOST)
            event.player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)
            event.player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1))
        }
    }

}