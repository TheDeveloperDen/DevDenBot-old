package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.data.CharTree
import java.math.BigInteger
import java.util.*
import kotlin.math.log

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

fun wordiness(str: String): Double {
    val split = str.split(" ").toTypedArray()
    return split.sumBy { s -> if (s in words) 1 else 0 } / split.size.toDouble()
}

private val random = SplittableRandom()

fun xpForMessage(message: String): Double {
    val compressibility = compressibility(message)
    val wordiness = wordiness(message)
    return ((1 - compressibility) * 0.7 + wordiness * 0.2) *
            (log(message.length.toDouble(), 1.5) * 2
                    + random.nextInt(0, 3))
}

fun xpForLevel(level: Int): BigInteger = level.toBigDecimal()
    .let { n -> (5 / 3).toBigDecimal() * n.pow(3) + (55 / 2).toBigDecimal() * n.pow(2) + (755 / 6).toBigDecimal() * n }
    .toBigInteger()
