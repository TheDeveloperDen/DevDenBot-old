package me.bristermitten.devdenbot.listener

import net.dv8tion.jda.api.JDA

interface EventListener {
    fun register(jda: JDA)
}
