package me.bristermitten.devdenbot.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LevenshteinUtilsTest {

    private val pronouns = listOf("(she/her)", "(he/him)", "(they/them)")

    @Test
    fun testGetSuggestionReturnsClosestSuggestion() {
        val input = "he/him"
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues);
        assertNotNull(suggestion)
        assertEquals(suggestion, "(he/him)")
    }

    @Test
    fun testGetSuggestionNoneMatchReturnsNull() {
        val input = "someInput"
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues);
        assertNull(suggestion)
    }

    @Test
    fun testAllowedValuesEmptyReturnsNull() {
        val input = "someInput"
        val allowedValues = listOf<String>()
        val suggestion = getSuggestion(input, allowedValues);
        assertNull(suggestion)
    }

    @Test
    fun testInputEmptyReturnsNull() {
        val input = ""
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues);
        assertNull(suggestion)
    }
}
