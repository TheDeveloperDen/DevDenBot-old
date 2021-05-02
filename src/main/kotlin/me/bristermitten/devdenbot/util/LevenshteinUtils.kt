package me.bristermitten.devdenbot.util

import org.apache.commons.text.similarity.LevenshteinDistance

val levenshtein = LevenshteinDistance.getDefaultInstance()::apply

private const val LEVENSHTEIN_SUGGESTION_THRESHOLD = .33

fun getSuggestion(
    input: String,
    allowedValues: List<String>,
    threshold: Double = LEVENSHTEIN_SUGGESTION_THRESHOLD,
): String? =
    allowedValues
        .map { it to levenshtein(input, it) }
        .minByOrNull { (_, distance) -> distance }
        ?.takeIf { (value, distance) ->
            distance.toDouble() / value.length < threshold || (value.length / distance.toDouble()) < (1 / threshold)
        }?.first
