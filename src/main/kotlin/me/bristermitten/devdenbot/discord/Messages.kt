package me.bristermitten.devdenbot.discord

import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message

suspend fun Message.fetchMember(): Member = member ?: guild.retrieveMemberById(author.id).await()
