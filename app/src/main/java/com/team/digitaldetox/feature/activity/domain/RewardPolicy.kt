package com.team.digitaldetox.feature.activity.domain

/**
 * 포인트/판정 정책 (B 내부). 모든 숫자는 **임시값**입니다.
 * 본인이 직접 활동을 고르는 자기 신고 방식이므로 하루 상한을 둡니다.
 */
data class RewardPolicy(
    /** 1분당 포인트 */
    val pointsPerMinute: Int = 1,
    /** 이 시간(분) 미만이면 포인트 없음 */
    val minimumMinutes: Int = 5,
    /** 미션 타이머로 하루에 받을 수 있는 최대 포인트 */
    val dailyPointCap: Int = 300,
    /** 취침 시간에는 타이머 시작을 막을지 (회의에서 '논의 중') */
    val blockTimerDuringSleep: Boolean = true
) {
    init {
        require(pointsPerMinute >= 0)
        require(minimumMinutes >= 0)
        require(dailyPointCap >= 0)
    }

    companion object {
        val DEFAULT = RewardPolicy()
    }
}