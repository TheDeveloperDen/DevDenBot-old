package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.util.log
import net.dv8tion.jda.api.JDA
import java.util.*

/**
 * @author AlexL
 */
class VoiceChatXPTask(val jda: JDA) : TimerTask() {

    private val logger by log()

    override fun run() {
        jda.guilds.asSequence()
            .flatMap { guild ->
                guild.voiceChannels.filterNot {
                    it == guild.afkChannel
                }
            }
            .filter { it.members.size > 1 }
            .flatMap { it.members }
            .filterNot { it.user.isBot }
            .filterNot { it.voiceState?.isMuted ?: true }
            .filterNot { it.voiceState?.isDeafened ?: true }
            .forEach {
                val user = StatsUsers[it.idLong]
                val gained = (1..3).random()
                user.giveXP(gained.toBigInteger())
                logger.info {
                    "Gave ${it.user.name} $gained XP for being in voice channel ${it.voiceState?.channel?.name} (${it.voiceState?.channel?.id})"
                }
            }
    }
}
