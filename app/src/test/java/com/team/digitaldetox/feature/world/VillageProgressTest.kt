package com.team.digitaldetox.feature.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class VillageProgressTest {

    @Test
    fun sample_matchesMockup() {
        val sample = VillageProgress.Sample
        assertEquals(5, sample.level)
        assertEquals(500, sample.affinityToNextLevel)
        assertEquals(0.64f, sample.affinityFraction, 0.0001f)
    }

    @Test
    fun addAffinity_belowGoal_keepsLevel() {
        val after = VillageProgress.Sample.addAffinity(100)
        assertEquals(5, after.level)
        assertEquals(420, after.affinity)
    }

    @Test
    fun addAffinity_reachingGoalExactly_levelsUpWithZeroAffinity() {
        val after = VillageProgress.Sample.addAffinity(180)
        assertEquals(6, after.level)
        assertEquals(0, after.affinity)
        assertEquals(600, after.affinityToNextLevel)
    }

    @Test
    fun addAffinity_carriesLeftoverIntoNextLevel() {
        val after = VillageProgress.Sample.addAffinity(300)
        assertEquals(6, after.level)
        assertEquals(120, after.affinity)
    }

    @Test
    fun addAffinity_largeAmount_raisesSeveralLevels() {
        // Lv.1 needs 100, Lv.2 needs 200 -> 350 = Lv.3 with 50 left.
        val after = VillageProgress(level = 1, affinity = 0, leaves = 0).addAffinity(350)
        assertEquals(3, after.level)
        assertEquals(50, after.affinity)
    }

    @Test
    fun addAffinity_keepsLeaves() {
        assertEquals(1250, VillageProgress.Sample.addAffinity(1000).leaves)
    }

    @Test
    fun addAffinity_rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException::class.java) { VillageProgress.Sample.addAffinity(-1) }
    }

    @Test
    fun constructor_rejectsAffinityAtOrAboveGoal() {
        assertThrows(IllegalArgumentException::class.java) {
            VillageProgress(level = 5, affinity = 500, leaves = 0)
        }
    }

    @Test
    fun constructor_rejectsNegativeLeaves() {
        assertThrows(IllegalArgumentException::class.java) {
            VillageProgress(level = 1, affinity = 0, leaves = -1)
        }
    }

    @Test
    fun formatLeaves_addsThousandsSeparator() {
        assertEquals("1,250", formatLeaves(1250))
        assertEquals("0", formatLeaves(0))
    }
}
