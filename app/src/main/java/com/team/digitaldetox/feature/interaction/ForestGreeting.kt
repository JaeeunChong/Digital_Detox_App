package com.team.digitaldetox.feature.interaction

/** The device's local hour when the app navigation is first opened. */
internal fun greetingForLaunchHour(hour: Int): String {
    require(hour in 0..23)
    return when (hour) {
        in 5..11 -> "안녕하세요! 좋은 아침이에요."
        in 12..17 -> "안녕하세요! 좋은 오후에요."
        else -> "안녕하세요! 편안한 저녁 보내고 계신가요?"
    }
}
