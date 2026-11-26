package com.team.digitaldetox.feature.activity.domain

import com.team.digitaldetox.feature.activity.data.MissionCatalog
import com.team.digitaldetox.feature.activity.data.MissionTemplate
import com.team.digitaldetox.feature.activity.model.Mission
import com.team.digitaldetox.feature.activity.model.MissionType
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

/**
 * 오늘의 미션 / 랜덤 미션 생성.
 * - 오늘의 미션: 날짜를 시드로 사용해 같은 날에는 항상 같은 미션이 나옵니다.
 * - 랜덤 미션: 내용과 시간이 모두 랜덤. 취침 시간에 걸치면 만들지 않습니다.
 */
class MissionGenerator(
    private val templates: List<MissionTemplate> = MissionCatalog.templates,
    private val random: Random = Random.Default
) {
    init {
        require(templates.isNotEmpty()) { "mission templates must not be empty" }
    }

    fun dailyMissions(date: LocalDate, count: Int = DAILY_COUNT): List<Mission> {
        val seeded = Random(date.toEpochDay())
        return templates.shuffled(seeded)
            .take(count.coerceAtMost(templates.size))
            .map { template ->
                Mission(
                    id = "daily-$date-${template.key}",
                    title = template.title,
                    category = template.category,
                    type = MissionType.DAILY,
                    targetMinutes = template.defaultMinutes
                )
            }
    }

    /**
     * @return 취침 시간에 걸쳐 만들 수 있는 미션이 없으면 null
     */
    fun randomMission(now: LocalTime, sleepWindow: SleepWindow): Mission? {
        if (sleepWindow.contains(now)) return null
        val candidates = templates.flatMap { template ->
            RANDOM_DURATIONS.map { minutes -> template to minutes }
        }.filterNot { (_, minutes) -> sleepWindow.overlaps(now, minutes) }
        if (candidates.isEmpty()) return null

        val (template, minutes) = candidates.random(random)
        return Mission(
            id = "random-${System.nanoTime()}-${random.nextInt(0, Int.MAX_VALUE)}",
            title = template.title,
            category = template.category,
            type = MissionType.RANDOM,
            targetMinutes = minutes
        )
    }

    companion object {
        const val DAILY_COUNT = 3
        val RANDOM_DURATIONS = listOf(10, 15, 20, 30)
    }
}