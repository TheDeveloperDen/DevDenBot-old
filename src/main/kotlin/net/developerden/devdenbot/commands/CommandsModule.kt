package net.developerden.devdenbot.commands

import com.google.inject.Singleton
import com.jagrosh.jdautilities.command.CommandClient
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibindingsScanner
import net.developerden.devdenbot.inject.Classpath

/**
 * @author Alexander Wood (BristerMitten)
 */
class CommandsModule : KotlinModule() {


    override fun configure() {
        val commands = Classpath.subtypesOf(DevDenCommand::class.java)
        val binder = KotlinMultibinder.newSetBinder<DevDenCommand>(kotlinBinder)

        for (command in commands) {
            binder.addBinding().to(command)
        }

        install(KotlinMultibindingsScanner.asModule())

        bind<CommandClient>().toProvider<CommandClientProvider>().`in`<Singleton>()

        super.configure()
    }
}
