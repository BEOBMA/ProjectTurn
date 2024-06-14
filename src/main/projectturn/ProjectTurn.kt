package org.beobma.projectturn

import org.beobma.projectturn.command.Commando
import org.beobma.projectturn.game.Event
import org.beobma.projectturn.info.Setup
import org.bukkit.plugin.java.JavaPlugin

class ProjectTurn : JavaPlugin() {
    companion object {
        lateinit var instance: ProjectTurn
    }

    override fun onEnable() {
        server.getPluginCommand("pt")?.setExecutor(Commando())
        server.pluginManager.registerEvents(Commando(), this)
        server.pluginManager.registerEvents(Event(), this)
        instance = this
        Setup().setUp()

        logger.info("ProjectTurn Enable")
    }

    override fun onDisable() {
        logger.info("ProjectTurn Disable")
    }
}
