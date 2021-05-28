package me.bristermitten.devdenbot.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ArgumentsTest {

    @Test
    fun `test basic argument parsing`() {
        val args = parseArguments("!", "!cmd hello arg1 arg2")!!
        assertEquals(3, args.args.size)
        assertEquals("cmd", args.command)
        assertEquals(emptyList(), args.flags)
    }

    @Test
    fun `test empty argument parsing`() {
        val args = parseArguments("!", "!cmd")!!
        assertEquals(0, args.args.size)
        assertEquals("cmd", args.command)
        assertEquals(emptyList(), args.flags)
    }

    @Test
    fun `test quoted argument parsing`() {
        val args = parseArguments("!", "!cmd \"hello world\"")!!
        assertEquals(1, args.args.size)
        assertEquals("cmd", args.command)
        assertEquals(emptyList(), args.flags)
        assertEquals(""""hello world"""", args.args.first().content)
    }
}
