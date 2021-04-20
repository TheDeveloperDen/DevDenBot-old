module DevDenBot.Commands
open DSharpPlus

let prefix = "!"
open FSharp.Control.Tasks
let (>>=) m f = Option.bind f m
let handleCommand (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) = task {
    (Option.ofNullable event.Channel.Guild) >>= (fun x -> Some 3)
}