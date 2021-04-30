package me.bristermitten.devdenbot.xp

import java.math.BigInteger

fun xpForLevel(level: Int): BigInteger = level.toBigDecimal()
    .let { n -> (5 / 3).toBigDecimal() * n.pow(3) + (55 / 2).toBigDecimal() * n.pow(2) + (755 / 6).toBigDecimal() * n }
    .toBigInteger()
