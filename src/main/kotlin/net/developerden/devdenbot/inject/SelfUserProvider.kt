package net.developerden.devdenbot.inject

import com.google.inject.Inject
import com.google.inject.Provider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.SelfUser

/**
 * @author AlexL
 */
class SelfUserProvider @Inject constructor(
    private val jda: JDA
) : Provider<SelfUser> {

    override fun get() = jda.selfUser
}
