package com.team.digitaldetox.feature.activity.model

/** 집중 타이머 종료 결과. */
enum class MissionOutcome(val label: String) {
    /** 목표 시간을 채움 → 포인트 지급, 미션 성공 횟수에 포함 */
    SUCCESS("미션 성공"),

    /** 최소 시간 이상 했지만 목표 전에 그만둠 → 한 시간만큼 포인트, 성공 횟수에는 미포함 */
    STOPPED_EARLY("중간 종료"),

    /** 최소 시간 미만 → 포인트 없음 */
    TOO_SHORT("시간 부족")
}

/**
 * 타이머 한 번의 결과 기록 (B 내부용).
 */
data class MissionCompletion(
    val missionId: String,
    val missionTitle: String,
    val category: ActivityCategory,
    val type: MissionType,
    val outcome: MissionOutcome,
    /** 실제로 진행한 시간(분, 내림) */
    val elapsedMinutes: Int,
    /** B 내부 포인트(임시) */
    val rewardPoints: Int,
    /** 종료 시각 (System.currentTimeMillis 기준) */
    val completedAtMillis: Long
)