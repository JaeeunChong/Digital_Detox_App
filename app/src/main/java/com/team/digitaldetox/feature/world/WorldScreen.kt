package com.team.digitaldetox.feature.world

import androidx.compose.runtime.Composable
import com.team.digitaldetox.ui.components.PlaceholderScreen

@Composable
fun WorldScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "C. 마을 성장",
        description = "마을, 건물 및 캐릭터 성장",
        onBack = onBack
    )
}
