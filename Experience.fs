module DevDenBot.Experience

open System
open System.Threading.Tasks
open DSharpPlus
open DSharpPlus.Entities

open Stats

 
let levenshtein (strOne : string) (strTwo : string) =
    let strOne = strOne.ToCharArray ()
    let strTwo = strTwo.ToCharArray ()
 
    let (distArray : int[,]) = Array2D.zeroCreate (strOne.Length + 1) (strTwo.Length + 1)
 
    for i = 0 to strOne.Length do distArray.[i, 0] <- i
    for j = 0 to strTwo.Length do distArray.[0, j] <- j
 
    for j = 1 to strTwo.Length do
        for i = 1 to strOne.Length do
            if strOne.[i - 1] = strTwo.[j - 1] then distArray.[i, j] <- distArray.[i - 1, j - 1]
            else
                distArray.[i, j] <- List.min (
                    [distArray.[i-1, j] + 1; 
                    distArray.[i, j-1] + 1; 
                    distArray.[i-1, j-1] + 1]
                )
    distArray.[strOne.Length, strTwo.Length]

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

// log10(len.toDouble()).toInt() + (len / 10) + (1..3).random()
let random = Random()

let nth arr i = Array.item i arr

let randFrom (arr: 'a []) (random: Random) =
    Array.length arr |> random.Next |> nth arr

let rawXPForMessage (msg: string) =
    let len = float msg.Length in

    log10 len
    + (len / 10.0)
    + float (randFrom [| 1 .. 3 |] random)

let xpForMessage previousMessages message =
    let rawXP = rawXPForMessage message

    if List.exists (fun elem -> percentageDifference elem message < validThreshold) previousMessages then
        0 // No XP for messages that are too similar to previous ones
    else
        int
        <| float rawXP
           * (0.9 ** (float <| countRepetitions message))

let private newUserStats id =
    { User = id
      Xp = 0
      Level = 0
      PreviousMessages = List.replicate 10 "" }

let doExperienceMessageProcess (client: DiscordClient) (event: EventArgs.MessageCreateEventArgs) =
    (fun () ->
        let start = DateTime.Now
        
        if not <| event.Channel :? DiscordDmChannel
           && not event.Author.IsBot then
            let snowflake = string event.Author.Id
            let stats =
                (Option.orDefault (fun () -> newUserStats snowflake)
                 <| statsMap.TryFind snowflake)

            let xpToAward =
                xpForMessage stats.PreviousMessages event.Message.Content

            let newStats =
                { stats with
                      Xp = stats.Xp + xpToAward
                      PreviousMessages =
                          (List.tail stats.PreviousMessages
                           @ [ event.Message.Content ]) }

            printfn $"Gave %d{xpToAward} XP to user %u{event.Author.Id}"
            statsMap <- statsMap.Add(snowflake, newStats)
            let endTime = DateTime.Now - start
            printfn $"Took {endTime.Milliseconds}ms to handle")
    |> Task.Run
