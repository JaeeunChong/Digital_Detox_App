package com.team.digitaldetox.feature.activity

import android.os.SystemClock
import com.team.digitaldetox.feature.activity.data.FakeStepCounterSource
import com.team.digitaldetox.feature.activity.data.InMemoryMissionRepository
import com.team.digitaldetox.feature.activity.domain.MissionGenerator
import java.time.LocalDate
import java.time.LocalTime

/**
 * B 기능의 객체 연결 지점 (B 내부에서만 완결).
 * 앱 프로세스가 살아 있는 동안 하나만 유지되므로, 홈으로 나갔다 오거나 화면을 회전해도
 * 진행 중인 타이머와 기록이 유지됩니다. (앱을 완전히 종료하면 사라짐)
 */
internal object ActivityDependencies {
    val controller: ActivityController by lazy {
        ActivityController(
            repository = InMemoryMissionRepository(),
            generator = MissionGenerator(),
            stepSource = FakeStepCounterSource(), // 2주차에 센서 구현으로 교체
            clock = AndroidActivityClock
        )
    }
}

private object AndroidActivityClock : ActivityClock {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun today(): LocalDate = LocalDate.now()
    override fun nowTime(): LocalTime = LocalTime.now()
}