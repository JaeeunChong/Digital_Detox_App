package com.team.digitaldetox.feature.interaction

import androidx.compose.runtime.Composable
import com.team.digitaldetox.ui.components.PlaceholderScreen

@Composable
fun InteractionScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "D. 캐릭터 상호작용",
        description = "주민 대화, 호감도 및 개인화",
        onBack = onBack
    )
}
