package me.abhigya.randomevent.troll

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Silverfish
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class DiamondOreTroll : RandomTrollList<BlockBreakEvent>() {

    private var ID = 488272
    private val potionDeBuffs = arrayListOf<PotionEffectType>(
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
        val id = ID++
        val spawnPacket = ClientboundAddEntityPacket(
            id,
            UUID.randomUUID(),
            it.block.x.toDouble(),
            it.block.y.toDouble(),
            it.block.z.toDouble(),
            0.0F,
            0.0F,
            net.minecraft.world.entity.EntityType.ITEM,
            0,
            Vec3.ZERO
        )
        val metaDataPacket = ClientboundSetEntityDataPacket(
            id,
            SynchedEntityData(null),
            false
        )
        val field = ClientboundSetEntityDataPacket::class.java.getDeclaredField("b")
        field.isAccessible = true
        field.set(metaDataPacket, listOf(
            SynchedEntityData.DataItem(EntityDataAccessor(8, EntityDataSerializers.ITEM_STACK), ItemStack(Items.DIAMOND))
        ))

        (it.player as CraftPlayer).handle.connection.send(spawnPacket)
        (it.player as CraftPlayer).handle.connection.send(metaDataPacket)
    }

    val tnt = Troll<BlockBreakEvent>("TNT", 10) {
        it.block.location.world?.spawnEntity(it.block.location, EntityType.PRIMED_TNT).let { primedTnt -> primedTnt as TNTPrimed
            primedTnt.fuseTicks = 20
        }
    }

    val lavaPool = Troll<BlockBreakEvent>("LavaPool", 5) {
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

    val lag = Troll<BlockBreakEvent>("Lag", 20) {
        it.isCancelled = true
    }

    val randomDeBuff = Troll<BlockBreakEvent>("RandomDeBuff", 25) {
        val random = ThreadLocalRandom.current()
        var chance = 1

        while (chance < 10) {
            it.player.addPotionEffect(PotionEffect(potionDeBuffs[random.nextInt(potionDeBuffs.size)], random.nextInt(30, 60) * 20, random.nextInt(1, 6)))
            chance = random.nextInt(100)
        }
    }

    val teleport = Troll<BlockBreakEvent>("Teleport", 15) {
        if (Bukkit.getOnlinePlayers().size <= 1) return@Troll
        var randomPlayer = Bukkit.getOnlinePlayers().random()
        while (randomPlayer == it.player) randomPlayer = Bukkit.getOnlinePlayers().random()
        it.player.teleport(randomPlayer)
        it.block.location.world?.playSound(randomPlayer.location, Sound.EVENT_RAID_HORN, 10f, 1f)
        randomPlayer.damage(5.0, it.player)
        it.player.damage(5.0, randomPlayer)
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

    override fun list(): List<Troll<BlockBreakEvent>> {
        return listOf(fakeDiamond, tnt, lavaPool, lag, randomDeBuff, teleport, silverfish)
    }

}
