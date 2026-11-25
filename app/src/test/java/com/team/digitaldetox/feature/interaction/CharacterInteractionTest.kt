package com.team.digitaldetox.feature.interaction

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterInteractionTest {
    @Test
    fun repeatedTouchesStayInRangeAndNeverRepeatImmediately() {
        val random = Random(42)
        var previous = -1
        val observed = mutableSetOf<Int>()
        repeat(1000) {
            val next = nextDialogueIndex(defaultCharacterDialogues.size, previous, random)
            assertTrue(next in defaultCharacterDialogues.indices)
            assertNotEquals(previous, next)
            observed += next
            previous = next
        }
        assertEquals(defaultCharacterDialogues.indices.toSet(), observed)
    }

    @Test
    fun twoDialoguesAlternateAndSingleDialogueRemainsUsable() {
        assertEquals(1, nextDialogueIndex(2, 0, Random(1)))
        assertEquals(0, nextDialogueIndex(2, 1, Random(1)))
        assertEquals(0, nextDialogueIndex(1, -1, Random(1)))
        assertEquals(0, nextDialogueIndex(1, 0, Random(1)))
    }
}
