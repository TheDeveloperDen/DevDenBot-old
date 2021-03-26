open DSharpPlus
open DevDenBot.Configuration
open DevDenBot.Formatting
open System.Threading.Tasks
open DevDenBot.HasteClient
open Hopac

let welcomeChannelId = 821743171942744114UL
let ddServerId = 821743100203368458UL
let pasteEmojiId = 822217736012693575UL

let discordConfig =
    let mainConfig = loadConfig "config.json"
    let config = DiscordConfiguration()
    config.set_Token (getToken mainConfig)
    config.set_TokenType TokenType.Bot
    config.set_Intents DiscordIntents.All
    config

let client = new DiscordClient(discordConfig)

let onReady (client: DiscordClient) _ =
    let user = client.CurrentUser in printfn "Successfully logged in as %s (%d)" (userAndDiscriminator user) user.Id

let doJoinMessage (client: DiscordClient) (event: EventArgs.GuildMemberAddEventArgs) =
    async {
        let! ddServer = client.GetGuildAsync ddServerId |> Async.AwaitTask
        let welcomeChannel = ddServer.GetChannel welcomeChannelId

        do! welcomeChannel.SendMessageAsync
                (sprintf
                    "Welcome %s to the Developer's Den! There are now %d users."
                     (event.Member.Mention)
                     (event.Guild.MemberCount))
            |> Async.AwaitTask
            |> Async.Ignore
    }



let processPasteReaction (_: DiscordClient) (event: EventArgs.MessageReactionAddEventArgs) =
    async {
        if event.Emoji.Id <> pasteEmojiId then
            return ()
        else
            let! reactionMember =
                event.Guild.GetMemberAsync(event.User.Id)
                |> Async.AwaitTask

            let permissions =
                reactionMember.PermissionsIn(event.Channel)

            if permissions &&& Permissions.ManageMessages = Permissions.None then
                return ()
            else
                let content = event.Message.Content |> trimCodeBlocks
                let! paste = createPaste content |> Job.toAsync
                do! event.Message.DeleteAsync() |> Async.AwaitTask

                let pasteMessage =
                    $"""
{paste}

{event.Message.Author.Mention}, an admin has converted your message to a paste to keep the channels clean.
Please use https://paste.bristermitten.me when sharing large blocks of code.
                    """

                do! event.Channel.SendMessageAsync pasteMessage

            return ()
    }


let mainTask =
    async {
        client.add_Ready (fun client event ->
            async { onReady client event }
            |> Async.StartAsTask :> _)

        client.add_GuildMemberAdded (fun client e -> doJoinMessage client e |> Async.StartAsTask :> _)
        client.add_MessageReactionAdded (fun client e -> processPasteReaction client e |> Async.StartAsTask :> _)

        client.ConnectAsync()
        |> Async.AwaitTask
        |> Async.RunSynchronously

        do! Async.AwaitTask(Task.Delay(-1))
    }


[<EntryPoint>]
let main argv =
    Async.RunSynchronously mainTask
    0 // return an integer exit code
