module DevDenBot.HasteClient
open HttpFs.Client
open Hopac
open FSharp.Json

let rawPasteUrl = "https://paste.bristermitten.me/"
let documentsEndpoint = rawPasteUrl + "documents"


type HasteResponse = {
    key: string
}

let getPasteUrl = (+) rawPasteUrl

let createPaste body =
    Request.createUrl Post documentsEndpoint
    |> Request.bodyString body
    |> Request.setHeader (UserAgent "DevDenBot/1.0")
    |> Request.responseAsString
    |> Job.map Json.deserialize<HasteResponse>
    |> Job.map (fun x -> x.key)
    |> Job.map getPasteUrl