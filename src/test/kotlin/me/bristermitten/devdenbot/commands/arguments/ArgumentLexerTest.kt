package me.bristermitten.devdenbot.commands.arguments

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ArgumentLexerTest {

    @Test
    fun lex() {
        val tokens = ArgumentLexer().lex("""
            command name -flag "quoted value"
        """.trimIndent())
        println(tokens)
    }
}
