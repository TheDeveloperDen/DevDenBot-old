package me.bristermitten.devdenbot.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class StringSimilarityUtilsTest {

    private val pronouns = listOf("(she/her)", "(he/him)", "(they/them)")

    @Test
    fun `test getSuggestion returns closest suggestion`() {
        val input = "he/him"
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues)
        assertNotNull(suggestion)
        assertEquals(suggestion, "(he/him)")
    }

    @Test
    fun `test getSuggestion none match returns null`() {
        val input = "someInput"
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues)
        assertNull(suggestion)
    }

    @Test
    fun `test getSuggestion allowed values empty returns null`() {
        val input = "someInput"
        val allowedValues = listOf<String>()
        val suggestion = getSuggestion(input, allowedValues)
        assertNull(suggestion)
    }

    @Test
    fun `test getSuggestion input empty returns null`() {
        val input = ""
        val allowedValues = pronouns
        val suggestion = getSuggestion(input, allowedValues)
        assertNull(suggestion)
    }
}
