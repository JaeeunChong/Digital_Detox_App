package com.team.digitaldetox.feature.usage

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ForestGreen = Color(0xFF477A55)
private val LightGreen = Color(0xFFDCE9D4)
private val Beige = Color(0xFFFDEBD2)
private val Background = Color(0xFFF6F3E8)
private val DarkText = Color(0xFF303D32)

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}시간 ${mins}분" else "${mins}분"
}

@Composable
fun UsageScreen(onBack: () -> Unit) {

    // 현재는 실제 API가 아닌 임시 데이터
    val todayUsageMinutes = 200

    var goalMinutes by rememberSaveable {
        mutableIntStateOf(240)
    }

    val progress = (
            todayUsageMinutes.toFloat() / goalMinutes
            ).coerceIn(0f, 1f)

    val appUsage = listOf(
        "인스타그램" to 70,
        "유튜브" to 50,
        "카카오톡" to 35,
        "기타 앱" to 45
    )

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

        // 상단 제목 및 뒤로가기
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("〈 뒤로", color = ForestGreen)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "OFF:ON",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
        }

        Text(
            text = "디지털 디톡스",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Text(
            text = "오늘의 스마트폰 사용 현황",
            fontSize = 15.sp,
            color = Color.Gray
        )

        // 오늘 사용 시간
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Beige
            )
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
                    formatMinutes(todayUsageMinutes),
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
                    text = "목표 ${formatMinutes(goalMinutes)} 중 " +
                            "${(progress * 100).toInt()}% 사용",
                    fontSize = 14.sp,
                    color = DarkText
                )

                Text(
                    text = if (todayUsageMinutes <= goalMinutes) {
                        "아직 목표 사용 시간 이내예요!"
                    } else {
                        "오늘의 목표 사용 시간을 초과했어요."
                    },
                    fontSize = 14.sp,
                    color = ForestGreen
                )
            }
        }

        // 목표 시간 설정
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    "하루 사용 시간 목표",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            goalMinutes = (goalMinutes - 30)
                                .coerceAtLeast(30)
                        }
                    ) {
                        Text("- 30분")
                    }

                    Text(
                        formatMinutes(goalMinutes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )

                    OutlinedButton(
                        onClick = {
                            goalMinutes = (goalMinutes + 30)
                                .coerceAtMost(1440)
                        }
                    ) {
                        Text("+ 30분")
                    }
                }
            }
        }

        // 디지털 디톡스 타이머
        DetoxTimerSection()

        // 앱별 사용 기록
        Text(
            "앱별 사용 시간",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                appUsage.forEach { (name, minutes) ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(name, color = DarkText)

                            Text(
                                formatMinutes(minutes),
                                color = ForestGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        LinearProgressIndicator(
                            progress = {
                                minutes.toFloat() / todayUsageMinutes
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

        // 최근 7일 사용 시간
        Text(
            "최근 7일 사용 시간",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Beige
            )
        ) {
            val weekUsage = listOf(
                "월" to 220,
                "화" to 180,
                "수" to 200,
                "목" to 260,
                "금" to 150,
                "토" to 200,
                "일" to 200
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                weekUsage.forEach { (day, minutes) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    (minutes / 300f * 100f).dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 5.dp,
                                        topEnd = 5.dp
                                    )
                                )
                                .background(ForestGreen)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            day,
                            fontSize = 13.sp,
                            color = DarkText
                        )
                    }
                }
            }
        }

        Text(
            text = "현재 화면의 사용 시간은 UI 테스트용 예시 데이터입니다.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
