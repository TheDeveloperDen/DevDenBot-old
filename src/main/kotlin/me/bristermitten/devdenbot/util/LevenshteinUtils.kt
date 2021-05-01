package me.bristermitten.devdenbot.util

import net.dv8tion.jda.api.entities.Role
import org.apache.commons.text.similarity.LevenshteinDistance

val levenshtein = LevenshteinDistance.getDefaultInstance()::apply

private const val LEVENSHTEIN_SUGGESTION_THRESHOLD = .33;

fun getSuggestion(
    input: String,
    allowedValues: List<String>,
    threshold: Double = LEVENSHTEIN_SUGGESTION_THRESHOLD
): String? =
    allowedValues
        .map { Pair(it, levenshtein(input, it)) }
        .minByOrNull { it.second }
        .takeIf {
            it?.let { it.second.toDouble() / it.first.length < threshold } == true
        }?.first;