package net.developerden.devdenbot.util

import java.util.concurrent.atomic.AtomicInteger

operator fun AtomicInteger.inc() = AtomicInteger(incrementAndGet())

operator fun AtomicInteger.dec() = AtomicInteger(decrementAndGet())
