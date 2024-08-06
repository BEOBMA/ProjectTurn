package org.beobma.projectturn.info

import org.beobma.projectturn.game.Game
import org.bukkit.Bukkit
import org.bukkit.World

class Info {
    companion object {
        var game: Game? = null
        var starting: Boolean = false
        var gaming: Boolean = false
        var gameInfo: GameInfoType = GameInfoType.IsNot
        val world: World = Bukkit.getWorld("world")!!
    }

    fun isStarting(): Boolean {
        return starting
    }

    fun isGaming(): Boolean {
        return gaming
    }

    fun getGame(): Game? {
        return game
    }

    fun getGameInfo(): GameInfoType {
        return gameInfo
    }

    fun setGameInfo(p1: GameInfoType) {
        gameInfo = p1
    }
}

enum class GameInfoType {
    IsEvent, IsBattle, IsHardBattle, IsShop, IsRest, IsBoss, IsNot
}
