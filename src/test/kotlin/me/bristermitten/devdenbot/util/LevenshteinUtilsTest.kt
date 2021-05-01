package me.bristermitten.devdenbot.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LevenshteinUtilsTest {

    @Test
    fun testGetSuggestionReturnsClosestSuggestion() {
        val input = "he/him"
        val allowedValues = listOf("(she/her)", "(he/him)", "(they/them)")
        val suggestion = getSuggestion(input, allowedValues);
        assertNotNull(suggestion)
        assertEquals(suggestion, "(he/him)")
    }

    @Test
    fun testGetSuggestionNoneMatchReturnsNull() {
        val input = "someInput"
        val allowedValues = listOf("(she/her)", "(he/him)", "(they/them)")
        val suggestion = getSuggestion(input, allowedValues);
        assertNull(suggestion)
    }
}
