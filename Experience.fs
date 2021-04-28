module DevDenBot.Experience

open System
open DSharpPlus
open DSharpPlus.Entities

let levenshtein (s1: string) (s2: string) : int =

    let s1' = s1.ToCharArray()
    let s2' = s2.ToCharArray()

    let rec dist l1 l2 =
        match (l1, l2) with
        | (l1, 0) -> l1
        | (0, l2) -> l2
        | (l1, l2) ->
            if s1'.[l1 - 1] = s2'.[l2 - 1] then
                dist (l1 - 1) (l2 - 1)
            else
                let d1 = dist (l1 - 1) l2
                let d2 = dist l1 (l2 - 1)
                let d3 = dist (l1 - 1) (l2 - 1)
                1 + Math.Min(d1, (Math.Min(d2, d3)))

    dist s1.Length s2.Length

let percentageDifference a b =
    levenshtein a b
    |> fun x -> float x / float b.Length


let countRepetitions data =
    // encodeData : seq<'T> -> seq<int * 'T> i.e. Takes a sequence of 'T types and return a sequence of tuples containing the run length and an instance of 'T.
    let rec encodeData input =
        seq {
            if not (Seq.isEmpty input) then
                let head = Seq.head input

                let runLength =
                    Seq.length (Seq.takeWhile ((=) head) input)

                yield runLength, head
                yield! encodeData (Seq.skip runLength input)
        }

    encodeData data
    |> Seq.map fst
    |> Seq.filter ((<) 2)
    |> Seq.length

let validThreshold = 0.5 // A message must be at least 50% different to any previous messages to be awarded xp

let xpForLevel n =
    (5.0 / 3.0 * n) ** 3.0
    + (55.0 / 2.0 * n) ** 2.0
    + (755.0 / 6.0 * n)

let rawXPForMessage (msg: string) =
    int <| max 1.0 (float msg.Length |> Math.Cbrt)

let xpForMessage previousMessages message =
    let rawXP = rawXPForMessage message

    if List.exists (fun elem -> percentageDifference elem message < validThreshold) previousMessages then
        0 // No XP for messages that are too similar to previous ones
    else
        int
        <| float rawXP
           * (0.9 ** (float <| countRepetitions message))

open Stats
open FSharp.Control.Tasks

let doExperienceMessageProcess (_client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) =
    task {
        if not <| event.Channel :? DiscordDmChannel then
            let stats =
                (Option.orDefault
                    (fun () ->
                        { User = event.Author.Id
                          Xp = 0
                          Level = 0
                          PreviousMessages = List.replicate 10 "" })
                 <| statsMap.TryFind event.Author.Id)

            let xpToAward =
                xpForMessage stats.PreviousMessages event.Message.Content

            let newStats =
                { stats with
                      Xp = stats.Xp + xpToAward
                      PreviousMessages =
                          (List.tail stats.PreviousMessages
                           @ [ event.Message.Content ]) }

            printfn $"Gave %d{xpToAward} XP to user %u{event.Author.Id}"
            statsMap <- statsMap.Add(event.Author.Id, newStats)

    }
