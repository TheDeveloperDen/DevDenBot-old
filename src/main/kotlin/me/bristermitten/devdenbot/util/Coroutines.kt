package me.bristermitten.devdenbot.util

import club.minnced.jda.reactor.on
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent

val scope = CoroutineScope(Dispatchers.Default)

inline fun <reified T : GenericEvent> JDA.listenFlow() = on<T>().asFlow()


inline fun <T> Flow<T>.launchEachIn(scope: CoroutineScope, crossinline run: suspend (T) -> Unit) = scope.launch {
    collect {
        scope.launch {
            run(it)
        }
    }
}
