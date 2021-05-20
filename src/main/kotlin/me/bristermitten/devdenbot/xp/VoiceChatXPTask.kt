package me.bristermitten.devdenbot.xp

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import java.util.*

/**
 * @author AlexL
 */
class VoiceChatXPTask(val jda: JDA) : TimerTask() {

    private val log by log()

    @FlowPreview
    override fun run() {
        scope.launch {
            jda.guilds.asFlow()
                .flatMapConcat { guild ->
                    guild.voiceChannels.asFlow().filterNot {
                        it == guild.afkChannel
                    }
                }
                .filter { it.members.size > 1 }
                .flatMapConcat { it.members.asFlow() }
                .filterNot { it.user.isBot }
                .filterNot { it.voiceState?.isMuted ?: true }
                .filterNot { it.voiceState?.isDeafened ?: true }
                .collect {
                    val user = StatsUsers.get(it.idLong)
                    val gained = (1..3).random()
                    user.addXP(gained)
                    log.debug {
                        "Gave ${it.user.name} $gained XP for being in voice channel ${it.voiceState?.channel?.name} (${it.voiceState?.channel?.id})"
                    }
                }
        }
    }
}
