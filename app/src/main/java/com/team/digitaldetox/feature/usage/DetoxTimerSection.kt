package com.team.digitaldetox.feature.usage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val TimerGreen = Color(0xFF477A55)
private val TimerBeige = Color(0xFFFDEBD2)
private val TimerText = Color(0xFF303D32)

@Composable
fun DetoxTimerSection(modifier: Modifier = Modifier) {
    var selectedMinutes by rememberSaveable { mutableIntStateOf(30) }
    var remainingSeconds by rememberSaveable { mutableIntStateOf(30 * 60) }
    var isRunning by rememberSaveable { mutableStateOf(false) }

    // 화면이 표시되고 타이머가 실행 중일 때 1초마다 갱신합니다.
    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1_000L)
            remainingSeconds -= 1
        }
        if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    val finished = remainingSeconds == 0
    val minutesText = (remainingSeconds / 60).toString().padStart(2, '0')
    val secondsText = (remainingSeconds % 60).toString().padStart(2, '0')

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TimerBeige)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "디지털 디톡스 타이머",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimerText
            )
            Text(
                text = "휴대폰을 잠시 내려놓고 쉬어 보세요.",
                fontSize = 14.sp,
                color = TimerText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 60).forEach { minutes ->
                    FilterChip(
                        selected = selectedMinutes == minutes,
                        onClick = {
                            if (!isRunning) {
                                selectedMinutes = minutes
                                remainingSeconds = minutes * 60
                            }
                        },
                        enabled = !isRunning,
                        label = { Text("${minutes}분") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDCE9D4),
                            selectedLabelColor = TimerGreen
                        )
                    )
                }
            }

            Text(
                text = "$minutesText:$secondsText",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = TimerGreen
            )

            Text(
                text = when {
                    finished -> "선택한 디톡스 시간이 끝났어요!"
                    isRunning -> "디톡스 진행 중이에요."
                    remainingSeconds < selectedMinutes * 60 -> "일시정지했어요."
                    else -> "시간을 선택하고 시작해 보세요."
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 14.sp,
                color = TimerText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (finished) remainingSeconds = selectedMinutes * 60
                        isRunning = !isRunning
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TimerGreen)
                ) {
                    Text(if (isRunning) "일시정지" else if (finished) "다시 시작" else "시작하기")
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        remainingSeconds = selectedMinutes * 60
                    }
                ) {
                    Text("초기화", color = TimerGreen)
                }
            }
        }
    }
}
