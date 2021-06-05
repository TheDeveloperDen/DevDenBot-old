package me.bristermitten.devdenbot.commands.info

import com.jagrosh.jdautilities.command.CommandEvent
import kong.unirest.Unirest
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.arguments.arguments
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import net.dv8tion.jda.api.JDA
import java.lang.Exception
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Used
class UrbanDictionaryCommand @Inject constructor(
        val jda: JDA,
) : DevDenCommand(
        name = "ud",
        help = "Display urban dictionary info",
        category = FunCategory,
        aliases = arrayOf("urband", "urbandictionary"),
        commandChannelOnly = true
) {

    override suspend fun CommandEvent.execute() {

        val args = arguments().args

        if(args.isEmpty()){
            message.channel.sendMessage("Please specify an urban dictionary url.").await()
            return
        }

        val url = args.first().content

        try{
            val res = Unirest.get("http://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(url, "UTF-8")).asJsonAsync()

            val json = res.get(30, TimeUnit.SECONDS)
            val list = json.body.`object`.getJSONArray("list")

            if(list.length() == 0) {
                message.channel.sendMessage("Unable to find a definition from that url.").await()
                return
            }

            val item = list.getJSONObject(0)
            val output = String.format("Definition for **%s**: \\n"
                                        +"```\n" + "%s\n"
                                        + "```\n"
                                        + "**example**: \n"
                                        + "%s" + "\n\n"
            , item.getString("word"), item.getString("definition"), item.getString("example"))

            message.channel.sendMessage(output).await()

        } catch (exception: Exception){
            throw Exception(exception)
        }

    }
}
