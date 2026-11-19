
package com.team.digitaldetox.feature.usage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val DetailGreen = Color(0xFF477A55)
private val DetailLightGreen = Color(0xFFDCE9D4)
private val DetailBeige = Color(0xFFFDEBD2)
private val DetailBackground = Color(0xFFF6F3E8)
private val DetailText = Color(0xFF303D32)

private fun detailFormatTime(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60

    return if (hours > 0) {
        "${hours}시간 ${mins}분"
    } else {
        "${mins}분"
    }
}

@Composable
fun UsageDetailScreen(
    appName: String,
    todayMinutes: Int,
    onBack: () -> Unit
) {
    // 실제 API 연결 전 임시 주간 데이터
    val dayLabels = listOf(
        "6일 전", "5일 전", "4일 전",
        "3일 전", "2일 전", "어제", "오늘"
    )

    val multipliers = listOf(
        0.7f, 1.1f, 0.8f, 1.3f,
        0.6f, 0.9f, 1.0f
    )

    val weeklyMinutes = multipliers.map {
        (todayMinutes * it).roundToInt()
    }

    val weeklyAverage = weeklyMinutes.average().roundToInt()
    val weeklyTotal = weeklyMinutes.sum()
    val maxMinutes = (weeklyMinutes.maxOrNull() ?: 1)
        .coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // 뒤로가기
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("〈 뒤로", color = DetailGreen)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "OFF:ON",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = DetailGreen
            )
        }

        Text(
            text = "$appName 사용 기록",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DetailText
        )

        Text(
            text = "앱 사용 패턴을 확인해 보세요.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        // 오늘의 사용 시간
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = DetailBeige
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "오늘 사용 시간",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DetailText
                )

                Text(
                    text = detailFormatTime(todayMinutes),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetailGreen
                )

                Text(
                    text = "이번 주 하루 평균 " +
                            detailFormatTime(weeklyAverage),
                    fontSize = 14.sp,
                    color = DetailText
                )
            }
        }

        // 주간 요약
        Text(
            text = "최근 7일 사용 요약",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DetailText
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailSummaryCard(
                title = "주간 총 사용",
                value = detailFormatTime(weeklyTotal),
                modifier = Modifier.weight(1f)
            )

            DetailSummaryCard(
                title = "하루 평균",
                value = detailFormatTime(weeklyAverage),
                modifier = Modifier.weight(1f)
            )
        }

        // 주간 그래프
        Text(
            text = "최근 7일 이용 현황",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DetailText
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "일별 사용 시간",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = DetailText
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyMinutes.forEachIndexed { index, minutes ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$minutes",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(
                                        (
                                                minutes.toFloat() /
                                                        maxMinutes * 100f
                                                ).dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp
                                        )
                                    )
                                    .background(
                                        if (index == 6) {
                                            DetailGreen
                                        } else {
                                            DetailLightGreen
                                        }
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (index == 6) "오늘"
                                else "${6 - index}일",
                                fontSize = 11.sp,
                                color = DetailText
                            )
                        }
                    }
                }

                Text(
                    text = "막대 위 숫자의 단위는 분입니다.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // 일별 상세 목록
        Text(
            text = "일별 사용 기록",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DetailText
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
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                dayLabels.forEachIndexed { index, day ->

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = day,
                                color = DetailText
                            )

                            Text(
                                text = detailFormatTime(
                                    weeklyMinutes[index]
                                ),
                                color = DetailGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        LinearProgressIndicator(
                            progress = {
                                weeklyMinutes[index].toFloat() /
                                        maxMinutes
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = DetailGreen,
                            trackColor = DetailLightGreen
                        )
                    }
                }
            }
        }

        Text(
            text = "현재 사용 기록은 UI 테스트용 예시 데이터입니다.",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun DetailSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DetailBeige
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = DetailText
            )

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DetailGreen
            )
        }
    }
}