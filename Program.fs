open DSharpPlus
open DevDenBot
open DevDenBot.Configuration
open DevDenBot.Formatting
open System.Threading.Tasks
open DevDenBot.HasteClient
open Hopac
open FSharp.Control.Tasks
open Experience
open Persistence
open Stats
open Commands

let welcomeChannelId = 821743171942744114UL
let ddServerId = 821743100203368458UL
let pasteEmojiId = 822217736012693575UL
let clashRoleId = 831987774499454997UL

let discordConfig =
    let mainConfig = loadConfig "config.json"
    let config = DiscordConfiguration()
    config.set_Token (getToken mainConfig)
    config.set_TokenType TokenType.Bot
    config.set_Intents DiscordIntents.All
    config

let client = new DiscordClient(discordConfig)

let onReady (client: DiscordClient) _ =
    task {
        let user = client.CurrentUser
        printfn $"Successfully logged in as %s{userAndDiscriminator user} (%d{user.Id})"
    }

let doJoinMessage (client: DiscordClient) (event: EventArgs.GuildMemberAddEventArgs) =
    task {
        let! ddServer = client.GetGuildAsync ddServerId
        let welcomeChannel = ddServer.GetChannel welcomeChannelId

        do!
            welcomeChannel.SendMessageAsync
                $"Welcome %s{event.Member.Mention} to the Developer's Den! There are now %d{event.Guild.MemberCount} users."
            :> Task
    }


let processPasteReaction (_: DiscordClient) (event: EventArgs.MessageReactionAddEventArgs) =
    task {
        if event.Emoji.Id = pasteEmojiId then
            let! reactionMember = event.Guild.GetMemberAsync(event.User.Id)

            let permissions =
                reactionMember.PermissionsIn(event.Channel)

            if permissions &&& Permissions.ManageMessages
               <> Permissions.None then

                let content = trimCodeBlocks event.Message.Content
                let! paste = createPaste content |> Job.toAsync
                do! event.Message.DeleteAsync()

                let pasteMessage =
                    $"""
{paste} {event.Message.Author.Mention}, an admin has converted your message to a paste to keep the channels clean.
Please use https://paste.bristermitten.me when sharing large blocks of code.
                    """

                let! _ = event.Channel.SendMessageAsync pasteMessage
                return ()
    }

let add elem f =
    elem (fun client e -> f client e :> Task)


let statsFile = "/var/data/stats.json"

let saveStats =
    async {
        saveAllStats statsFile statsMap
        printfn "Saved stats to flatfile."
    }

let mainTask =
    task {
        statsMap <- loadAllStats statsFile

        add client.add_Ready onReady
        add client.add_GuildMemberAdded doJoinMessage
        add client.add_MessageReactionAdded processPasteReaction
        add client.add_MessageCreated doExperienceMessageProcess
        add client.add_MessageCreated handleCommand

        do! client.ConnectAsync()

        async {
            while true do
                do! saveStats
                do! Task.Delay(10000) // Save every 10 seconds
        } |> Async.Start
        do! Task.Delay(-1)
    }



[<EntryPoint>]
let main _ =
    task {
        do! mainTask
        do! saveStats
    }
    |> Async.AwaitTask
    |> Async.RunSynchronously

    0 // return an integer exit code
