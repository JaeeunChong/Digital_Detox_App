package com.team.digitaldetox.feature.activity.domain

import com.team.digitaldetox.feature.activity.model.MissionOutcome

/**
 * 집중 타이머 성공 여부 판정.
 *
 * 1주차 기준: '사용자가 중간에 그만뒀는지'와 '진행 시간'만으로 판정합니다.
 * 타이머 도중 다른 앱 사용을 실패로 볼지는 A(사용 기록)와 협의 후 추가합니다.
 */
class FocusTimerJudge(private val policy: RewardPolicy = RewardPolicy.DEFAULT) {

    fun judge(elapsedMillis: Long, targetMinutes: Int): MissionOutcome {
        val elapsedMinutes = toMinutes(elapsedMillis)
        return when {
            elapsedMillis >= targetMinutes * MILLIS_PER_MINUTE -> MissionOutcome.SUCCESS
            elapsedMinutes >= policy.minimumMinutes -> MissionOutcome.STOPPED_EARLY
            else -> MissionOutcome.TOO_SHORT
        }
    }

    /** 포인트 계산용 분(내림). 목표 시간을 넘겨도 목표 시간까지만 인정합니다. */
    fun creditedMinutes(elapsedMillis: Long, targetMinutes: Int): Int =
        minOf(toMinutes(elapsedMillis), targetMinutes)

    private fun toMinutes(millis: Long): Int =
        (millis.coerceAtLeast(0) / MILLIS_PER_MINUTE).toInt()

    companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}