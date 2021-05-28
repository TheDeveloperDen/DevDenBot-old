package me.bristermitten.devdenbot.extensions

import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author AlexL
 */

suspend fun <T> RestAction<T>.await(failure: (Continuation<T>, Throwable) -> Unit = {
        continuation: Continuation<T>, throwable: Throwable ->
    continuation.resumeWithException(throwable)
}): T {
    return suspendCoroutine { cont ->
        this.queue({ cont.resume(it) }, { failure(cont, it) })
    }
}
