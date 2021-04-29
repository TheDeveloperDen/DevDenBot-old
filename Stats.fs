module DevDenBot.Stats

type Snowflake = string

type UserStats =
    { User: Snowflake
      Xp: int
      Level: int
      PreviousMessages: string list }

let mutable statsMap = Map.empty<Snowflake, UserStats>
