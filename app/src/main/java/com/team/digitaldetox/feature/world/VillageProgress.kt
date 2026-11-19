package com.team.digitaldetox.feature.world

import java.util.Locale

/**
 * Growth numbers shown on the village screen.
 *
 * Residents give affinity every day. Each resident's daily affinity resets every day
 * and decides that day's coins; the same amount also piles up here as cumulative
 * affinity, which raises the village level. Daily affinity and coin payouts are not
 * implemented yet (they need D's residents and the team's coin rule), so the screen
 * uses [Sample].
 *
 * @property level Current village level. Starts at 1.
 * @property affinity Cumulative affinity inside the current level, from 0 to [affinityToNextLevel] - 1.
 * @property leaves Currency the user owns (shown with the leaf icon). Cannot be negative.
 */
data class VillageProgress(
    val level: Int,
    val affinity: Int,
    val leaves: Int
) {
    init {
        require(level >= 1) { "level must be at least 1" }
        require(affinity in 0 until affinityNeededForLevel(level)) {
            "affinity must be from 0 to affinityToNextLevel - 1"
        }
        require(leaves >= 0) { "leaves cannot be negative" }
    }

    /** Cumulative affinity needed to go from [level] to the next level. */
    val affinityToNextLevel: Int
        get() = affinityNeededForLevel(level)

    /** Filled share of the level bar, from 0.0 up to (not including) 1.0. */
    val affinityFraction: Float
        get() = affinity.toFloat() / affinityToNextLevel

    /**
     * Returns the progress after residents give [amount] affinity.
     * Leftover affinity carries into the next level, and one large amount can raise several levels.
     *
     * @throws IllegalArgumentException If [amount] is negative.
     */
    fun addAffinity(amount: Int): VillageProgress {
        require(amount >= 0) { "amount cannot be negative" }
        var newLevel = level
        var newAffinity = affinity.toLong() + amount
        while (newAffinity >= affinityNeededForLevel(newLevel)) {
            newAffinity -= affinityNeededForLevel(newLevel)
            newLevel++
        }
        return copy(level = newLevel, affinity = newAffinity.toInt())
    }

    companion object {
        /** Example values taken from the village mockup (village_preview.png): Lv. 5, 320 / 500. */
        val Sample = VillageProgress(level = 5, affinity = 320, leaves = 1250)
    }
}

/**
 * Cumulative affinity needed to finish [level]. Lv. 1 needs 100, Lv. 5 needs 500 (matches the mockup).
 * Placeholder rule until the team decides the growth curve.
 */
internal fun affinityNeededForLevel(level: Int): Int = level * 100

/** Formats a currency amount with thousands separators, e.g. 1250 -> "1,250". */
internal fun formatLeaves(leaves: Int): String = String.format(Locale.KOREA, "%,d", leaves)
