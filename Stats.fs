module DevDenBot.Stats

type Snowflake = uint64

type UserStats =
    { User: Snowflake
      Xp: int
      Level: int
      PreviousMessages: string list }

let mutable statsMap = Map.empty<Snowflake, UserStats>
