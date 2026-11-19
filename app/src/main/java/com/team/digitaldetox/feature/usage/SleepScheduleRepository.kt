package com.team.digitaldetox.feature.usage

import android.content.Context
import java.util.Calendar

/** 온보딩에서 입력한 취침/기상 시간을 하루 중 분 단위로 보관한다. */
data class SleepSchedule(
    val bedHour: Int = 23,
    val bedMinute: Int = 0,
    val wakeHour: Int = 7,
    val wakeMinute: Int = 0
) {
    private val bedTimeMinutes: Int get() = bedHour * 60 + bedMinute
    private val wakeTimeMinutes: Int get() = wakeHour * 60 + wakeMinute

    /** 자정을 넘기는 취침 시간(23:00 ~ 07:00)도 지원한다. */
    fun isSleeping(atMillis: Long = System.currentTimeMillis()): Boolean {
        if (bedTimeMinutes == wakeTimeMinutes) return false
        val calendar = Calendar.getInstance().apply { timeInMillis = atMillis }
        val minuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 +
                calendar.get(Calendar.MINUTE)
        return if (bedTimeMinutes < wakeTimeMinutes) {
            minuteOfDay >= bedTimeMinutes && minuteOfDay < wakeTimeMinutes
        } else {
            minuteOfDay >= bedTimeMinutes || minuteOfDay < wakeTimeMinutes
        }
    }

    /** 기준 시각보다 뒤에 있는 첫 취침 시작 시각을 밀리초로 반환한다. */
    fun nextBedtimeAfter(fromMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = fromMillis
            set(Calendar.HOUR_OF_DAY, bedHour)
            set(Calendar.MINUTE, bedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= fromMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    fun bedtimeLabel(): String = "%02d:%02d".format(bedHour, bedMinute)
    fun wakeTimeLabel(): String = "%02d:%02d".format(wakeHour, wakeMinute)
}

class SleepScheduleRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "offon_sleep_schedule", Context.MODE_PRIVATE
    )

    fun getSchedule(): SleepSchedule = SleepSchedule(
        bedHour = preferences.getInt("bed_hour", 23),
        bedMinute = preferences.getInt("bed_minute", 0),
        wakeHour = preferences.getInt("wake_hour", 7),
        wakeMinute = preferences.getInt("wake_minute", 0)
    )

    fun save(bedHour: Int, bedMinute: Int, wakeHour: Int, wakeMinute: Int) {
        require(bedHour in 0..23 && wakeHour in 0..23)
        require(bedMinute in 0..59 && wakeMinute in 0..59)
        preferences.edit()
            .putInt("bed_hour", bedHour)
            .putInt("bed_minute", bedMinute)
            .putInt("wake_hour", wakeHour)
            .putInt("wake_minute", wakeMinute)
            .apply()
    }
}
