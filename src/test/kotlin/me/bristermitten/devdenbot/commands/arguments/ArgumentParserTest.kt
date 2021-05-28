package me.bristermitten.devdenbot.commands.arguments

import me.bristermitten.devdenbot.extensions.Argument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ArgumentParserTest {

    @Test
    fun parse() {
        val tokens = ArgumentLexer().lex("""
            command name "quoted value"
        """.trimIndent())

        val parsed = ArgumentParser().parse(tokens)

        assertEquals(
            listOf(
                Argument("name"),
                Argument("quoted value"),
            ),
            parsed.args
        )
    }
}
