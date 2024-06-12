package org.beobma.projectturn.stats

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.game.StatusEffect
import org.beobma.projectturn.game.StatusEffectType
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.event.DamageEvent
import org.beobma.projectturn.event.DeathEvent
import org.beobma.projectturn.event.HealEvent
import org.beobma.projectturn.game.GameManager
import org.beobma.projectturn.info.Info
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class EnemyStats(
    val enemy: Entity,
    var health: Int,
    var maxHealh: Int,
    var defense: Int,
    var basicPower: Int,
    var relativeSpeed: Int,
    var usingCardList: MutableList<Card> = mutableListOf(),
    var id: String,
    var name: String,
    val statusEffect: MutableList<StatusEffect> = mutableListOf()
) {

    fun addBurn(p1: Int, caster: Player) {
        statusEffect.add(StatusEffect(StatusEffectType.Burn, p1, 1, this.enemy, caster))
    }

    fun getBurn(): Int {
        var i = 0

        statusEffect.forEach { status ->
            if (status.type == StatusEffectType.Burn) {
                i++
            }
        }
        return i
    }

    fun set(entity: Entity) {
        val game = Info().getGame() ?: return
        val enemyStats = this

        entity.apply {
            this.customName =
                "${ChatColor.BOLD}${enemyStats.name} | ${ChatColor.RED}${game.gameEnemyStats[entity]?.health} | ${ChatColor.WHITE}${game.gameEnemyStats[entity]?.defense}"
            this.isCustomNameVisible = true
        }
    }

    fun action() {
        try {
            val clazz = Class.forName("org.beobma.projectturn.enemy.${this.id}")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getDeclaredMethod("action", EnemyStats::class.java)

            method.invoke(instance, this)

            object : BukkitRunnable() {
                override fun run() {
                    GameManager().run {
                        enemy.turnEnd()
                    }
                }
            }.runTaskLater(ProjectTurn.instance, 30L)
        } catch (e: ClassNotFoundException) {
            println("Class not found: $e")
        } catch (e: Exception) {
            println("An error occurred: $e")
        }
    }

    /**
     * 적에게 피해를 입힙니다.
     * @param damage 입힐 피해
     */
    fun damage(damage: Int, player: Player, message: Boolean = true) {
        val game = Info().getGame() ?: return
        var finalDamage = damage

        val playerBasicPower = game.gamePlayerStats[player]?.basicPower ?: 0
        finalDamage += playerBasicPower

        when {
            finalDamage > defense -> {
                if (defense != 0) {
                    Info.world.playSound(player.location, Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F)
                }
                finalDamage -= defense
                defense = 0
            }

            finalDamage == defense -> {
                Info.world.playSound(this.enemy.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                finalDamage = 0
                defense = 0
            }

            else -> {
                Info.world.playSound(this.enemy.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                defense -= finalDamage
                finalDamage = 0
            }
        }

        if (finalDamage <= 0) {
            return
        }

        val damageEvent = DamageEvent(enemy, player, finalDamage, message)
        ProjectTurn.instance.server.pluginManager.callEvent(damageEvent)
        if (damageEvent.isCancelled) {
            return
        }

        finalDamage = damageEvent.damage

        health -= finalDamage
        if (health <= 0) {
            this.death()
        }

        game.gameEnemyStats[this.enemy]?.set(this.enemy)
    }

    /**
     * 적의 체력을 회복시킵니다.
     * @param damage 회복할 체력
     */
    fun heal(damage: Int, entity: Entity) {
        val game = Info().getGame() ?: return
        val healEvent = HealEvent(entity, enemy, damage)
        var finalDamage = damage
        ProjectTurn.instance.server.pluginManager.callEvent(healEvent)
        if (healEvent.isCancelled) {
            return
        }
        finalDamage += healEvent.damage
        health += finalDamage
        if (health > maxHealh) {
            health = maxHealh
        }
        val enemyStats = this

        this.enemy.apply {
            customName =
                "${ChatColor.BOLD}${enemyStats.name} | ${ChatColor.RED}${game.gameEnemyStats[entity]?.health} | ${ChatColor.WHITE}${game.gameEnemyStats[entity]?.defense}"
        }
    }

    /**
     * 적을 처치합니다.
     */
    fun death() {
        val game = Info().getGame() ?: return

        if (this.enemy.isDead) {
            return
        }
        val deathEvent = DeathEvent(enemy)
        ProjectTurn.instance.server.pluginManager.callEvent(deathEvent)
        if (deathEvent.isCancelled) {
            return
        }

        game.countTimer.find { it.entity == this.enemy }?.countEnd()

        game.gameTurnOrder.remove(this.enemy)
        game.gameEnemy.remove(this.enemy)
        game.gameEnemyStats.remove(this.enemy)

        this.enemy.remove()

        if (game.gameEnemy.size < 1) {
            GameManager().battleEnd()
        } else {
            GameManager().enemyLocationReTake()
        }

        try {
            val clazz = Class.forName("org.beobma.projectturn.enemy.${this.id}")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getDeclaredMethod("dead", EnemyStats::class.java)

            method.invoke(instance, this)
        } catch (e: ClassNotFoundException) {
            println("Class not found: $e")
        } catch (e: Exception) {
            println("대상에게 해당 함수가 존재하지 않음: $e")
        }
    }

    /**
     * 방어력을 얻습니다.
     * @param damage 방어력
     */
    fun addDefense(damage: Int) {
        defense += damage

        this.set(enemy)
    }
}