package me.bristermitten.devdenbot.util

import java.math.BigInteger
import java.text.NumberFormat
import java.util.*

fun formatNumber(a: Number): String = NumberFormat.getNumberInstance(Locale.US).format(a)
