module DevDenBot.Configuration

open FSharp.Json
open System.IO

type Config = {
    token: string
}

let readFile = File.ReadAllText
let loadConfig = readFile >> Json.deserialize<Config>