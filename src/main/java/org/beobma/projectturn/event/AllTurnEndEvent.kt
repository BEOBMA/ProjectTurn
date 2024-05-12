package org.beobma.projectturn.event

import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AllTurnEndEvent : Event() {
    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}