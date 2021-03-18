module DevDenBot.Configuration

open FSharp.Json
open System.IO

type Config = { token: string }

let readFile = File.ReadAllText
let loadConfig = readFile >> Json.deserialize<Config>

let tokenFromEnv () =
    System.Environment.GetEnvironmentVariable "DDB_TOKEN"
    |> Option.ofObj

let (/??) opt def =
    match opt with
    | Some t -> t
    | None -> def

let getToken config = tokenFromEnv () /?? config.token
