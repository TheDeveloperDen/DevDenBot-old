module DevDenBot.Commands

open DSharpPlus
open System.Threading.Tasks
open FSharp.Control.Tasks
open Stats

let prefix = "!"

let handleCommand (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) =
    task {
        let command = event.Message

        if not command.Author.IsBot
           && command.Content = "!xp" then
            let xp = statsMap.Item command.Author.Id
            do! event.Channel.SendMessageAsync $"{command.Author.Mention}, you have %d{xp.Xp} experience." :> Task
    }
