module DevDenBot.Formatting

open DSharpPlus.Entities

let userAndDiscriminator (user: DiscordUser) = sprintf "%s#%s" user.Username user.Discriminator