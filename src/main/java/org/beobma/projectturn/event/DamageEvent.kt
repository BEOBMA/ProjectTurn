package org.beobma.projectturn.event

import org.apache.logging.log4j.message.Message
import org.bukkit.entity.Entity
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DamageEvent(val victim: Entity,var attacker: Entity, var damage: Int, val message: Boolean = true) : Event(), Cancellable {
    private var isCancelled = false

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

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