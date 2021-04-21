module DevDenBot.Commands

open DSharpPlus

let prefix = "!"
open FSharp.Control.Tasks
let (>>=) m f = Option.bind f m

type CommandHandler = DiscordClient -> EventArgs.MessageCreateEventArgs -> Task<unit>

let handleCommand commands (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) =
    task {
        if event.Channel.Guild <> null then
            let command = event.Message.Content
            let parts = command.Split " "

            if parts.[0].StartsWith prefix then
                let commandName = parts.[0].Substring(prefix.Length)
                let matchingCommand = Map.tryFind commandName commands
                return Option.map (fun x -> x()) matchingCommand
    }

let createCommandHandler commands = handleCommand commands
