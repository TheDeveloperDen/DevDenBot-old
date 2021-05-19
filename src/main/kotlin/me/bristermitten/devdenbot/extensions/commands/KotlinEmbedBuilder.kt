package me.bristermitten.devdenbot.extensions.commands

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

/**
 * @author Alexander Wood (BristerMitten)
 */
@Suppress("MemberVisibilityCanBePrivate")
class KotlinEmbedBuilder : EmbedBuilder() {

    var title: String? = null
        set(value) {
            setTitle(value, url)
            field = value
        }

    var author: String? = null
        set(value) {
            setAuthor(value, authorUrl)
            field = value
        }

    var authorUrl: String? = null
        set(value) {
            setAuthor(author, value, authorImage)
            field = value
        }

    var authorImage: String? = null
        set(value) {
            setAuthor(author, authorUrl, value)
            field = value
        }

    var url: String? = null
        set(value) {
            setTitle(title, value)
            field = value
        }

    var thumbnail: String?
        get() = writeOnly()
        set(value) {
            setThumbnail(value)
        }


    var description: String?
        get() = writeOnly()
        set(value) {
            setDescription(value)
        }

    var footer: String?
        get() = writeOnly()
        set(value) {
            setFooter(value)
        }

    var color: Color?
        get() = writeOnly()
        set(value) {
            setColor(value)
        }

    fun field(name: String?, value: String = " ", inline: Boolean = false): EmbedBuilder {
        return addField(name, value, inline)
    }

    private fun writeOnly(): Nothing = throw UnsupportedOperationException("Write-only var")
}

