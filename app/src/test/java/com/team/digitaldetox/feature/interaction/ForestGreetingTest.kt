package com.team.digitaldetox.feature.interaction

import org.junit.Assert.assertEquals
import org.junit.Test

class ForestGreetingTest {
    @Test
    fun greetingMatchesEveryLocalHour() {
        val morning = "안녕하세요! 좋은 아침이에요."
        val afternoon = "안녕하세요! 좋은 오후에요."
        val evening = "안녕하세요! 편안한 저녁 보내고 계신가요?"
        val expected = List(5) { evening } + List(7) { morning } + List(6) { afternoon } + List(6) { evening }
        expected.forEachIndexed { hour, message ->
            assertEquals("hour=$hour", message, greetingForLaunchHour(hour))
        }
    }
}
