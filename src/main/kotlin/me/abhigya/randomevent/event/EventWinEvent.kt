package me.abhigya.randomevent.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EventWinEvent(
    val winner: Player
) : Event() {

    companion object {
        @JvmStatic val handler = HandlerList()
        @JvmStatic fun getHandlerList() = handler
    }

    override fun getHandlers(): HandlerList = handler

}