package me.bristermitten.devdenbot.util

import org.apache.commons.text.similarity.LevenshteinDistance
import info.debatty.java.stringsimilarity.*;

val levenshtein = LevenshteinDistance.getDefaultInstance()::apply

private const val SUGGESTION_THRESHOLD = .25

private val similarity = SorensenDice(2)::similarity

fun getSuggestion(
    input: String,
    allowedValues: List<String>,
    threshold: Double = SUGGESTION_THRESHOLD,
): String? = allowedValues
        .map { it to similarity(input, it) }
        .maxByOrNull { (_, distance) -> distance }
        ?.takeIf { (_, distance) -> distance >= threshold}
        ?.first
