package com.team.digitaldetox.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onUsageClick: () -> Unit,
    onActivityClick: () -> Unit,
    onWorldClick: () -> Unit,
    onInteractionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Digital Detox",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("공통 개발 환경 테스트")

        Button(onClick = onUsageClick) {
            Text("A. 디지털 디톡스")
        }

        Button(onClick = onActivityClick) {
            Text("B. 활동 및 미션")
        }

        Button(onClick = onWorldClick) {
            Text("C. 마을 성장")
        }

        Button(onClick = onInteractionClick) {
            Text("D. 캐릭터 상호작용")
        }
    }
}
