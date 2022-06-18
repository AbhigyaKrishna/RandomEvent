package me.abhigya.randomevent.troll

import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class CowTroll : RandomTrollList<PlayerInteractAtEntityEvent>() {

    private val turnCowToChicken = Troll<PlayerInteractAtEntityEvent> ("Turn Cow To Chicken", 50) {
        val location = it.rightClicked.location
        it.rightClicked.remove()
        val chicken = it.rightClicked.world.spawnEntity(location, EntityType.CHICKEN)
    }

    private val DoNothing = Troll<PlayerInteractAtEntityEvent> ("Do Nothing", 50) {
        it.isCancelled = false
    }

    override fun list(): List<Troll<PlayerInteractAtEntityEvent>> {
        return listOf(turnCowToChicken, DoNothing)
    }
}