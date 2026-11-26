package com.team.digitaldetox.feature.activity.data

/** 걸음 수 제공 (B 담당). 2주차에 SensorManager(TYPE_STEP_COUNTER) 구현을 추가합니다. */
interface StepCounterSource {
    /** 실제 측정값이면 true. 화면에 '임시 데이터' 표시 여부를 정하는 데 씁니다. */
    val isReal: Boolean
    fun todaySteps(): Int
}

/** 임시: 고정 값. 에뮬레이터 테스트용으로도 계속 사용 가능 */
class FakeStepCounterSource(private val steps: Int = 3_200) : StepCounterSource {
    override val isReal: Boolean = false
    override fun todaySteps(): Int = steps
}