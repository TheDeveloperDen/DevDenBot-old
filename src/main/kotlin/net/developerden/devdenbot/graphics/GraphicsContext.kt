package net.developerden.devdenbot.graphics

import java.awt.Font
import java.awt.GraphicsEnvironment

object GraphicsContext {
    private fun loadFont(fontName: String) {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val stream = javaClass.classLoader.getResourceAsStream(fontName)
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream))
    }

    fun init(fonts: List<String> = listOf("Horta.otf")) = fonts.forEach(this::loadFont)
}
