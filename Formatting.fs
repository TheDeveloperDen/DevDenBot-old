module DevDenBot.Formatting

open DSharpPlus.Entities

let userAndDiscriminator (user: DiscordUser) =
    sprintf "%s#%s" user.Username user.Discriminator

let trimCodeBlocks (s: string) =
    let trimmed = s.Trim()

//    if trimmed.StartsWith("```") then trimmed.Substring(trimmed.IndexOf(' ')).Trim()
//    else if trimmed.StartsWith("`") then trimmed.Substring(1, trimmed.Length - 2).Trim()
//    else
    trimmed
