package net.developerden.devdenbot.learning

import com.google.inject.Inject
import com.google.inject.Singleton
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.discord.isStaff
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

@Used
@Singleton
class LearningResourcesCommand @Inject constructor(override val ddbConfig: DDBConfig) : DevDenSlashCommand(
    name = "learning",
    description = "Shows a list of resources for learning a specific programming topic"
), HasConfig {

    override suspend fun load(action: CommandCreateAction) {
        val topicOption = OptionData(OptionType.STRING, "topic", "the name of the topic")

        LearningResourcesCache.all()
            .map { it.name }
            .forEach {
                topicOption.addChoices(Command.Choice(it, it))
            }

        action.addSubcommands(SubcommandData("get", "Get resources about a topic")
            .addOptions(topicOption))

        action.addSubcommands(SubcommandData("list", "Get all topics"))
        action.addSubcommands(SubcommandData("update", "Refresh topics from database"))
    }

    override suspend fun SlashCommandEvent.execute() {
        if (subcommandName == "list") {
            return replyAllTopics()
        }
        if (subcommandName == "get") {
            val topicName = getOption("topic")!!.asString
            val topic = LearningResourcesCache.get(topicName) ?: run {
                reply("Unknown topic $topicName.").setEphemeral(true).await()
                return
            }

            replyEmbeds(embedDefaults {
                author = topic.name
                title = topic.description
                description = topic.resources.joinToString("\n") {
                    val pros = if (it.pros.isEmpty()) "" else "\n**Pros:**\n" + it.pros.joinToString("\n")
                    val cons = if (it.cons.isEmpty()) "" else "\n**Cons:**\n" + it.cons.joinToString("\n")
                    val linkedName = "[${it.name}](${it.url})"
                    val price = "**Price:** ${it.price ?: "Free!"}"
                    "$linkedName\n$price$pros$cons\n"
                }
                setFooter("Requested by ${user.name}#${user.discriminator} | Learning Resources", user.avatarUrl)
            }).await()
            return
        }
        if (subcommandName == "update") {
            if (!member!!.isStaff()) {
                reply("No permission.").setEphemeral(true).await()
                return
            }
            LearningResourcesCache.updateAll()
            reply("Updated cache").setEphemeral(true).await()
            register(jda)
        }
    }

    private suspend fun SlashCommandEvent.replyAllTopics() {
        val topicNames = LearningResourcesCache.all().joinToString(", ") { it.name }
        replyEmbeds(embedDefaults {
            author = "Topic List"
            description = topicNames
            setFooter("Requested by ${user.name}#${user.discriminator} | Learning Resources", user.avatarUrl)
        }).setEphemeral(true).await()
    }

}
