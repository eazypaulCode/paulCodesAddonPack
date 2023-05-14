package de.eazypaul.paulCodesAddonPack.utils

import de.eazypaul.paulCodesAddonPack.AddonManager
import de.eazypaul.paulCodesAddonPack.mods.DamageOnMove
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.CustomChallengeData
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.settings.ChallengeIntSetting
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.eazypaul.paulCodesAddonPack.PaulCodesAddonPack
import de.eazypaul.paulCodesAddonPack.mods.BlockIsChunk
import de.miraculixx.challenge.api.settings.ChallengeBoolSetting
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

enum class AddonMod(val uuid: UUID) {
    DAMAGE_ON_MOVE(UUID.randomUUID()),
    BLOCK_IS_CHUNK(UUID.randomUUID())
    ;

    fun getModData(): CustomChallengeData {
        return when (this) {
            DAMAGE_ON_MOVE -> CustomChallengeData(
                uuid,
                DamageOnMove(),
                AddonManager.getSettings(this),
                Icon("LEATHER_BOOTS", naming = IconNaming(cmp("Damage on Move"), listOf(cmp("You will get hurt"), cmp("when you move.")))),
                setOf(ChallengeTags.HARD, ChallengeTags.FREE),
                PaulCodesAddonPack.addonName
            )
            BLOCK_IS_CHUNK -> CustomChallengeData(
                uuid,
                BlockIsChunk(),
                AddonManager.getSettings(this),
                Icon("DIAMOND_BLOCK", naming = IconNaming(cmp("Block is Chunk"),
                    listOf(cmp("If you place an block"), cmp("the entire chunk will"), cmp("be replaced.")))),
                setOf(ChallengeTags.FUN, ChallengeTags.FREE),
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
            BLOCK_IS_CHUNK -> ChallengeData(
                mapOf(
                    "insecure" to ChallengeBoolSetting("END_PORTAL_FRAME")
                ),
                mapOf(
                    "insecure" to IconNaming(cmp("Insecure Mode"), listOf(cmp("This will enable, that"),
                        cmp("laggy blocks can be replaced."), cmp("Use with caution!", NamedTextColor.RED)))
                )
            )
        }
    }
}