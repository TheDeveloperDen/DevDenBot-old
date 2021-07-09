package net.developerden.devdenbot.util

import kotlin.test.*

internal class StringSimilarityUtilsTest {

    private val testRoles = listOf("(she/her)", "(he/him)", "(they/them)", "Clash of Code", "Bump Notifications")

    @Test
    fun `test getSuggestion returns closest suggestion`() {
        val input = "he/him"
        val allowedValues = testRoles
        val suggestion = getSuggestion(input, allowedValues)
        assertNotNull(suggestion)
        assertEquals("(he/him)", suggestion)
    }

    @Test
    fun `test getSuggestion her returns she her`() {
        val input = "her"
        val allowedValues = testRoles
        val suggestion = getSuggestion(input, allowedValues)
        assertNotNull(suggestion)
        assertEquals("(she/her)", suggestion)
    }

    @Test
    fun `test getSuggestion returns case insensitive suggestion`() {
        val input = "cLASH OF cODY"
        val allowedValues = testRoles
        val suggestion = getSuggestion(input, allowedValues)
        assertNotNull(suggestion)
        assertEquals("Clash of Code", suggestion)
    }

    @Test
    fun `test getSuggestion none match returns null`() {
        val input = "someInput"
        val allowedValues = testRoles
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
        val allowedValues = testRoles
        val suggestion = getSuggestion(input, allowedValues)
        assertNull(suggestion)
    }
}
