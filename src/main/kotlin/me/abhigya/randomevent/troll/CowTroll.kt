package me.abhigya.randomevent.troll

import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class CowTroll : RandomTrollList<PlayerInteractAtEntityEvent>() {

    private val turnCowToChicken = Troll<PlayerInteractAtEntityEvent> ("Turn Cow To Chicken", 50) {
        val location = it.rightClicked.location
        it.rightClicked.remove()
        it.rightClicked.world.spawnEntity(location, EntityType.CHICKEN)
    }

    override fun list(): List<Troll<PlayerInteractAtEntityEvent>> {
        return listOf(turnCowToChicken)
    }
}