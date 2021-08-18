package net.developerden.devdenbot.faq

import com.google.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.selects.select
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.discord.isStaff
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

@Used
class FAQCommand @Inject constructor(override val ddbConfig: DDBConfig) : DevDenSlashCommand(
    name = "faq",
    description = "View or set FAQ topics"
), HasConfig {


    override suspend fun load(action: CommandCreateAction) {
        val nameOption = OptionData(OptionType.STRING, "name", "the name of the faq")


        newSuspendedTransaction {
            FAQDAO.all()
                .map { it.name }
                .forEach { nameOption.addChoices(Command.Choice(it, it)) }
        }

        action.addSubcommands(SubcommandData("list", "Get all FAQs"))

        action.addSubcommands(SubcommandData("get", "Get a FAQ")
            .addOptions(nameOption))

        action.addSubcommands(SubcommandData("delete", "Delete a FAQ")
            .addOptions(nameOption))

        action.addSubcommands(SubcommandData("add", "Add a FAQ")
            .addOptions(OptionData(OptionType.STRING, "name", "The name of the faq"),
                OptionData(OptionType.STRING, "title", "The FAQ Title")))

        action.addSubcommands(SubcommandData("update", "Update a FAQ")
            .addOptions(nameOption,
                OptionData(OptionType.STRING, "title", "The FAQ Title").setRequired(false)))
    }

    override suspend fun SlashCommandEvent.execute() {
        if (subcommandName == "list") {
            return replyAllFAQs()
        }
        if (subcommandName == "get") {
            replyEmbeds(displayFAQ(getOption("name")!!.asString, user)).await()
            return
        }
        if (subcommandName == "delete") {
            return deleteFAQ(getOption("name")!!.asString)
        }
        if (subcommandName == "add" || subcommandName == "update") {
            if (!member!!.isStaff()) {
                reply("No permission.").setEphemeral(true).await()
                return
            }
            val uuid = UUID.randomUUID().toString()
            val name = getOption("name")!!.asString
            val title = getOption("title")?.asString
            val content = replyEmbeds(embedDefaults {
                this.title = "**Input FAQ Content**"
                addField("Name", name, false)
                title?.let { addField("Title", it, false) }
            })
                .setEphemeral(true)
                .addActionRow(Button.danger("${uuid}cancel", "Cancel"))
                .await()


            val click = scope.async {
                jda.listenFlow<ButtonClickEvent>()
                    .firstOrNull {
                        it.componentId.startsWith(uuid) && it.user == user
                    }
            }
            val message = scope.async {
                jda.listenFlow<MessageReceivedEvent>().firstOrNull {
                    it.channel == channel && it.author == user
                }
            }

            val event = select<Event?> {
                listOf(click, message).forEach { it.onAwait { it } }
            }

            if (event is ButtonClickEvent) {
                val reply = event.reply("Cancelled").setEphemeral(true).await()
                content.deleteOriginal().await()
                delay(5000)
                reply.deleteOriginal().await()
                return
            } else if (event is MessageReceivedEvent) {
                event.createOrUpdateFAQ(name, title, event.message.contentDisplay)
                register(jda) // reload
            }
        }
    }

    private suspend fun SlashCommandEvent.replyAllFAQs() {
        val faqs = newSuspendedTransaction {
            FAQDAO.all().map { it.toPOJO() }
        }

        val faqNames = faqs.joinToString(", ") { it.name }
        replyEmbeds(embedDefaults {
            author = "FAQ List"
            description = faqNames
            setFooter("Requested by ${user.name}#${user.discriminator} | $name", user.avatarUrl)
        }).setEphemeral(true).await()
    }


    private suspend fun MessageReceivedEvent.createOrUpdateFAQ(name: String, title: String?, content: String) =
        newSuspendedTransaction {
            val existingFAQ = FAQDAO.find {
                FAQs.name eq name
            }.firstOrNull()
            if (existingFAQ != null) {
                if (title != null) {
                    existingFAQ.title = title
                }
                existingFAQ.content = content
                message.reply("**Updated FAQ `$name`**").await()
                return@newSuspendedTransaction
            }

            FAQDAO.new {
                this.name = name
                this.author = this@createOrUpdateFAQ.author.idLong
                this.title = title ?: name.replaceFirstChar { it.titlecase() }
                this.content = content
            }
            message.reply("**Created new FAQ `$name`**").await()
        }

    private suspend fun SlashCommandEvent.deleteFAQ(name: String) = newSuspendedTransaction {
        if (!member!!.isStaff()) {
            reply("No permission.").setEphemeral(true).await()
            return@newSuspendedTransaction
        }
        val existingFAQ = FAQDAO.find {
            FAQs.name eq name
        }.firstOrNull() ?: run {
            reply("**Unknown FAQ `$name`**").setEphemeral(true).await()
            return@newSuspendedTransaction
        }

        existingFAQ.delete()
        reply("**Deleted FAQ `$name`**").await()
        register(jda)
    }


}
