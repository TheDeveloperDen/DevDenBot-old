package me.bristermitten.devdenbot.discord

import net.dv8tion.jda.api.entities.Member

/**
 * Returns a "ping" of the given Member depending on their role settings
 * If the user has elected to not be mentioned, a string in the format "Username#discriminator" is returned
 * Otherwise a formatted string mentioning the user is returned
 */
fun Member.getPing(): String {
    return if (canBePinged()) {
        user.asMention
    } else {
        "${user.name}#${user.discriminator}"
    }
}

/**
 * Return if a given member should be pinged by the bot or users
 * This is defined by them not having the "No Ping" role
 */
fun Member.canBePinged() = roles.none { it.idLong == NO_PING_ROLE_ID }
