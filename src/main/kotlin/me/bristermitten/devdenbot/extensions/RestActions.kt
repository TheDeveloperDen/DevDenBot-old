package me.bristermitten.devdenbot.extensions

import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author AlexL
 */

suspend fun <T> RestAction<T>.await(
    failure: (Continuation<T>, Throwable) -> Unit = { continuation, throwable ->
        continuation.resumeWithException(throwable)
    },
): T = suspendCoroutine { cont ->
    this.queue({ cont.resume(it) }, { failure(cont, it) })
}
