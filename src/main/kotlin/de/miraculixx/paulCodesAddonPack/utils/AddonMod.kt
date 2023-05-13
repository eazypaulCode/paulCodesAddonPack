package de.miraculixx.paulCodesAddonPack.utils

import de.miraculixx.paulCodesAddonPack.AddonManager
import de.miraculixx.paulCodesAddonPack.mods.DamageOnMove
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.CustomChallengeData
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.settings.ChallengeIntSetting
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.paulCodesAddonPack.PaulCodesAddonPack
import java.util.*

enum class AddonMod(val uuid: UUID) {
    DAMAGE_ON_MOVE(UUID.randomUUID()),
    ;

    fun getModData(): CustomChallengeData {
        return when (this) {
            DAMAGE_ON_MOVE -> CustomChallengeData(
                uuid,
                DamageOnMove(),
                AddonManager.getSettings(this),
                Icon("LEATHER_BOOTS", naming = IconNaming(cmp("Damage on Move"), listOf(cmp("You will get hurt",), cmp("when you move.")))),
                setOf(ChallengeTags.HARD),
                PaulCodesAddonPack.addonName
            )
        }
    }

    fun getDefaultSetting(): ChallengeData {
        return when (this) {
            DAMAGE_ON_MOVE -> ChallengeData(
                mapOf(
                    "damage" to ChallengeIntSetting("BEETROOT", 2, "hp", min = 1, max = 20)
                ),
                mapOf(
                    "damage" to IconNaming(cmp("Damage Amount"), listOf(cmp("The amount of Damage on Move")))
                ),
            )
        }
    }
}