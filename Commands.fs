module DevDenBot.Commands

open DSharpPlus
open DSharpPlus.Entities
open FSharpPlus

let prefix = "!"
open FSharp.Control.Tasks
let (>>=) m f = Option.bind f m
open Stats

let handleCommand (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) =
    task {
        let command = event.Message

        if not command.Author.IsBot
           && command.Content = "!xp" then
            let xp = statsMap.Item command.Author.Id
            do! event.Channel.SendMessageAsync $"{command.Author.Mention}, you have %d{xp.Xp} experience." :> Task
    }
