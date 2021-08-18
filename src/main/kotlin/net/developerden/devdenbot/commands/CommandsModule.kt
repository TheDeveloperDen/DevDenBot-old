package net.developerden.devdenbot.commands

import com.google.inject.Singleton
import com.jagrosh.jdautilities.command.CommandClient
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibindingsScanner
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.inject.Classpath
import java.lang.reflect.Modifier
import kotlin.reflect.full.isSuperclassOf

/**
 * @author Alexander Wood (BristerMitten)
 */
class CommandsModule : KotlinModule() {


    override fun configure() {
        val commands = Classpath.subtypesOf(DevDenCommand::class.java)
        val normalCommandBinder = KotlinMultibinder.newSetBinder<DevDenCommand>(kotlinBinder)
        val slashCommandBinder = KotlinMultibinder.newSetBinder<DevDenSlashCommand>(kotlinBinder)

        for (command in commands) {
            if (Modifier.isAbstract(command.modifiers)) {
                continue
            }
            normalCommandBinder.addBinding().to(command)
            if (DevDenSlashCommand::class.isSuperclassOf(command.kotlin)) {
                @Suppress("UNCHECKED_CAST")
                slashCommandBinder.addBinding().to(command as Class<out DevDenSlashCommand>)
            }
        }

        install(KotlinMultibindingsScanner.asModule())

        bind<CommandClient>().toProvider<CommandClientProvider>().`in`<Singleton>()

        super.configure()
    }
}
