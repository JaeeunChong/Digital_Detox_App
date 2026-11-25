package com.team.digitaldetox.feature.interaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/** Standalone D demo reached through the existing home D button. */
@Composable
fun InteractionScreen(onBack: () -> Unit) {
    var character by rememberSaveable { mutableStateOf("boy") }
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("D. 캐릭터 상호작용", style = MaterialTheme.typography.headlineMedium)
        Text("동물 주민", style = MaterialTheme.typography.titleLarge)
        Text("주민별 미션 성공 버튼으로 대사와 보상을 확인해 보세요.")
        Resident.entries.forEach { resident ->
            ResidentReactionCard(resident)
        }
        Text("메인 캐릭터", style = MaterialTheme.typography.titleLarge)
        Text("캐릭터를 누르면 5개 대사 중 하나가 나와요. 다시 누르면 다른 대사를 들려줘요.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = character == "boy", onClick = { character = "boy" }, label = { Text("남자 캐릭터") })
            FilterChip(selected = character == "girl", onClick = { character = "girl" }, label = { Text("여자 캐릭터") })
        }
        ForestHomeContent(
            nickname = "친구",
            character = character,
            modifier = Modifier.fillMaxWidth().height(480.dp).clip(RoundedCornerShape(24.dp))
        )
        Button(onClick = onBack) { Text("홈으로 돌아가기") }
    }
}
