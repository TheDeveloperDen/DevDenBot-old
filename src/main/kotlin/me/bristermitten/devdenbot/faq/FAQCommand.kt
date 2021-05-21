package me.bristermitten.devdenbot.faq

import com.google.inject.Inject
import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.info.InfoCategory
import me.bristermitten.devdenbot.commands.requireLength
import me.bristermitten.devdenbot.commands.requireLengthAtLeast
import me.bristermitten.devdenbot.extensions.arguments
import me.bristermitten.devdenbot.extensions.commands.embed
import me.bristermitten.devdenbot.extensions.commands.tempReply
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.isModerator
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.awt.Color

@Used
class FAQCommand @Inject constructor(val ddbConfig: DDBConfig) : DevDenCommand(
    name = "faq",
    help = "View or set FAQ topics",
    arguments = "<set> <name> <title> <content> | <name> | <delete> <name>",
    category = InfoCategory
) {
    override suspend fun CommandEvent.execute() {

        val args = arguments()
        val arguments = args.args
        args.requireLengthAtLeast(this@FAQCommand, 1)
        when (arguments.size) {
            1 -> replyFAQ(arguments.first().content)
            else -> {
                if (!isModerator(member)) {
                    tempReply("**No permission.**", 5)
                    return
                }
                when (arguments[1].content) {
                    "set" -> {
                        args.requireLength(this@FAQCommand, 4)
                        val name = arguments[2].content
                        val title = arguments[3].content
                        val content = arguments.drop(3).joinToString(" ") { it.content }
                        return createOrUpdateFAQ(name, title, content)
                    }
                    "delete" -> {
                        args.requireLength(this@FAQCommand, 2)
                        val name = arguments[2].content
                        return deleteFAQ(name)
                    }
                    else -> reply("Unknown subcommand ${arguments[1].content}")
                }
            }
        }
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
            reply("**Unknown FAQ `$name`.**")
            return
        }

        reply(embed {
            timestampNow()
            color = Color(ddbConfig.colour)
            author = "FAQ Answer"
            authorImage = "https://developerden.net/logo.png"
            title = faq.title
            description = faq.content
            setFooter("Requested by ${member.asMention} | $name", member.user.avatarUrl)
        })
    }
}
