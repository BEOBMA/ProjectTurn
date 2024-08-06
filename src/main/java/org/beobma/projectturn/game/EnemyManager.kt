package org.beobma.projectturn.game

import org.beobma.projectturn.game.Field.*
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import kotlin.math.roundToInt
import kotlin.random.Random

class EnemyManager {
    fun spawnNormalEnemy(field: Field) {
        val game = Info().getGame() ?: return
        val difficultyWeight = when (game.gameDifficulty) {
            Difficulty.Easy -> 0.75
            Difficulty.Normal -> 1.0
            Difficulty.Hard -> 1.25
        }
        val healthWeight = (game.gameStep) * difficultyWeight

        fun spawnEntity(entityType: EntityType, name: String, koreanName: String, healthBase: Double) {
            val entity: Entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType)
            val health = (healthBase * healthWeight).roundToInt()
            val enemyStats = EnemyStats(entity, health, health, 0, 0, 1, mutableListOf(), name, koreanName)
            game.gameEnemyStats[entity] = enemyStats
            game.gameEnemy.add(game.gameEnemyStats[entity]!!.enemy)
            game.gameEnemyStats[entity]!!.set(entity)
        }

        when (field) {
            Forest -> {
                spawnEntity(EntityType.COW, "Cow", "소", 25.0)
                spawnEntity(EntityType.PIG, "Pig", "돼지", 20.0)
                spawnEntity(EntityType.CHICKEN, "Chicken", "닭", 10.0)
            }

            Cave -> {
                if (game.gameStep <= 3) {
                    repeat(3) {
                        spawnEntity(EntityType.ZOMBIE, "Zombie", listOf("허기진 좀비", "배고픈 좀비", "일반적인 좀비")[it], 30.0)
                    }
                } else {
                    repeat(3) {
                        spawnEntity(EntityType.CREEPER, "Creeper", listOf("일반적인 크리퍼", "강한 크리퍼", "조용한 크리퍼")[it], 40.0)
                    }
                }
            }

            Sea -> {
                val random = Random.nextInt(1, 3)
                when (random) {
                    1 -> if (game.gameStep <= 3) {
                        repeat(3) {
                            spawnEntity(EntityType.COD, "Cod", listOf("맛있는 대구", "일반적인 대구", "대구")[it], 30.0)
                        }

                    } else {
                        repeat(2) {
                            spawnEntity(EntityType.SALMON, "Salmon", listOf("연어", "헤엄쳐 올라온 연어")[it], 40.0)
                        }
                        repeat(1) {
                            spawnEntity(EntityType.COD, "Cod", listOf("대구")[it], 30.0)
                        }
                    }

                    2 -> if (game.gameStep <= 3) {
                        repeat(3) {
                            spawnEntity(EntityType.COD, "Cod", listOf("맛있는 대구", "일반적인 대구", "대구")[it], 30.0)
                        }

                    } else {
                        repeat(2) {
                            spawnEntity(EntityType.SALMON, "Salmon", listOf("연어", "헤엄쳐 올라온 연어")[it], 40.0)
                        }
                        repeat(1) {
                            spawnEntity(EntityType.DOLPHIN, "Dolphin", listOf("돌고래")[it], 50.0)
                        }
                    }
                }
            }

            End -> TODO()
        }
    }

    fun spawnHardEnemy(field: Field) {
        val game = Info().getGame() ?: return
        val difficultyWeight = when (game.gameDifficulty) {
            Difficulty.Easy -> 0.8
            Difficulty.Normal -> 1.0
            Difficulty.Hard -> 1.2
        }

        fun spawnEntity(entityType: EntityType, name: String, koreanName: String, healthBase: Double, field: Field) {
            val entity: Entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType)
            val health = (healthBase * difficultyWeight).roundToInt()
            val enemyStats = EnemyStats(entity, health, health, 0, 0, 1, mutableListOf(), name, koreanName)
            game.gameEnemyStats[entity] = enemyStats
            game.gameEnemy.add(game.gameEnemyStats[entity]!!.enemy)
            game.gameEnemyStats[entity]!!.set(entity)
        }

        when (field) {
            Forest -> {
                repeat(4) {
                    spawnEntity(
                        EntityType.WOLF,
                        "Wolf",
                        listOf("일반적인 늑대", "미친 늑대", "사나운 늑대", "이상한 늑대")[it],
                        40.0,
                        field
                    )
                }
            }

            Cave -> {
                repeat(2) {
                    spawnEntity(
                        EntityType.SKELETON,
                        "SharpshooterSkeleton",
                        listOf("명사수 스켈레톤", "달인 스켈레톤")[it],
                        50.0,
                        field
                    )
                }
                repeat(2) {
                    spawnEntity(EntityType.SKELETON, "Skeleton", listOf("강력한 스켈레톤", "강한 스켈레톤")[it], 70.0, field)
                }
            }

            Sea -> {
                repeat(1) {
                    spawnEntity(EntityType.SQUID, "Squid", listOf("오징어")[it], 100.0, field)
                }
            }

            End -> TODO()
        }
    }

    fun spawnBossEnemy(field: Field) {
        val game = Info().getGame() ?: return
        val difficultyWeight = when (game.gameDifficulty) {
            Difficulty.Easy -> 1.0
            Difficulty.Normal -> 1.2
            Difficulty.Hard -> 1.4
        }

        fun spawnEntity(entityType: EntityType, name: String, koreanName: String, healthBase: Double) {
            val entity: Entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType)
            val health = (healthBase * difficultyWeight).roundToInt()
            val enemyStats = EnemyStats(entity, health, health, 0, 0, 1, mutableListOf(), name, koreanName)
            game.gameEnemyStats[entity] = enemyStats
            game.gameEnemy.add(game.gameEnemyStats[entity]!!.enemy)
            game.gameEnemyStats[entity]!!.set(entity)
        }

        when (field) {
            Forest -> {
                repeat(1) {
                    spawnEntity(EntityType.SNIFFER, "Sniffer", listOf("스니퍼")[it], 60.0)
                }
            }

            Cave -> {
                repeat(1) {
                    spawnEntity(EntityType.RAVAGER, "Ravager", listOf("동굴 파괴수")[it], 120.0)
                }
            }

            Sea -> {
                repeat(1) {
                    spawnEntity(EntityType.DROWNED, "Drowned", listOf("삼지창에게 선택받은 드라운드")[it], 100.0)
                }
            }

            End -> TODO()
        }
    }

}