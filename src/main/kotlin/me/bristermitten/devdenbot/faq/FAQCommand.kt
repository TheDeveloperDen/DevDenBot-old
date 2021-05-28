package me.bristermitten.devdenbot.faq

import com.google.inject.Inject
import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.info.InfoCategory
import me.bristermitten.devdenbot.commands.requireLength
import me.bristermitten.devdenbot.commands.requireLengthAtLeast
import me.bristermitten.devdenbot.extensions.arguments
import me.bristermitten.devdenbot.extensions.commands.embed
import me.bristermitten.devdenbot.extensions.commands.embedDefaults
import me.bristermitten.devdenbot.extensions.commands.tempReply
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.trait.HasConfig
import me.bristermitten.devdenbot.util.isModerator
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.awt.Color

@Used
class FAQCommand @Inject constructor(override val ddbConfig: DDBConfig) : DevDenCommand(
    name = "faq",
    help = "View or set FAQ topics",
    arguments = "<set> <name> <title> <content> | <name> | <delete> <name>",
    category = InfoCategory,
    commandChannelOnly = false
), HasConfig {
    override suspend fun CommandEvent.execute() {

        val args = arguments()
        val arguments = args.args
        args.requireLengthAtLeast(this@FAQCommand, 1)
        when (arguments.size) {
            0 -> replyAllFAQs()
            1 -> replyFAQ(arguments.first().content)
            else -> {
                if (!isModerator(member)) {
                    tempReply("**No permission.**", 5)
                    return
                }
                when (arguments[0].content) {
                    "set" -> {
                        args.requireLength(this@FAQCommand, 4)
                        val name = arguments[1].content
                        val title = arguments[2].content
                        val content = arguments.drop(3).joinToString(" ") { it.content }
                        return createOrUpdateFAQ(name, title, content)
                    }
                    "delete" -> {
                        args.requireLength(this@FAQCommand, 2)
                        val name = arguments[1].content
                        return deleteFAQ(name)
                    }
                    else -> reply("Unknown subcommand ${arguments[0].content}")
                }
            }
        }
    }

    private suspend fun CommandEvent.replyAllFAQs() {
        val faqs = newSuspendedTransaction {
            FAQDAO.all().map { it.toPOJO() }
        }

        val faqNames = faqs.joinToString(", ") { it.name }
        reply(embedDefaults {
            author = "FAQ List"
            description = faqNames
            setFooter("Requested by ${member.user.name}#${member.user.discriminator} | $name", member.user.avatarUrl)
        })
    }

    private suspend fun CommandEvent.createOrUpdateFAQ(name: String, title: String, content: String) =
        newSuspendedTransaction {
            val existingFAQ = FAQDAO.find {
                FAQs.name eq name
            }.firstOrNull()
            if (existingFAQ != null) {
                existingFAQ.title = title
                existingFAQ.content = content
                reply("**Updated FAQ `$name`**")
                return@newSuspendedTransaction
            }
            FAQDAO.new {
                this.name = name
                this.author = member.idLong
                this.title = title
                this.content = content
            }
            reply("**Created new FAQ `$name`**")
        }

    private suspend fun CommandEvent.deleteFAQ(name: String) = newSuspendedTransaction {
        val existingFAQ = FAQDAO.find {
            FAQs.name eq name
        }.firstOrNull() ?: run {
            reply("**Unknown FAQ `$name`**")
            return@newSuspendedTransaction
        }

        existingFAQ.delete()
        reply("**Deleted FAQ `$name`**")
    }

    private suspend fun CommandEvent.replyFAQ(name: String) {
        val faq = newSuspendedTransaction {
            FAQDAO.find {
                FAQs.name eq name
            }.firstOrNull()?.toPOJO()
        }

        if (faq == null) {
            reply("**Unknown FAQ `$name`**")
            return
        }

        reply(embedDefaults {
            author = "FAQ Answer"
            title = faq.title
            description = faq.content
            setFooter("Requested by ${member.user.name}#${member.user.discriminator} | $name", member.user.avatarUrl)
        })
    }
}
