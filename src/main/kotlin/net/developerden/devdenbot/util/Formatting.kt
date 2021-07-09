package net.developerden.devdenbot.util

import java.text.NumberFormat
import java.util.*

fun formatNumber(a: Number): String = NumberFormat.getNumberInstance(Locale.US).format(a)

fun mention(id: Long): String = "<@$id>"
