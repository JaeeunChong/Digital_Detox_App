package com.team.digitaldetox.feature.activity.domain

import java.time.LocalTime

/**
 * 취침 구간. 23:00 ~ 07:00 처럼 자정을 넘는 구간을 지원합니다.
 * 시작(취침)은 포함, 끝(기상)은 제외합니다. 취침 = 기상이면 빈 구간으로 봅니다.
 *
 * 회의 결정(취침 시간에는 랜덤 미션·타이머 없음)을 위한 B 내부 규칙입니다.
 * 현재는 B 내부 기본값([DEFAULT])을 사용합니다.
 */
data class SleepWindow(
    val sleepHour: Int,
    val sleepMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int
) {
    init {
        require(sleepHour in 0..23 && wakeHour in 0..23) { "hour must be 0..23" }
        require(sleepMinute in 0..59 && wakeMinute in 0..59) { "minute must be 0..59" }
    }

    private val start = sleepHour * 60 + sleepMinute
    private val end = wakeHour * 60 + wakeMinute

    /** 하루 중 분(0..1439)이 취침 구간인지 */
    fun contains(minuteOfDay: Int): Boolean {
        val m = Math.floorMod(minuteOfDay, MINUTES_PER_DAY)
        return when {
            start == end -> false
            start < end -> m in start until end
            else -> m >= start || m < end // 자정을 넘는 경우
        }
    }

    fun contains(time: LocalTime): Boolean = contains(time.hour * 60 + time.minute)

    /** [startMinuteOfDay]부터 [durationMinutes] 동안 한 번이라도 취침 구간에 걸치는지 */
    fun overlaps(startMinuteOfDay: Int, durationMinutes: Int): Boolean {
        if (durationMinutes <= 0) return contains(startMinuteOfDay)
        // 길이가 하루를 넘어도 하루치만 보면 충분합니다.
        val span = minOf(durationMinutes, MINUTES_PER_DAY)
        return (0 until span).any { contains(startMinuteOfDay + it) }
    }

    fun overlaps(start: LocalTime, durationMinutes: Int): Boolean =
        overlaps(start.hour * 60 + start.minute, durationMinutes)

    companion object {
        const val MINUTES_PER_DAY = 24 * 60

        /** B 내부 기본값(23:00 ~ 07:00) */
        val DEFAULT = SleepWindow(23, 0, 7, 0)
    }
}