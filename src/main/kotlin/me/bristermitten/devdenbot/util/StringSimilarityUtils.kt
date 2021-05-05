package me.bristermitten.devdenbot.util

import info.debatty.java.stringsimilarity.SorensenDice
import org.apache.commons.text.similarity.LevenshteinDistance
import java.util.*

val levenshtein = LevenshteinDistance.getDefaultInstance()::apply

private const val SUGGESTION_THRESHOLD = .25

private val similarity = SorensenDice(2)::similarity

fun getSuggestion(
    input: String,
    allowedValues: List<String>,
    threshold: Double = SUGGESTION_THRESHOLD,
): String? {

    if (input.length <= 2){
        return null;
    }

    return allowedValues
        .map { it to similarity(input.toLowerCase(Locale.ROOT), it.toLowerCase(Locale.ROOT)) }
        .maxByOrNull { (_, distance) -> distance }
        ?.takeIf { (_, distance) -> distance >= threshold}
        ?.first
}
