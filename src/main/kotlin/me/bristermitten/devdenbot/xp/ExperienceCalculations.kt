package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.data.CharTree
import java.math.BigInteger
import java.util.*
import kotlin.math.pow
import kotlin.math.tanh

private val words by lazy {
    val words = object {}.javaClass.classLoader.getResourceAsStream("words.txt")
    requireNotNull(words)
    words.bufferedReader().useLines { lines ->
        lines.map(String::toLowerCase).toSet()
    }
}

fun compressibility(str: String): Double {
    val lower = str.toLowerCase()
    val tree = CharTree<Unit>()
    var cut = 0
    var i = 0
    while (i < lower.length) {
        val pair = tree.getFrom(lower, i)
        val diff = (pair.second - 1).coerceAtLeast(0)
        cut += diff
        i += diff
        if (i + pair.second + 1 < lower.length) {
            tree[lower.substring(i, i + pair.second + 1)] = Unit
        }
        i++
    }
    return cut.toDouble() * 2 / lower.length
}

private val punctuationRegex = "[.?,!\\-'`]".toRegex()

fun wordiness(str: String): Double {
    val split = punctuationRegex.replace(str, "") // Strip punctuation to not affect the score
        .split(" ")
    return split.sumBy { s -> if (s in words) s.length + 1 else 0 } / str.length.toDouble()
}

fun xpForMessage(message: String): Double {
    val compressibility = compressibility(message)
    val wordiness = wordiness(message)
    val length = message.length.toDouble()
    return ((1 - compressibility) * 0.7 + wordiness * 0.2) * tanh(length / 20.0) * length.pow(0.7)
}

fun xpForLevel(level: Int): BigInteger = level.toBigDecimal()
    .let { n -> (5 / 3).toBigDecimal() * n.pow(3) + (55 / 2).toBigDecimal() * n.pow(2) + (755 / 6).toBigDecimal() * n }
    .toBigInteger()
