package org.beobma.projectturn.event

import org.beobma.projectturn.card.Card
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class CardUsingEvent(val entity: Entity, val card: Card, val item: ItemStack) : Event() {
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