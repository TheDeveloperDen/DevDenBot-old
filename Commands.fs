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
            let xpMaybe = statsMap.TryFind (string command.Author.Id)
            match xpMaybe with
            | Some xp -> do! event.Channel.SendMessageAsync $"{command.Author.Mention}, you have %d{xp.Xp} XP" :> Task
            | None -> do! event.Channel.SendMessageAsync $"{command.Author.Mention}, No XP found for you" :> Task
    }
