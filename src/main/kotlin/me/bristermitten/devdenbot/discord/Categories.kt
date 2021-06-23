package me.bristermitten.devdenbot.discord

import net.dv8tion.jda.api.entities.Message

// Left this as constants as well just in case they are needed later, the set under is the important one
const val OBJECT_ORIENTED_CATEGORY_ID = 821811247904981053
const val IMPERATIVE_CATEGORY_ID = 821813466431881236
const val WEB_CATEGORY_ID = 821813298466521158
const val FUNCTIONAL_CATEGORY_ID = 821812754901762078
const val OTHER_CATEGORY_ID = 826146754247524352

private val SUPPORT_CATEGORIES = setOf(
    OBJECT_ORIENTED_CATEGORY_ID,
    IMPERATIVE_CATEGORY_ID,
    WEB_CATEGORY_ID,
    FUNCTIONAL_CATEGORY_ID,
    OTHER_CATEGORY_ID
)

/**
 * Checks if the message was sent in a support channel or not (under the above categories).
 */
fun Message.inSupportChannel() = category?.let { it.idLong in SUPPORT_CATEGORIES } == true