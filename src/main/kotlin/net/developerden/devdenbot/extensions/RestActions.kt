package net.developerden.devdenbot.extensions

import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author AlexL
 */

suspend fun <T> RestAction<T>.await(): T = submit().await()
