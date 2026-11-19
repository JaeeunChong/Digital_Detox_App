
package com.team.digitaldetox.feature.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

// 앱 하나의 실제 사용 시간
data class AppUsageRecord(
    val packageName: String,
    val appName: String,
    val usageTimeMillis: Long
) {
    val usageMinutes: Long
        get() = usageTimeMillis / 60_000L
}

// 특정 날짜의 전체 사용 시간과 앱 목록
data class UsageDayReport(
    val dayStartMillis: Long,
    val totalUsageMillis: Long,
    val apps: List<AppUsageRecord>
) {
    val totalUsageMinutes: Long
        get() = totalUsageMillis / 60_000L
}

// 최근 7일 통계에 사용하는 데이터
data class DailyUsageRecord(
    val dayStartMillis: Long,
    val label: String,
    val usageTimeMillis: Long
) {
    val usageMinutes: Long
        get() = usageTimeMillis / 60_000L
}

class UsageStatsRepository(context: Context) {

    private val appContext = context.applicationContext

    private val usageStatsManager =
        appContext.getSystemService(
            Context.USAGE_STATS_SERVICE
        ) as UsageStatsManager

    private val packageManager =
        appContext.packageManager

    // 사용 정보 접근 권한 확인
    fun hasUsagePermission(): Boolean {
        return UsagePermissionHelper.hasUsageAccess(
            appContext
        )
    }

    // 오늘 사용 시간 조회
    fun getTodayUsage(): UsageDayReport? {
        return getUsageForDay(daysAgo = 0)
    }

    // 특정 날짜의 사용 시간 조회
    // 0 = 오늘, 1 = 어제, 2 = 그제
    fun getUsageForDay(
        daysAgo: Int
    ): UsageDayReport? {

        require(daysAgo >= 0) {
            "daysAgo must be 0 or greater"
        }

        if (!hasUsagePermission()) {
            return null
        }

        val start = startOfDay(daysAgo)

        val end = if (daysAgo == 0) {
            System.currentTimeMillis()
        } else {
            startOfDay(daysAgo - 1)
        }

        if (end <= start) {
            return UsageDayReport(
                dayStartMillis = start,
                totalUsageMillis = 0L,
                apps = emptyList()
            )
        }

        // Android 시스템의 실제 사용 시간 조회
        val stats = try {
            usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
            ) ?: return null
        } catch (_: SecurityException) {
            return null
        }

        // 동일 앱의 기록을 패키지별로 합산
        val durationByPackage = stats
            .asSequence()
            .filter {
                it.packageName != null
            }
            .filter {
                it.packageName != appContext.packageName
            }
            .groupBy {
                it.packageName
            }
            .mapValues { (_, rows) ->
                rows.sumOf {
                    it.totalTimeInForeground
                        .coerceAtLeast(0L)
                }
            }

        // 앱 이름 및 사용 시간 정리
        val apps = durationByPackage
            .filterValues {
                it > 0L
            }
            .map { (packageName, duration) ->
                AppUsageRecord(
                    packageName = packageName,
                    appName = resolveAppName(packageName),
                    usageTimeMillis = duration
                )
            }
            .sortedByDescending {
                it.usageTimeMillis
            }

        return UsageDayReport(
            dayStartMillis = start,
            totalUsageMillis = apps.sumOf {
                it.usageTimeMillis
            },
            apps = apps
        )
    }

    // 최근 7일 전체 사용 시간 조회
    fun getLast7Days(): List<DailyUsageRecord>? {

        if (!hasUsagePermission()) {
            return null
        }

        val formatter = SimpleDateFormat(
            "MM/dd",
            Locale.KOREA
        )

        return (6 downTo 0).map { daysAgo ->

            val report = getUsageForDay(daysAgo)
                ?: return null

            DailyUsageRecord(
                dayStartMillis = report.dayStartMillis,
                label = formatter.format(
                    Date(report.dayStartMillis)
                ),
                usageTimeMillis = report.totalUsageMillis
            )
        }
    }

    // 특정 앱의 최근 7일 사용 시간 조회
    fun getAppLast7Days(
        packageName: String
    ): List<DailyUsageRecord>? {

        if (!hasUsagePermission()) {
            return null
        }

        val formatter = SimpleDateFormat(
            "MM/dd",
            Locale.KOREA
        )

        return (6 downTo 0).map { daysAgo ->

            val report = getUsageForDay(daysAgo)
                ?: return null

            val duration = report.apps
                .firstOrNull {
                    it.packageName == packageName
                }
                ?.usageTimeMillis ?: 0L

            DailyUsageRecord(
                dayStartMillis = report.dayStartMillis,
                label = formatter.format(
                    Date(report.dayStartMillis)
                ),
                usageTimeMillis = duration
            )
        }
    }

    // 오늘 또는 이전 날짜의 자정 계산
    private fun startOfDay(
        daysAgo: Int
    ): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            add(
                Calendar.DAY_OF_YEAR,
                -daysAgo
            )
        }.timeInMillis
    }

    // 패키지 이름을 사용자에게 보이는 앱 이름으로 변환
    private fun resolveAppName(
        packageName: String
    ): String {
        return try {
            @Suppress("DEPRECATION")
            val appInfo = packageManager
                .getApplicationInfo(
                    packageName,
                    0
                )

            packageManager
                .getApplicationLabel(appInfo)
                .toString()

        } catch (_: PackageManager.NameNotFoundException) {
            // 앱 이름을 조회할 수 없다면 패키지명 사용
            packageName
        }
    }
}
