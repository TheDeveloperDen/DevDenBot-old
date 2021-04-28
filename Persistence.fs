module DevDenBot.Persistence

open Stats
open System.IO
open FSharp.Json

let loadAllStats file =
    File.ReadAllText file
    |> Json.deserialize<Map<Snowflake, UserStats>>

let saveAllStats file stats = File.WriteAllText(file, Json.serialize stats) 