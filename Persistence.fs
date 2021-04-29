module DevDenBot.Persistence

open Stats
open System.IO
open FSharp.Json

type StatsMap = Map<Snowflake, UserStats>

let loadAllStats file =
    File.ReadAllText file
    |> Json.deserialize<StatsMap>


let saveAllStats file (stats: StatsMap) = File.WriteAllText(file, Json.serialize stats) 