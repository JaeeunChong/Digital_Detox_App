package com.team.digitaldetox.feature.activity

import com.team.digitaldetox.feature.activity.data.MissionRepository
import com.team.digitaldetox.feature.activity.data.StepCounterSource
import com.team.digitaldetox.feature.activity.domain.FocusTimerJudge
import com.team.digitaldetox.feature.activity.domain.MissionGenerator
import com.team.digitaldetox.feature.activity.domain.RewardCalculator
import com.team.digitaldetox.feature.activity.domain.RewardPolicy
import com.team.digitaldetox.feature.activity.domain.SleepWindow
import com.team.digitaldetox.feature.activity.model.ActivityCategory
import com.team.digitaldetox.feature.activity.model.Mission
import com.team.digitaldetox.feature.activity.model.MissionCompletion
import com.team.digitaldetox.feature.activity.model.MissionOutcome
import java.time.LocalDate
import java.time.LocalTime

/** B 화면 내부의 페이지. 공통 Navigation(AppNavGraph)을 건드리지 않기 위해 내부 상태로 전환합니다. */
sealed interface ActivityPage {
    data object MissionList : ActivityPage
    data class Running(val mission: Mission, val startedAtElapsedMillis: Long) : ActivityPage
    data class Result(val completion: MissionCompletion) : ActivityPage
}

data class ActivityUiState(
    val page: ActivityPage,
    val dailyMissions: List<Mission>,
    val randomMission: Mission?,
    val customMissions: List<Mission>,
    /** 오늘 성공한 미션 id (오늘의/랜덤 미션 완료 표시용) */
    val completedTodayIds: Set<String>,
    val todayPoints: Int,
    val todaySuccessCount: Int,
    val dailyCapReached: Boolean,
    val todaySteps: Int,
    val stepsAreReal: Boolean,
    val sleepWindow: SleepWindow,
    val isSleepTimeNow: Boolean,
    /** 지금 타이머를 시작할 수 있는지 (취침 시간 차단 정책 반영) */
    val canStartTimer: Boolean,
    /** 한 번 보여줄 안내 문구 */
    val notice: String?
)

/** 테스트에서 시간을 바꿀 수 있도록 시계를 주입받습니다. */
interface ActivityClock {
    /** 타이머 경과 시간 측정용 (Android: SystemClock.elapsedRealtime) */
    fun elapsedRealtimeMillis(): Long
    fun currentTimeMillis(): Long
    fun today(): LocalDate
    fun nowTime(): LocalTime
}

/**
 * B(현실 활동·미션) 화면의 상태와 동작.
 * Android/Compose에 의존하지 않는 순수 Kotlin이므로 JVM 단위 테스트가 가능합니다.
 * 모든 동작은 새 [ActivityUiState]를 돌려주며, 화면은 그 값을 그대로 그립니다.
 */
class ActivityController(
    private val repository: MissionRepository,
    private val generator: MissionGenerator,
    /** 취침 구간 (B 내부 기본값) */
    private val sleepWindow: SleepWindow = SleepWindow.DEFAULT,
    private val stepSource: StepCounterSource,
    private val clock: ActivityClock,
    val policy: RewardPolicy = RewardPolicy.DEFAULT
) {
    private val judge = FocusTimerJudge(policy)
    private val calculator = RewardCalculator(policy)

    private var page: ActivityPage = ActivityPage.MissionList
    private var randomMission: Mission? = null

    val isRunning: Boolean get() = page is ActivityPage.Running

    fun state(notice: String? = null): ActivityUiState {
        val today = clock.today()
        val todays = repository.completionsOn(today)
        val points = todays.sumOf { it.rewardPoints }
        val sleepingNow = sleepWindow.contains(clock.nowTime())
        return ActivityUiState(
            page = page,
            dailyMissions = generator.dailyMissions(today),
            randomMission = randomMission,
            customMissions = repository.customMissions(),
            completedTodayIds = todays
                .filter { it.outcome == MissionOutcome.SUCCESS }
                .map { it.missionId }
                .toSet(),
            todayPoints = points,
            todaySuccessCount = todays.count { it.outcome == MissionOutcome.SUCCESS },
            dailyCapReached = points >= policy.dailyPointCap,
            todaySteps = stepSource.todaySteps(),
            stepsAreReal = stepSource.isReal,
            sleepWindow = sleepWindow,
            isSleepTimeNow = sleepingNow,
            canStartTimer = !(policy.blockTimerDuringSleep && sleepingNow),
            notice = notice
        )
    }

    fun drawRandomMission(): ActivityUiState {
        val mission = generator.randomMission(clock.nowTime(), sleepWindow)
        return if (mission == null) {
            state(notice = "취침 시간에는 랜덤 미션이 나오지 않아요.")
        } else {
            randomMission = mission
            state()
        }
    }

    fun addCustomMission(title: String, category: ActivityCategory, targetMinutes: Int): ActivityUiState {
        if (title.isBlank()) return state(notice = "미션 이름을 입력해 주세요.")
        if (title.trim().length > MAX_TITLE_LENGTH) {
            return state(notice = "미션 이름은 ${MAX_TITLE_LENGTH}자 이하로 입력해 주세요.")
        }
        if (targetMinutes <= 0) return state(notice = "목표 시간을 선택해 주세요.")
        repository.addCustomMission(title, category, targetMinutes)
        return state()
    }

    fun removeCustomMission(id: String): ActivityUiState {
        repository.removeCustomMission(id)
        return state()
    }

    fun startMission(mission: Mission): ActivityUiState {
        if (isRunning) return state(notice = "이미 진행 중인 미션이 있어요.")
        if (policy.blockTimerDuringSleep && sleepWindow.contains(clock.nowTime())) {
            return state(notice = "취침 시간에는 타이머를 시작할 수 없어요.")
        }
        page = ActivityPage.Running(mission, clock.elapsedRealtimeMillis())
        return state()
    }

    /** 진행 중인 타이머의 경과 시간(ms). 진행 중이 아니면 0 */
    fun elapsedMillis(): Long {
        val running = page as? ActivityPage.Running ?: return 0L
        return (clock.elapsedRealtimeMillis() - running.startedAtElapsedMillis).coerceAtLeast(0L)
    }

    /** 목표 시간 도달 여부. 화면이 주기적으로 확인해 자동 종료합니다. */
    fun isTargetReached(): Boolean {
        val running = page as? ActivityPage.Running ?: return false
        return elapsedMillis() >= running.mission.targetMinutes * FocusTimerJudge.MILLIS_PER_MINUTE
    }

    /** 타이머 종료 (목표 도달 또는 사용자가 그만두기). 결과를 판정하고 포인트를 계산·기록합니다. */
    fun finishMission(): ActivityUiState {
        val running = page as? ActivityPage.Running ?: return state()
        val mission = running.mission
        val elapsed = elapsedMillis()
        val outcome = judge.judge(elapsed, mission.targetMinutes)
        val minutes = judge.creditedMinutes(elapsed, mission.targetMinutes)
        val earnedToday = repository.earnedPointsOn(clock.today())
        val points = calculator.calculate(outcome, minutes, earnedToday)

        val completion = MissionCompletion(
            missionId = mission.id,
            missionTitle = mission.title,
            category = mission.category,
            type = mission.type,
            outcome = outcome,
            elapsedMinutes = minutes,
            rewardPoints = points,
            completedAtMillis = clock.currentTimeMillis()
        )
        repository.addCompletion(completion)

        if (outcome == MissionOutcome.SUCCESS && randomMission?.id == mission.id) {
            randomMission = null
        }
        page = ActivityPage.Result(completion)
        return state()
    }

    fun backToList(): ActivityUiState {
        if (page is ActivityPage.Result) page = ActivityPage.MissionList
        return state()
    }

    companion object {
        const val MAX_TITLE_LENGTH = 20
    }
}