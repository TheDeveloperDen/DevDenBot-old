package net.developerden.devdenbot.pasting

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.log
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.nio.file.Files
import javax.inject.Inject

class CodeAttachmentMessageListener @Inject constructor(
    override val ddbConfig: DDBConfig,
) : EventListener, HasConfig {

    companion object {
        private val log by log()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun isAttachmentText(attachment: Message.Attachment): Boolean {
        if (attachment.isImage || attachment.isVideo) {
            return false
        }
        if (attachment.contentType?.startsWith("text") == true) {
            return true
        }
        return withContext(Dispatchers.IO) {
            val tempFile = Files.createTempFile("devdenbot-paste", "")
            val file = attachment.downloadToFile(tempFile.toFile()).await()
            val type = Files.probeContentType(file.toPath())
            when {
                type == null -> false // probably a binary
                type.startsWith("text") -> true
                else -> false
            }
        }
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }
        if (event.message.attachments.isEmpty()) {
            return
        }

        val attachmentsToConvert = event.message.attachments.asFlow()
            .filter(this::isAttachmentText)


        val convertedAttachments = attachmentsToConvert
            .map {
                it.fileName to HasteClient.postCode(it.retrieveInputStream().await())
            }
            .toList()
            .toMap()

        if (convertedAttachments.isEmpty()) {
            return
        }

        val member = event.member ?: event.guild.retrieveMemberById(event.author.id).await()

        val embed = createEmbed(convertedAttachments, member)
        event.message.replyEmbeds(embed).await()
    }

    private fun createEmbed(attachments: Map<String, String>, member: Member): MessageEmbed {
        return embedDefaults {
            author = member.effectiveName
            authorImage = member.user.effectiveAvatarUrl
            title = "Your Code"
            description = "We converted your code files to paste links for nicer formatting"

            attachments.forEach { (t, u) ->
                field(t, HasteClient.baseUrl + u)
            }

            setFooter("This message was converted automatically to keep the channels clean from large code blocks.")

        }
    }


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }

}
