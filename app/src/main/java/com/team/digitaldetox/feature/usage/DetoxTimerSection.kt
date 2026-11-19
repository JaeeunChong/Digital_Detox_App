package com.team.digitaldetox.feature.usage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val TimerGreen = Color(0xFF477A55)
private val TimerBeige = Color(0xFFFDEBD2)
private val TimerText = Color(0xFF303D32)
private const val MAX_TIMER_MINUTES = 24 * 60
private const val MILLIS_PER_MINUTE = 60_000L

private fun formatTimer(millis: Long): String {
    val seconds = (millis.coerceAtLeast(0L) + 999L) / 1000L
    val hours = seconds / 3600L
    val minutes = (seconds % 3600L) / 60L
    val remainingSeconds = seconds % 60L
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
    } else {
        "%02d:%02d".format(minutes, remainingSeconds)
    }
}

private fun formatRewardTime(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0L) / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
}

@Composable
fun DetoxTimerSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scheduleRepository = remember(context) { SleepScheduleRepository(context) }
    val rewardStore = remember(context) { DetoxRewardStore(context) }
    val schedule = remember(scheduleRepository) { scheduleRepository.getSchedule() }

    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedMinutes by rememberSaveable { mutableIntStateOf(0) }
    var remainingMillis by rememberSaveable { mutableLongStateOf(0L) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var isAwaitingSleepChoice by rememberSaveable { mutableStateOf(false) }
    var continueThroughSleep by rememberSaveable { mutableStateOf(false) }
    var sessionStarted by rememberSaveable { mutableStateOf(false) }
    var sessionClosed by rememberSaveable { mutableStateOf(false) }
    var runStartedAtMillis by rememberSaveable { mutableLongStateOf(0L) }
    var runStartedWithMillis by rememberSaveable { mutableLongStateOf(0L) }
    var lastResult by rememberSaveable { mutableStateOf("") }
    var totalEligibleMillis by remember {
        mutableLongStateOf(rewardStore.totalEligibleMillis())
    }

    // A 화면이 열려 있는 동안 현재 시각을 갱신한다.
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(250L)
        }
    }

    // 1초씩 감소시키는 대신 실제 경과 시간으로 남은 시간을 계산한다.
    // 따라서 화면이 잠시 멈춰도 다시 그려질 때 경과 시간이 반영된다.
    LaunchedEffect(isRunning, nowMillis) {
        if (!isRunning) return@LaunchedEffect
        val finishAtMillis = runStartedAtMillis + runStartedWithMillis
        val bedtimeAtMillis = schedule.nextBedtimeAfter(runStartedAtMillis)

        if (!continueThroughSleep &&
            schedule.bedHour * 60 + schedule.bedMinute !=
            schedule.wakeHour * 60 + schedule.wakeMinute &&
            bedtimeAtMillis < finishAtMillis &&
            nowMillis >= bedtimeAtMillis
        ) {
            // 23:00에 정확히 멈춘 것으로 계산한다. 팝업 대기 시간은 보상 제외.
            remainingMillis = (finishAtMillis - bedtimeAtMillis).coerceAtLeast(0L)
            isRunning = false
            isAwaitingSleepChoice = true
        } else {
            remainingMillis = (finishAtMillis - nowMillis).coerceAtLeast(0L)
            if (remainingMillis == 0L) {
                isRunning = false
                sessionClosed = true
                // '이번만 계속'으로 취침 시간을 넘긴 경우에도 전 시간을 인정한다.
                val earned = selectedMinutes.toLong() * MILLIS_PER_MINUTE
                rewardStore.recordEligibleTime(earned)
                totalEligibleMillis = rewardStore.totalEligibleMillis()
                lastResult = "디톡스 완료! ${formatRewardTime(earned)}이 보상 인정 시간에 포함됐어요."
                continueThroughSleep = false
                sessionStarted = false
            }
        }
    }

    fun adjustMinutes(delta: Int) {
        if (isRunning || isAwaitingSleepChoice || continueThroughSleep) return
        val updated = (selectedMinutes + delta).coerceIn(0, MAX_TIMER_MINUTES)
        val actualDelta = updated - selectedMinutes
        if (actualDelta == 0) return
        selectedMinutes = updated
        if (sessionClosed) {
            remainingMillis = updated.toLong() * MILLIS_PER_MINUTE
            sessionClosed = false
            sessionStarted = false
        } else {
            remainingMillis = (remainingMillis + actualDelta * MILLIS_PER_MINUTE)
                .coerceIn(0L, updated.toLong() * MILLIS_PER_MINUTE)
        }
        lastResult = ""
    }

    fun resetTimer(clearSelection: Boolean) {
        isRunning = false
        isAwaitingSleepChoice = false
        continueThroughSleep = false
        sessionStarted = false
        sessionClosed = false
        if (clearSelection) selectedMinutes = 0
        remainingMillis = selectedMinutes.toLong() * MILLIS_PER_MINUTE
        lastResult = ""
    }

    val isSleepTime = schedule.isSleeping(nowMillis)
    val canChangeTime = !isRunning && !isAwaitingSleepChoice && !continueThroughSleep
    val isFinished = selectedMinutes > 0 && remainingMillis == 0L && sessionClosed
    val isPaused = sessionStarted && !isRunning && !isAwaitingSleepChoice

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TimerBeige)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("디지털 디톡스 타이머", fontSize = 20.sp,
                fontWeight = FontWeight.Bold, color = TimerText)
            Text("시간을 누를 때마다 누적돼요. 최대 24시간이에요.",
                fontSize = 13.sp, color = TimerText)
            Text("취침 ${schedule.bedtimeLabel()} · 기상 ${schedule.wakeTimeLabel()}",
                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TimerGreen)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 60).forEach { minutes ->
                    OutlinedButton(
                        onClick = { adjustMinutes(minutes) },
                        enabled = canChangeTime,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("+${minutes}분", fontSize = 13.sp, maxLines = 1)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { adjustMinutes(-1) },
                    enabled = canChangeTime && selectedMinutes > 0,
                    modifier = Modifier.weight(1f)
                ) { Text("-1분") }
                OutlinedButton(
                    onClick = { adjustMinutes(1) },
                    enabled = canChangeTime && selectedMinutes < MAX_TIMER_MINUTES,
                    modifier = Modifier.weight(1f)
                ) { Text("+1분") }
            }

            Text("설정한 총 시간: ${selectedMinutes}분",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = TimerText, fontSize = 14.sp)
            Text(formatTimer(remainingMillis),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = TimerGreen, fontSize = 44.sp, fontWeight = FontWeight.Bold)

            Text(
                text = when {
                    isAwaitingSleepChoice -> "취침 시간이 되어 일시정지했어요."
                    continueThroughSleep -> "이번 타이머만 취침 시간에도 진행해요. 보상에도 포함돼요."
                    isSleepTime && !isRunning -> "현재 취침 시간이라 새 타이머를 시작할 수 없어요."
                    isFinished -> "디톡스 시간이 끝났어요!"
                    isRunning -> "디톡스 진행 중이에요."
                    isPaused -> "일시정지했어요."
                    else -> "시간을 추가하고 시작해 보세요."
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = TimerText, fontSize = 13.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = selectedMinutes > 0 && !isAwaitingSleepChoice &&
                            (isRunning || !isSleepTime || continueThroughSleep),
                    onClick = {
                        if (isRunning) {
                            remainingMillis = (runStartedWithMillis -
                                    (System.currentTimeMillis() - runStartedAtMillis))
                                .coerceAtLeast(0L)
                            isRunning = false
                        } else {
                            if (remainingMillis == 0L || sessionClosed) {
                                remainingMillis = selectedMinutes.toLong() * MILLIS_PER_MINUTE
                                sessionClosed = false
                                sessionStarted = false
                                continueThroughSleep = false
                            }
                            sessionStarted = true
                            runStartedAtMillis = System.currentTimeMillis()
                            runStartedWithMillis = remainingMillis
                            lastResult = ""
                            isRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TimerGreen)
                ) {
                    Text(
                        when {
                            isRunning -> "일시정지"
                            isFinished -> "다시 시작"
                            isPaused -> "이어하기"
                            else -> "시작하기"
                        },
                        fontSize = 13.sp
                    )
                }
                OutlinedButton(
                    enabled = selectedMinutes > 0 && !isAwaitingSleepChoice,
                    onClick = { resetTimer(clearSelection = false) },
                    modifier = Modifier.weight(1f)
                ) { Text("타이머 초기화", fontSize = 13.sp) }
            }

            TextButton(
                enabled = canChangeTime && selectedMinutes > 0,
                modifier = Modifier.align(Alignment.End),
                onClick = { resetTimer(clearSelection = true) }
            ) { Text("설정 시간 모두 지우기", color = TimerGreen, fontSize = 13.sp) }

            if (lastResult.isNotEmpty()) {
                Text(lastResult, color = TimerGreen,
                    fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
            Text("누적 보상 인정 시간: ${formatRewardTime(totalEligibleMillis)}",
                color = TimerGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("보상 인정 시간은 저장되며, 실제 캐릭터 포인트 지급은 추후 팀 기능과 연결해요.",
                color = TimerText, fontSize = 11.sp)
        }
    }

    if (isAwaitingSleepChoice) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("취침 시간이 되었어요 🌙") },
            text = {
                Text(
                    "디톡스 타이머가 ${formatTimer(remainingMillis)} 남았어요.\n\n" +
                            "지금 종료하고 취침 모드로 전환할까요?\n" +
                            "이번만 계속하면 남은 시간도 보상에 포함돼요."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    isAwaitingSleepChoice = false
                    continueThroughSleep = false
                    sessionClosed = true
                    sessionStarted = false
                    // 취침 전 실제 진행한 시간은 보상 인정 시간에 포함한다.
                    val earned = (selectedMinutes.toLong() * MILLIS_PER_MINUTE -
                            remainingMillis).coerceAtLeast(0L)
                    rewardStore.recordEligibleTime(earned)
                    totalEligibleMillis = rewardStore.totalEligibleMillis()
                    lastResult = "취침 모드로 전환했어요. 취침 전 " +
                            "${formatRewardTime(earned)}은 보상에 인정됐어요."
                }) { Text("종료하고 취침") }
            },
            dismissButton = {
                TextButton(onClick = {
                    isAwaitingSleepChoice = false
                    continueThroughSleep = true
                    sessionStarted = true
                    runStartedAtMillis = System.currentTimeMillis()
                    runStartedWithMillis = remainingMillis
                    lastResult = "이번 타이머만 계속해요. 남은 시간도 보상에 포함돼요."
                    isRunning = true
                }) { Text("이번만 계속") }
            }
        )
    }
}
