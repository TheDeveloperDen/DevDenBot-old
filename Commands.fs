module DevDenBot.Commands

open DSharpPlus
open DSharpPlus.Entities
open FSharpPlus

let prefix = "!"
open FSharp.Control.Tasks
let (>>=) m f = Option.bind f m

let handleCommand commands (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) = task {
    let command = event.Message
    if not command.Author.IsBot && command.Content.StartsWith prefix then
        printfn ""
}
