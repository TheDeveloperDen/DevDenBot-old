package me.bristermitten.devdenbot.util

import me.bristermitten.devdenbot.data.AtomicBigInteger
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

fun Int.atomic() = AtomicInteger(this)
fun BigInteger.atomic() = AtomicBigInteger(this)

operator fun AtomicInteger.inc() = AtomicInteger(incrementAndGet())

operator fun AtomicInteger.dec() = AtomicInteger(decrementAndGet())
