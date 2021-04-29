package me.bristermitten.devdenbot.extensions

import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author AlexL
 */

suspend fun <T> RestAction<T>.await(): T
{
    return suspendCoroutine { cont ->
        this.queue({ cont.resume(it) }, { cont.resumeWithException(it) })
    }
}
