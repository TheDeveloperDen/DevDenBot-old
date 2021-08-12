package net.developerden.devdenbot.commands.arguments

object ArgumentLexer {
    fun lex(message: String): List<Token> {
        val tokenTypes = message.map {
            it to when (it) {
                '"' -> TokenType.QUOTE
                '-' -> TokenType.DASH
                ' ' -> TokenType.SPACE
                else -> TokenType.NORMAL
            }
        } + (0.toChar() to TokenType.EOF)

        val tokens = mutableListOf<Token>()
        var previous = TokenType.EOF
        val buffer = StringBuilder()
        for ((char, tokenType) in tokenTypes) {
            if (tokenType == previous) {
                buffer.append(char)
            } else if (tokenType == TokenType.DASH && previous != TokenType.SPACE) {
                buffer.append(char) // flags MUST be separated by a space
            } else {
                if (previous != TokenType.EOF && previous != TokenType.SPACE) {
                    tokens.add(Token(previous, buffer.toString()))
                }
                buffer.clear()
                previous = tokenType
                buffer.append(char)
            }
        }
        return tokens
    }
}
