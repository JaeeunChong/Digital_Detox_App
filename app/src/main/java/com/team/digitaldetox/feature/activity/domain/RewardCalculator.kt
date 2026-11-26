package com.team.digitaldetox.feature.activity.domain

import com.team.digitaldetox.feature.activity.model.MissionOutcome

/** 진행 시간 → B 내부 포인트 계산. */
class RewardCalculator(private val policy: RewardPolicy = RewardPolicy.DEFAULT) {

    /**
     * @param elapsedMinutes 실제 진행 시간(분)
     * @param earnedTodayPoints 오늘 이미 미션으로 받은 포인트
     */
    fun calculate(outcome: MissionOutcome, elapsedMinutes: Int, earnedTodayPoints: Int): Int {
        if (outcome == MissionOutcome.TOO_SHORT) return 0
        val raw = elapsedMinutes.coerceAtLeast(0) * policy.pointsPerMinute
        val remaining = (policy.dailyPointCap - earnedTodayPoints).coerceAtLeast(0)
        return minOf(raw, remaining)
    }
}