package me.bristermitten.devdenbot.leaderboard

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.bristermitten.devdenbot.extensions.commands.KotlinEmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.concurrent.TimeUnit


/**
 * Paginator that allows flexible embeds inspired by com.jagrosh.jdautilities.menu.Paginator
 */
class DevDenPaginator<T> constructor(
    private val valueSupplier: (Int) -> T,
    private val valueRenderer: (KotlinEmbedBuilder, T, Int) -> Unit,
    private val entriesPerPage: Int = 10,
    private val entryCount: Int,
    private val title: String = "",
    private val eventWaiter: EventWaiter,
) {

    companion object {
        private const val LEFT = "\u25C0"
        private const val STOP = "\u23F9"
        private const val RIGHT = "\u25B6"

        private const val timeout: Long = 60
        private val timeoutUnit: TimeUnit = TimeUnit.SECONDS
    }

    fun display(channel: MessageChannel, page: Int = 0) = showMessage(page) { channel.sendMessage(it) }

    private fun update(msg: Message, page: Int) = showMessage(page) { msg.editMessage(it) }

    private fun showMessage(page: Int, showAction: (MessageEmbed) -> MessageAction) =
            showAction.invoke(render(page)).queue { addReactions(it, page); addReactionListener(it, page) }

    private fun render(page: Int): MessageEmbed {
        val embedBuilder = KotlinEmbedBuilder()
        val start = page * entriesPerPage
        val end = min((page + 1) * entriesPerPage, entryCount)
        embedBuilder.title = title
        (start until end)
            .map { it to valueSupplier.invoke(it) }
            .forEach { (idx, value) -> valueRenderer.invoke(embedBuilder, value, idx) }
        embedBuilder.footer = "Page ${page + 1}/${pageCount()}"

        return embedBuilder.build()
    }

    private fun addReactions(msg: Message, page: Int) =
        RestAction.allOf(
            msg.addReaction(LEFT),
            msg.addReaction(STOP),
            msg.addReaction(RIGHT),
        ).queue()

    private fun addReactionListener(msg: Message, page: Int) =
        eventWaiter.waitForEvent(
            MessageReactionAddEvent::class.java,
            { it.messageIdLong == msg.idLong && it.user?.isBot == false },
            { handleReaction(it, msg, page) },
            timeout, timeoutUnit
        ) { cleanUp(msg) }

    private fun handleReaction(event: MessageReactionAddEvent, msg: Message, page: Int) {
        if (event.reactionEmote.name == STOP) {
            cleanUp(msg)
            return
        }

        event.user?.let { event.reaction.removeReaction(it).queue() }

        update(
            msg,
            when (event.reactionEmote.name) {
                LEFT -> max(page - 1, 0)
                RIGHT -> min(page + 1, lastPage())
                else -> page
            }
        )
    }

    private fun pageCount() = ((entryCount - 1) / entriesPerPage) + 1 // <=> ceil(count / itemsPerPage)
    private fun lastPage() = pageCount() - 1 // pageCount is zero-indexed

    private fun cleanUp(msg: Message) = msg.clearReactions().queue()
}