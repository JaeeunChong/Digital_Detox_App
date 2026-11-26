package com.team.digitaldetox.feature.activity.data

import com.team.digitaldetox.feature.activity.model.ActivityCategory
import com.team.digitaldetox.feature.activity.model.Mission
import com.team.digitaldetox.feature.activity.model.MissionCompletion
import com.team.digitaldetox.feature.activity.model.MissionType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 미션 데이터 저장소.
 * 1주차는 메모리 구현만 있습니다(앱을 완전히 종료하면 사라짐).
 * 영구 저장 방식은 팀 협의 후 정하며, 그때 구현체만 교체합니다.
 */
interface MissionRepository {
    fun customMissions(): List<Mission>
    fun addCustomMission(title: String, category: ActivityCategory, targetMinutes: Int): Mission
    fun removeCustomMission(id: String)

    fun completions(): List<MissionCompletion>
    fun addCompletion(completion: MissionCompletion)

    fun completionsOn(date: LocalDate): List<MissionCompletion>
    fun earnedPointsOn(date: LocalDate): Int = completionsOn(date).sumOf { it.rewardPoints }
}

class InMemoryMissionRepository(
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : MissionRepository {

    private val custom = mutableListOf<Mission>()
    private val history = mutableListOf<MissionCompletion>()
    private var nextCustomId = 1

    @Synchronized
    override fun customMissions(): List<Mission> = custom.toList()

    @Synchronized
    override fun addCustomMission(
        title: String,
        category: ActivityCategory,
        targetMinutes: Int
    ): Mission {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "title must not be blank" }
        require(targetMinutes > 0) { "targetMinutes must be positive" }
        val mission = Mission(
            id = "custom-${nextCustomId++}",
            title = trimmed,
            category = category,
            type = MissionType.CUSTOM,
            targetMinutes = targetMinutes
        )
        custom += mission
        return mission
    }

    @Synchronized
    override fun removeCustomMission(id: String) {
        custom.removeAll { it.id == id }
    }

    @Synchronized
    override fun completions(): List<MissionCompletion> = history.toList()

    @Synchronized
    override fun addCompletion(completion: MissionCompletion) {
        history += completion
    }

    @Synchronized
    override fun completionsOn(date: LocalDate): List<MissionCompletion> =
        history.filter {
            Instant.ofEpochMilli(it.completedAtMillis).atZone(zoneId).toLocalDate() == date
        }
}