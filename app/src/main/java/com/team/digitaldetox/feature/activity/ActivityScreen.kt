package com.team.digitaldetox.feature.activity

import androidx.compose.runtime.Composable
import com.team.digitaldetox.ui.components.PlaceholderScreen

@Composable
fun ActivityScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "B. 현실 활동 및 미션",
        description = "걸음 수 측정, 미션 및 보상",
        onBack = onBack
    )
}
