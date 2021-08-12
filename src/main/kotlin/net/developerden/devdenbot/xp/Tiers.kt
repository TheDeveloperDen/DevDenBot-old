package net.developerden.devdenbot.xp

import net.dv8tion.jda.api.JDA

val TIER_ROLE_IDS = listOf(
    821743100203368458, //@everyone (tier 0)
    823167811555033150, // tier 1
    837653180774875178, // 2
    837661828405395476, // 3
    837662055921221712, // 4
    837662055921221712, // 5
    837662496432193640,
    837662699235311616,
    837662908703703070,
    837663085657194546,
    837663288064999424
)

fun tierOf(level: Int): Int {
    if (level == 0) {
        return 0
    }
    return 1 + (level / 10)
}

fun tierRole(jda: JDA, tier: Int) = jda.getRoleById(TIER_ROLE_IDS[tier]) ?: error("Could not find role for Tier $tier")
