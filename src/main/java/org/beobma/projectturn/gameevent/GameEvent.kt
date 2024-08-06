package org.beobma.projectturn.gameevent

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.cardAllList
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class GameEvent(
    val name: String,
    val description: List<String>,
    val id: String
) {
    fun start() {
        try {
            val clazz = Class.forName("org.beobma.projectturn.gameevent.list.${id}")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getDeclaredMethod("start")

            method.invoke(instance)
        } catch (e: ClassNotFoundException) {
            println("Class not found: $e")
        } catch (e: Exception) {
            println("An error occurred: $e")
        }
    }
}
