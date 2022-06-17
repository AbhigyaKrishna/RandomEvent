package me.abhigya.randomevent.troll

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Silverfish
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class DiamondOreTroll {

    private var ID = 488272
    private val POTION_DEBUF = arrayListOf<PotionEffectType>(
        PotionEffectType.BLINDNESS,
        PotionEffectType.CONFUSION,
        PotionEffectType.HUNGER,
        PotionEffectType.LEVITATION,
        PotionEffectType.POISON,
        PotionEffectType.SLOW,
        PotionEffectType.WEAKNESS,
        PotionEffectType.WITHER
    )


    val fakeDiamond  = Troll<BlockBreakEvent>("Fake Diamond", 20) {
        val id = ID++;
        val spawnPacket = WrapperPlayServerSpawnEntity(
            id,
            Optional.empty(),
            EntityTypes.ITEM,
            Vector3d(it.block.x.toDouble(), it.block.y.toDouble(), it.block.z.toDouble()),
            0.0F,
            0.0F,
            0.0F,
            0,
            Optional.empty()
        )
        val metaDataPacket = WrapperPlayServerEntityMetadata(id, mutableListOf(
            EntityData(8, EntityDataTypes.ITEMSTACK, ItemStack.builder().type(ItemTypes.DIAMOND).amount(1).build())
        ))

        PacketEvents.getAPI().playerManager.sendPacket(it.player, spawnPacket)
        PacketEvents.getAPI().playerManager.sendPacket(it.player, metaDataPacket)
    }

    val tnt = Troll<BlockBreakEvent>("TNT", 10) {
        it.block.location.world?.spawnEntity(it.block.location, EntityType.PRIMED_TNT).let { primedTnt -> primedTnt as TNTPrimed
            primedTnt.fuseTicks = 20
        }
    }

    val lavaPool = Troll<BlockBreakEvent>("LavaPool", 10) {
        val loc1 = it.block.location.add(3.0, 0.0, 3.0)
        val loc2 = it.block.location.subtract(3.0, 3.0, 3.0)

        val blocks : MutableList<Block> = ArrayList()

        for (x in loc2.blockX..loc1.blockX) {
            for (y in loc2.blockY..loc1.blockY) {
                for (z in loc2.blockZ..loc1.blockZ) {
                    val curr = it.block.world.getBlockAt(x, y, z)
                    if (curr.type == Material.AIR) continue
                    blocks += curr
                }
            }
        }

        for (block in blocks) {
            it.block.world.setType(block.location, Material.LAVA)
        }
    }

    val lagg = Troll<BlockBreakEvent>("Lagg", 25) {
        it.isCancelled = true
    }

    val randomDebuff = Troll<BlockBreakEvent>("RandomDebuff", 10) {
        val random = ThreadLocalRandom.current()
        var chance = 1

        while (chance < 10) {
            it.player.addPotionEffect(PotionEffect(POTION_DEBUF[random.nextInt(POTION_DEBUF.size)], random.nextInt(30, 60) * 20, random.nextInt(1, 6)))
            chance = random.nextInt(100)
        }
    }

    val teleport = Troll<BlockBreakEvent>("Teleport", 20) {
        if (Bukkit.getOnlinePlayers().size <= 1) return@Troll
        var randomPlayer = Bukkit.getOnlinePlayers().random()
        while (randomPlayer == it.player) randomPlayer = Bukkit.getOnlinePlayers().random()
        it.player.teleport(randomPlayer)
        it.block.location.world?.playSound(randomPlayer.location, Sound.EVENT_RAID_HORN, 10f, 1f)
        randomPlayer.sendMessage(MiniMessage.miniMessage().deserialize("<color:#fff700>FIGHT! FIGHT! FIGHT!</color>"))
        it.player.sendMessage(MiniMessage.miniMessage().deserialize("<color:#fff700>FIGHT! FIGHT! FIGHT!</color>"))
    }

    val silverfish = Troll<BlockBreakEvent>("SilverFish", 5) {
        for (x in 1..5) {
            it.block.world.spawnEntity(it.block.location, EntityType.SILVERFISH).let { fish -> fish as Silverfish
                fish.addPotionEffects(listOf(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2),
                    PotionEffect(PotionEffectType.INVISIBILITY, 200, 1),
                    PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1))
                )
                it.block.location.world?.playSound(it.block.location, Sound.ENTITY_WITCH_CELEBRATE, 10f, 1f)
            }
        }
    }

}