package com.team.digitaldetox.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.team.digitaldetox.feature.interaction.ForestHomeContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onUsageClick: () -> Unit,
    onActivityClick: () -> Unit,
    onWorldClick: () -> Unit,
    onInteractionClick: () -> Unit,
    nickname: String,
    character: String,
    launchHour: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ForestHomeContent(
            nickname = nickname,
            character = character,
            launchHour = launchHour,
            modifier = Modifier.fillMaxWidth().height(440.dp).clip(RoundedCornerShape(24.dp))
        )

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
