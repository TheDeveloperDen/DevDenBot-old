package me.bristermitten.devdenbot.util

import java.util.concurrent.atomic.AtomicInteger

fun Int.atomic() = AtomicInteger(this)

operator fun AtomicInteger.inc() = AtomicInteger(incrementAndGet())

operator fun AtomicInteger.dec() = AtomicInteger(decrementAndGet())
