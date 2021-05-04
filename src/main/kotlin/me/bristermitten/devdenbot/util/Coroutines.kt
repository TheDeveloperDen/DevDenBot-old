package me.bristermitten.devdenbot.util

import club.minnced.jda.reactor.on
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.asFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent

val scope = CoroutineScope(Dispatchers.Default)

inline fun <reified T : GenericEvent> JDA.listenFlow() = on<T>().asFlow()
