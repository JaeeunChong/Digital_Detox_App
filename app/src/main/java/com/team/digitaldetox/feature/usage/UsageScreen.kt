package com.team.digitaldetox.feature.usage

import androidx.compose.runtime.Composable
import com.team.digitaldetox.ui.components.PlaceholderScreen

@Composable
fun UsageScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "A. 디지털 디톡스",
        description = "사용 시간 측정 및 디톡스 타이머",
        onBack = onBack
    )
}
