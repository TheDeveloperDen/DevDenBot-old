package me.bristermitten.devdenbot.mock

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.utils.AttachmentOption
import java.io.File
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.BooleanSupplier
import java.util.function.Consumer

class MockMessageAction(private val value: Message) : MessageAction {
    override fun getJDA(): JDA {
        TODO("Not yet implemented")
    }

    override fun setCheck(checks: BooleanSupplier?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun timeout(timeout: Long, unit: TimeUnit): MessageAction {
        TODO("Not yet implemented")
    }

    override fun deadline(timestamp: Long): MessageAction {
        TODO("Not yet implemented")
    }

    override fun queue(success: Consumer<in Message>?, failure: Consumer<in Throwable>?) {
        success?.accept(value)
    }

    override fun complete(shouldQueue: Boolean): Message {
        return value
    }

    override fun submit(shouldQueue: Boolean): CompletableFuture<Message> {
        return CompletableFuture.completedFuture(value)
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): MessageAction {
        TODO("Not yet implemented")
    }

    override fun append(c: Char): MessageAction {
        TODO("Not yet implemented")
    }

    override fun getChannel(): MessageChannel {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEdit(): Boolean {
        TODO("Not yet implemented")
    }

    override fun apply(message: Message?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun referenceById(messageId: Long): MessageAction {
        TODO("Not yet implemented")
    }

    override fun mentionRepliedUser(mention: Boolean): MessageAction {
        TODO("Not yet implemented")
    }

    override fun failOnInvalidReply(fail: Boolean): MessageAction {
        TODO("Not yet implemented")
    }

    override fun tts(isTTS: Boolean): MessageAction {
        TODO("Not yet implemented")
    }

    override fun reset(): MessageAction {
        TODO("Not yet implemented")
    }

    override fun nonce(nonce: String?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun content(content: String?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun embed(embed: MessageEmbed?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun addFile(data: InputStream, name: String, vararg options: AttachmentOption?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun addFile(file: File, name: String, vararg options: AttachmentOption?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun clearFiles(): MessageAction {
        TODO("Not yet implemented")
    }

    override fun clearFiles(finalizer: BiConsumer<String, InputStream>): MessageAction {
        TODO("Not yet implemented")
    }

    override fun clearFiles(finalizer: Consumer<InputStream>): MessageAction {
        TODO("Not yet implemented")
    }

    override fun override(bool: Boolean): MessageAction {
        TODO("Not yet implemented")
    }

    override fun allowedMentions(allowedMentions: MutableCollection<Message.MentionType>?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun mention(vararg mentions: IMentionable?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun mentionUsers(vararg userIds: String?): MessageAction {
        TODO("Not yet implemented")
    }

    override fun mentionRoles(vararg roleIds: String?): MessageAction {
        TODO("Not yet implemented")
    }

}
