package net.developerden.devdenbot.extensions

import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.concurrent.Task
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> Task<T>.await(
    failure: (Continuation<T>, Throwable) -> Unit = { continuation, throwable ->
        continuation.resumeWithException(throwable)
    },
): T = suspendCoroutine { cont ->
    this.onSuccess { cont.resume(it) }
    this.onError { failure(cont, it) }

}
