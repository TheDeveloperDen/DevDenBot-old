package me.bristermitten.devdenbot.util

import info.debatty.java.stringsimilarity.SorensenDice
import org.apache.commons.text.similarity.LevenshteinDistance

val levenshtein = LevenshteinDistance.getDefaultInstance()::apply

private const val SUGGESTION_THRESHOLD = .25

private const val k = 2 // number of letters that form a comparison group in the SD similarity algorithm
private val similarity = SorensenDice(k)::similarity

fun getSuggestion(
    input: String,
    allowedValues: List<String>,
    threshold: Double = SUGGESTION_THRESHOLD,
): String? {
    if (input.length <= k) {
        return null
    }

    return allowedValues
        .filter { it.length >= k }
        .map { it to similarity(input.lowercase(), it.lowercase()) }
        .maxByOrNull { (_, distance) -> distance }
        ?.takeIf { (_, distance) -> distance >= threshold }
        ?.first
}
