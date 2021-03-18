open System
open DSharpPlus
open DevDenBot.Configuration
open DevDenBot.Formatting
open DSharpPlus.CommandsNext
open System.Threading.Tasks
open System.IO
open Microsoft.Extensions.Configuration

let welcomeChannelId = 821743171942744114UL
let ddServerId = 821743100203368458UL

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

        do! welcomeChannel.SendMessageAsync(sprintf "Welcome %s to the Developer's Den!" (event.Member.Mention))
            |> Async.AwaitTask
            |> Async.Ignore
    }

let mainTask =
    async {
        client.add_Ready (fun client event ->
            async { onReady client event }
            |> Async.StartAsTask :> _)

        client.add_GuildMemberAdded (fun client e -> doJoinMessage client e |> Async.StartAsTask :> _)

        client.ConnectAsync()
        |> Async.AwaitTask
        |> Async.RunSynchronously

        do! Async.AwaitTask(Task.Delay(-1))
    }


[<EntryPoint>]
let main argv =
    Async.RunSynchronously mainTask
    0 // return an integer exit code
