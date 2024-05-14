package org.beobma.projectturn.game

import org.beobma.projectturn.game.Field.*
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import kotlin.math.roundToInt

class EnemyManager {
    fun spawnNormalEnemy(field: Field) {
        val game = Info().getGame() ?: return
        when (field) {
            Forest -> {
                val difficultyWeight = when (game.gameDifficulty) {
                    Difficulty.Easy -> {
                        0.75
                    }

                    Difficulty.Normal -> {
                        1.0
                    }

                    Difficulty.Hard -> {
                        1.25
                    }
                }

                val healthWeight = (game.gameStep) * difficultyWeight
                val cowEntity: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.COW)
                val cow = EnemyStats(cowEntity, (25.0 * healthWeight).roundToInt(), (25.0 * healthWeight).roundToInt(),0, 0, 1, mutableListOf(), "Cow", "소")
                val pigEntity: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.PIG)
                val pig = EnemyStats(pigEntity, (20.0 * healthWeight).roundToInt(), (20.0 * healthWeight).roundToInt(),0, 0, 1, mutableListOf(), "Pig", "돼지")
                val chickenEntity: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.CHICKEN)
                val chicken = EnemyStats(chickenEntity, (10.0 * healthWeight).roundToInt(), (10.0 * healthWeight).roundToInt(),0, 0, 1, mutableListOf(), "Chicken", "닭")


                game.gameEnemyStats[cowEntity] = cow
                game.gameEnemy.add(game.gameEnemyStats[cowEntity]!!.enemy)
                game.gameEnemyStats[cowEntity]?.set(cowEntity)

                game.gameEnemyStats[pigEntity] = pig
                game.gameEnemy.add(game.gameEnemyStats[pigEntity]!!.enemy)
                game.gameEnemyStats[pigEntity]?.set(pigEntity)

                game.gameEnemyStats[chickenEntity] = chicken
                game.gameEnemy.add(game.gameEnemyStats[chickenEntity]!!.enemy)
                game.gameEnemyStats[chickenEntity]?.set(chickenEntity)
            }

            Cave -> TODO()
            Sea -> TODO()
        }
    }
    fun spawnHardEnemy(field: Field) {
        val game = Info().getGame() ?: return
        when (field) {
            Forest -> {
                val difficultyWeight = when (game.gameDifficulty) {
                    Difficulty.Easy -> {
                        0.8
                    }

                    Difficulty.Normal -> {
                        1.0
                    }

                    Difficulty.Hard -> {
                        1.2
                    }
                }

                val wolfEntity1: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.WOLF)
                val wolfEntity2: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.WOLF)
                val wolfEntity3: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.WOLF)
                val wolfEntity4: Entity = Bukkit.getWorld("world")!!
                    .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), EntityType.WOLF)
                val wolf1 = EnemyStats(wolfEntity1, (40.0 * difficultyWeight).roundToInt(), (40.0 * difficultyWeight).roundToInt(),0, 0, 2, mutableListOf(), "Wolf", "일반적인 늑대")
                val wolf2 = EnemyStats(wolfEntity2, (40.0 * difficultyWeight).roundToInt(), (40.0 * difficultyWeight).roundToInt(),0, 0, 2, mutableListOf(), "Wolf", "미친 늑대")
                val wolf3 = EnemyStats(wolfEntity3, (40.0 * difficultyWeight).roundToInt(), (40.0 * difficultyWeight).roundToInt(),0, 0, 2, mutableListOf(), "Wolf", "사나운 늑대")
                val wolf4 = EnemyStats(wolfEntity4, (40.0 * difficultyWeight).roundToInt(), (40.0 * difficultyWeight).roundToInt(),0, 0, 2, mutableListOf(), "Wolf", "이상한 늑대")

                game.gameEnemyStats[wolfEntity1] = wolf1
                game.gameEnemyStats[wolfEntity2] = wolf2
                game.gameEnemyStats[wolfEntity3] = wolf3
                game.gameEnemyStats[wolfEntity4] = wolf4
                game.gameEnemy.add(game.gameEnemyStats[wolfEntity1]!!.enemy)
                game.gameEnemy.add(game.gameEnemyStats[wolfEntity2]!!.enemy)
                game.gameEnemy.add(game.gameEnemyStats[wolfEntity3]!!.enemy)
                game.gameEnemy.add(game.gameEnemyStats[wolfEntity4]!!.enemy)
                game.gameEnemyStats[wolfEntity1]?.set(wolfEntity1)
                game.gameEnemyStats[wolfEntity2]?.set(wolfEntity2)
                game.gameEnemyStats[wolfEntity3]?.set(wolfEntity3)
                game.gameEnemyStats[wolfEntity4]?.set(wolfEntity4)
            }

            Cave -> TODO()
            Sea -> TODO()
        }
    }
}