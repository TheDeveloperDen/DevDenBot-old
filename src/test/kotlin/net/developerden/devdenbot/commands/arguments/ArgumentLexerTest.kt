package net.developerden.devdenbot.commands.arguments

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ArgumentLexerTest {

    @Test
    fun lex() {
        val tokens = ArgumentLexer.lex("""
            command name "quoted value"
        """.trimIndent())
        assertEquals(
            listOf(
                Token(TokenType.NORMAL, "command"),
                Token(TokenType.NORMAL, "name"),
                Token(TokenType.QUOTE, "\""),
                Token(TokenType.NORMAL, "quoted"),
                Token(TokenType.NORMAL, "value"),
                Token(TokenType.QUOTE, "\""),
            ),
            tokens
        )
    }
}
