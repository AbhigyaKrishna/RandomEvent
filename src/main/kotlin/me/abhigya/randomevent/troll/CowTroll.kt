package me.abhigya.randomevent.troll

import me.libraryaddict.disguise.disguisetypes.DisguiseType
import me.libraryaddict.disguise.disguisetypes.MobDisguise
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack

class CowTroll : RandomTrollList<PlayerInteractAtEntityEvent>() {

    private val turnCowToChicken = Troll<PlayerInteractAtEntityEvent> ("Turn Cow To Chicken", 50) {
        val location = it.rightClicked.location
        it.rightClicked.remove()
        val zombie = it.rightClicked.world.spawnEntity(location, EntityType.ZOMBIE) as Zombie
        val chickenDisguise = MobDisguise(DisguiseType.CHICKEN)
        zombie.equipment.helmet = ItemStack(Material.ACACIA_BUTTON)
        chickenDisguise.entity = zombie
        chickenDisguise.startDisguise()
        zombie.customName(MiniMessage.miniMessage().deserialize("<rainbow>Chicken The Chad"))
    }

    private val doNothing = Troll<PlayerInteractAtEntityEvent> ("Do Nothing", 50) {
        it.isCancelled = false
    }

    override fun list(): List<Troll<PlayerInteractAtEntityEvent>> {
        return listOf(turnCowToChicken, doNothing)
    }
}