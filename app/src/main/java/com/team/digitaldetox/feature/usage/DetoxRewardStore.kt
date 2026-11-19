package com.team.digitaldetox.feature.usage

import android.content.Context

/**
 * 디톡스 완료/취침 전환 시 보상 계산에 인정할 실제 진행 시간을 저장한다.
 * 캐릭터 포인트/코인 지급 비율은 아직 정해지지 않았으므로 여기서는 시간만 기록한다.
 */
class DetoxRewardStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "offon_detox_reward_time", Context.MODE_PRIVATE
    )

    fun totalEligibleMillis(): Long = preferences.getLong("total_eligible_millis", 0L)
    fun completedSessions(): Int = preferences.getInt("recorded_sessions", 0)

    /** 실제 진행한 시간(일시정지/팝업 대기 시간 제외)만 인정한다. */
    fun recordEligibleTime(eligibleMillis: Long) {
        if (eligibleMillis <= 0L) return
        val oldTotal = totalEligibleMillis()
        preferences.edit()
            .putLong("total_eligible_millis", oldTotal + eligibleMillis)
            .putInt("recorded_sessions", completedSessions() + 1)
            .apply()
    }
}
