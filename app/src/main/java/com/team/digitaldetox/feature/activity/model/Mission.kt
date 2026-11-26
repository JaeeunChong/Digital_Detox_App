package com.team.digitaldetox.feature.activity.model

/**
 * 미션 종류. 회의 결정에 따라 화면에서는 하나의 목록으로 합쳐 보여주고,
 * 어떤 종류든 시작하면 집중 타이머로 넘어갑니다.
 */
enum class MissionType(val label: String) {
    RANDOM("랜덤"),
    DAILY("오늘의"),
    CUSTOM("내가 정한")
}

/**
 * 현실 활동 분류. 미션 카드에 표시하고, 내가 정한 미션을 추가할 때 고릅니다.
 */
enum class ActivityCategory(val label: String) {
    READING("독서"),
    WALK("산책"),
    EXERCISE("운동"),
    STUDY("공부"),
    HOBBY("취미"),
    REST("휴식"),
    ETC("기타")
}

data class Mission(
    val id: String,
    val title: String,
    val category: ActivityCategory,
    val type: MissionType,
    /** 목표 시간(분). 타이머가 이 시간에 도달하면 성공입니다. */
    val targetMinutes: Int
)