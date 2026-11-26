package com.team.digitaldetox.feature.activity.data

import com.team.digitaldetox.feature.activity.model.ActivityCategory

/** 미션 원본(템플릿). 오늘의/랜덤 미션은 이 목록에서 만들어집니다. */
data class MissionTemplate(
    val key: String,
    val title: String,
    val category: ActivityCategory,
    val defaultMinutes: Int
)

/**
 * 초기 미션 목록 (임시 하드코딩).
 * 회의 결정대로 추후 사용자 취미 조사 결과를 보고 교체/확장합니다.
 * key는 미션을 구분하는 값이므로 한 번 정하면 바꾸지 않는 것을 권장합니다.
 */
object MissionCatalog {
    val templates: List<MissionTemplate> = listOf(
        MissionTemplate("read_book", "종이책 읽기", ActivityCategory.READING, 20),
        MissionTemplate("walk_outside", "동네 산책하기", ActivityCategory.WALK, 20),
        MissionTemplate("stretching", "스트레칭하기", ActivityCategory.EXERCISE, 10),
        MissionTemplate("home_workout", "맨몸 운동하기", ActivityCategory.EXERCISE, 15),
        MissionTemplate("study_focus", "공부에 집중하기", ActivityCategory.STUDY, 30),
        MissionTemplate("journal", "일기 쓰기", ActivityCategory.HOBBY, 10),
        MissionTemplate("drawing", "그림 그리기", ActivityCategory.HOBBY, 20),
        MissionTemplate("tidy_room", "방 정리하기", ActivityCategory.ETC, 15),
        MissionTemplate("meditation", "명상하기", ActivityCategory.REST, 10),
        MissionTemplate("cook", "요리하기", ActivityCategory.HOBBY, 30)
    )
}