package com.team.digitaldetox.feature.usage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ForestGreen = Color(0xFF477A55)
private val LightGreen = Color(0xFFDCE9D4)
private val Beige = Color(0xFFFDEBD2)
private val Background = Color(0xFFF6F3E8)
private val DarkText = Color(0xFF303D32)

private fun formatMinutes(minutes: Long): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}시간 ${mins}분" else "${mins}분"
}

@Composable
fun UsageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repository = remember(context) {
        UsageStatsRepository(context.applicationContext)
    }

    var hasPermission by remember {
        mutableStateOf(repository.hasUsagePermission())
    }
    var refreshCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var loadFailed by remember { mutableStateOf(false) }
    var todayReport by remember { mutableStateOf<UsageDayReport?>(null) }
    var weeklyRecords by remember {
        mutableStateOf<List<DailyUsageRecord>>(emptyList())
    }

    // 설정에서 돌아오거나 앱을 다시 열면 권한을 확인하고 데이터를 새로 읽는다.
    DisposableEffect(lifecycleOwner, repository) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = repository.hasUsagePermission()
                refreshCount += 1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 사용 기록 조회는 메인 스레드가 아닌 IO 스레드에서 실행한다.
    LaunchedEffect(hasPermission, refreshCount) {
        if (!hasPermission) {
            todayReport = null
            weeklyRecords = emptyList()
            isLoading = false
            loadFailed = false
            return@LaunchedEffect
        }

        isLoading = true
        loadFailed = false
        try {
            val (today, week) = withContext(Dispatchers.IO) {
                repository.getTodayUsage() to repository.getLast7Days()
            }
            todayReport = today
            weeklyRecords = week.orEmpty()
            loadFailed = today == null || week == null
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            todayReport = null
            weeklyRecords = emptyList()
            loadFailed = true
        } finally {
            isLoading = false
        }
    }

    var goalMinutes by rememberSaveable { mutableIntStateOf(240) }
    var selectedPackage by rememberSaveable { mutableStateOf<String?>(null) }

    val apps = todayReport?.apps.orEmpty()
    val selectedApp = apps.firstOrNull { it.packageName == selectedPackage }

    // 기존 상세 화면과 연결한다. 이 화면의 '오늘'은 실제 값이고,
    // 상세 화면 내부의 주간 데이터는 아직 예시 데이터이다.
    if (selectedApp != null) {
        UsageDetailScreen(
            appName = selectedApp.appName,
            todayMinutes = selectedApp.usageMinutes.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            onBack = { selectedPackage = null }
        )
        return
    }

    val totalMinutes = todayReport?.totalUsageMinutes
    val totalMillis = todayReport?.totalUsageMillis ?: 0L
    val progress = if (totalMinutes != null) {
        (totalMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)
    } else 0f
    val percent = if (totalMinutes != null) {
        (totalMinutes * 100 / goalMinutes).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("〈 뒤로", color = ForestGreen)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "OFF:ON",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "디지털 디톡스",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    "실제 스마트폰 사용 현황",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            TextButton(onClick = { refreshCount += 1 }) {
                Text("새로고침", color = ForestGreen)
            }
        }

        // 권한이 없을 때만 권한 설정 안내를 보여준다.
        if (!hasPermission) {
            UsagePermissionSection()
        }

        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = ForestGreen,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("사용 기록을 불러오는 중이에요.", color = DarkText)
            }
        } else if (hasPermission && loadFailed) {
            Text(
                "사용 기록을 가져오지 못했어요. 잠금 해제 상태에서 새로고침해 주세요.",
                color = Color(0xFF9B5937),
                fontSize = 14.sp
            )
        }

        // 오늘 사용 시간 및 목표 달성률
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Beige)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "오늘 총 사용 시간",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    if (totalMinutes == null) "--" else formatMinutes(totalMinutes),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = ForestGreen,
                    trackColor = LightGreen
                )
                Text(
                    text = if (percent == null) {
                        "목표 ${formatMinutes(goalMinutes.toLong())} · 사용 기록 조회 필요"
                    } else {
                        "목표 ${formatMinutes(goalMinutes.toLong())} 중 ${percent}% 사용"
                    },
                    fontSize = 14.sp,
                    color = DarkText
                )
                if (totalMinutes != null) {
                    Text(
                        if (totalMinutes <= goalMinutes) {
                            "아직 목표 사용 시간 이내예요!"
                        } else {
                            "오늘의 목표 사용 시간을 초과했어요."
                        },
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                }
            }
        }

        // 하루 목표 시간: 1분 또는 30분씩 조절할 수 있다.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "하루 사용 시간 목표",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Text(
                    text = formatMinutes(goalMinutes.toLong()),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(-30, -1, 1, 30).forEach { delta ->
                        OutlinedButton(
                            onClick = {
                                // 최소 1분 ~ 최대 24시간
                                goalMinutes = (goalMinutes + delta).coerceIn(1, 1440)
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (delta > 0) "+${delta}분" else "${delta}분",
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                Text(
                    text = "1분 단위로 세밀하게 설정할 수 있어요. (최대 24시간)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // 누적 시간 선택과 1분 단위 조절이 가능한 디톡스 타이머
        DetoxTimerSection()

        Text(
            "앱별 사용 시간",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (apps.isEmpty()) {
                    Text(
                        when {
                            !hasPermission -> "사용 정보 접근 권한을 허용하면 앱별 기록이 표시돼요."
                            isLoading -> "앱별 사용 기록을 확인하고 있어요."
                            else -> "표시할 앱 사용 기록이 없어요."
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                } else {
                    apps.forEach { app ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPackage = app.packageName },
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    app.appName,
                                    modifier = Modifier.weight(1f),
                                    color = DarkText,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    formatMinutes(app.usageMinutes),
                                    color = ForestGreen,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                            LinearProgressIndicator(
                                progress = {
                                    if (totalMillis <= 0L) {
                                        0f
                                    } else {
                                        (app.usageTimeMillis.toFloat() /
                                                totalMillis.toFloat())
                                            .coerceIn(0f, 1f)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = ForestGreen,
                                trackColor = LightGreen
                            )
                        }
                    }
                }
            }
        }

        Text(
            "최근 7일 사용 시간",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Beige)
        ) {
            if (weeklyRecords.isEmpty()) {
                Text(
                    if (!hasPermission) "권한을 허용하면 주간 기록이 표시돼요."
                    else "주간 사용 기록을 불러올 수 없어요.",
                    modifier = Modifier.padding(20.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                val maxMinutes = (weeklyRecords.maxOfOrNull { it.usageMinutes } ?: 0L)
                    .coerceAtLeast(1L)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyRecords.forEachIndexed { index, day ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${day.usageMinutes}분",
                                fontSize = 9.sp,
                                color = DarkText,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        (day.usageMinutes.toFloat() /
                                                maxMinutes.toFloat() * 94f).dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp
                                        )
                                    )
                                    .background(
                                        if (index == weeklyRecords.lastIndex) ForestGreen
                                        else LightGreen
                                    )
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(
                                if (index == weeklyRecords.lastIndex) "오늘" else day.label,
                                fontSize = 10.sp,
                                color = DarkText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Text(
            "Android 사용 기록을 기반으로 표시하며, 디지털 웰빙의 값과 차이가 날 수 있어요. " +
                    "상세 화면의 주간 그래프와 목표 저장은 다음 단계에서 연결합니다.",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}