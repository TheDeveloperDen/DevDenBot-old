package me.bristermitten.devdenbot.faq

import me.bristermitten.devdenbot.extensions.commands.embedDefaults
import me.bristermitten.devdenbot.trait.HasConfig
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun HasConfig.displayFAQ(name: String, user: User): MessageEmbed {
    val faq = newSuspendedTransaction {
        FAQDAO.find {
            FAQs.name eq name
        }.firstOrNull()?.toPOJO()
    } ?: return embedDefaults {
        title = "Unknown FAQ"
        description = "Unknown FAQ `$name`"
        setFooter("Requested by ${user.name}#${user.discriminator} | $name", user.avatarUrl)
    }

    return embedDefaults {
        author = "FAQ Answer"
        title = faq.title
        description = faq.content
        setFooter("Requested by ${user.name}#${user.discriminator} | $name", user.avatarUrl)
    }
}
