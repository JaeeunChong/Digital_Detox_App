package com.team.digitaldetox.feature.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.team.digitaldetox.feature.activity.ui.MissionListContent

/**
 * B. 현실 활동 및 미션 화면 (진입점) — 1단계: 미션 목록만 표시
 * AppNavGraph의 composable("activity")에서 호출되므로 함수 이름과 매개변수는 그대로 유지합니다.
 * '시작' 버튼은 2단계(타이머)에서 연결합니다.
 */
@Composable
fun ActivityScreen(onBack: () -> Unit) {
    val controller = remember { ActivityDependencies.controller }
    var uiState by remember { mutableStateOf(controller.state()) }

    // 같은 안내 문구가 연속으로 나와도 다시 보이도록 순번을 둡니다.
    var noticeSeq by remember { mutableIntStateOf(0) }
    var notice by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun showNotice(text: String) {
        notice = text
        noticeSeq++
    }

    fun update(newState: ActivityUiState) {
        uiState = newState
        newState.notice?.let { showNotice(it) }
    }

    LaunchedEffect(noticeSeq) {
        notice?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        // 배경 그림은 화면 끝까지 그리고, 내용만 상태바 안쪽(innerPadding)에 둡니다.
        Box(modifier = Modifier.fillMaxSize()) {
            MissionListContent(
                state = uiState,
                contentPadding = innerPadding,
                onBack = onBack,
                onStart = { mission ->
                    // TODO(2단계): update(controller.startMission(mission)) 로 교체
                    showNotice("'${mission.title}' 타이머는 2단계에서 연결해요.")
                },
                onDrawRandom = { update(controller.drawRandomMission()) },
                onAddCustom = { title, category, minutes ->
                    update(controller.addCustomMission(title, category, minutes))
                },
                onRemoveCustom = { update(controller.removeCustomMission(it)) }
            )
        }
    }
}