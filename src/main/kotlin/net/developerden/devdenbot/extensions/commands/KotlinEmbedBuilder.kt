package net.developerden.devdenbot.extensions.commands

import net.developerden.devdenbot.trait.HasConfig
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.time.LocalDateTime

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

    fun timestampNow() = setTimestamp(LocalDateTime.now())

    fun field(name: String?, value: String = " ", inline: Boolean = false): EmbedBuilder {
        return addField(name, value, inline)
    }

    private fun writeOnly(): Nothing = throw UnsupportedOperationException("Write-only var")
}

inline fun embed(body: KotlinEmbedBuilder.() -> Unit) = KotlinEmbedBuilder().apply(body).build()

inline fun HasConfig.embedDefaults(body: KotlinEmbedBuilder.() -> Unit) = embed {
    timestampNow()
    color = Color(ddbConfig.colour)
    authorImage = "https://developerden.net/logo.png"
    body()
}
