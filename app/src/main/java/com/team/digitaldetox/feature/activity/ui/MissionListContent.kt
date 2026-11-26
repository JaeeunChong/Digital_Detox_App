package com.team.digitaldetox.feature.activity.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team.digitaldetox.R
import com.team.digitaldetox.feature.activity.ActivityController
import com.team.digitaldetox.feature.activity.ActivityUiState
import com.team.digitaldetox.feature.activity.model.ActivityCategory
import com.team.digitaldetox.feature.activity.model.Mission
import com.team.digitaldetox.feature.activity.model.MissionType
import java.util.Locale

/*
 * 미션 목록 화면 (픽셀아트 버전)
 * - 저장소에 이미 있는 이미지만 사용: village_background, offon_logo, icon_sleep, check_empty, check_filled
 * - 종이 패널·색상은 온보딩 화면과 같은 값을 B 파일 안에 따로 두었습니다.
 *   (온보딩의 PaperPanel 등은 private 이라 가져다 쓸 수 없고, 공통 파일은 수정하지 않기 위해)
 */
private val Ink = Color(0xFF552B12)
private val DarkEdge = Color(0xFF69370F)
private val Tan = Color(0xFFDCAD79)
private val Edge = Color(0xFFA66A3B)
private val Paper = Color(0xFFFDEBD2)
private val PaperLight = Color(0xFFFFF1DE)
private val InnerPaper = Color(0xFFFFE9CA)
private val Green = Color(0xFF73C33B)
private val DarkGreen = Color(0xFF418C22)
private val Warn = Color(0xFFB3261E)
private val HintInk = Color(0xFF7A4A2A)

/**
 * 미션 목록. 오늘의 / 랜덤 / 내가 정한 미션을 한 화면에 모아 보여줍니다.
 * @param contentPadding 상태바·내비게이션바 영역. 배경은 화면 끝까지 그리고 내용만 안쪽에 둡니다.
 */
@Composable
fun MissionListContent(
    state: ActivityUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onStart: (Mission) -> Unit,
    onDrawRandom: () -> Unit,
    onAddCustom: (String, ActivityCategory, Int) -> Unit,
    onRemoveCustom: (String) -> Unit
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val startEnabled = state.canStartTimer

    Box(Modifier.fillMaxSize()) {
        PixelImage(R.drawable.village_background, Modifier.fillMaxSize(), ContentScale.Crop)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(start = 13.dp, end = 13.dp, top = 4.dp, bottom = 16.dp)
        ) {
            Box(Modifier.fillMaxWidth().height(52.dp)) {
                PaperButton("‹ 홈", onBack, Modifier.align(Alignment.CenterStart))
                PixelImage(
                    R.drawable.offon_logo,
                    Modifier.align(Alignment.Center).fillMaxWidth(0.43f).height(47.dp)
                )
            }
            Spacer(Modifier.height(8.dp))

            PaperPanel {
                Text(
                    "현실 활동 · 미션",
                    modifier = Modifier.fillMaxWidth(),
                    color = Ink, fontSize = 25.sp, lineHeight = 32.sp,
                    fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                )
                Text(
                    "오늘 할 현실 활동을 골라 보세요",
                    modifier = Modifier.fillMaxWidth(),
                    color = Ink, fontSize = 15.sp, textAlign = TextAlign.Center
                )

                SummaryBox(state)

                SectionHeader("오늘의 미션")
                state.dailyMissions.forEach { mission ->
                    MissionCard(
                        mission = mission,
                        done = mission.id in state.completedTodayIds,
                        showCheck = true,
                        startEnabled = startEnabled,
                        onStart = { onStart(mission) }
                    )
                }

                SectionHeader("랜덤 미션") {
                    PaperButton(if (state.randomMission == null) "뽑기" else "다시 뽑기", onDrawRandom)
                }
                val random = state.randomMission
                if (random == null) {
                    HintText("뽑기를 누르면 내용과 시간이 랜덤인 미션이 나와요. (취침 시간 제외)")
                } else {
                    MissionCard(
                        mission = random,
                        done = random.id in state.completedTodayIds,
                        showCheck = true,
                        startEnabled = startEnabled,
                        onStart = { onStart(random) }
                    )
                }

                SectionHeader("내가 정한 미션") {
                    PaperButton("+ 추가", { showAddDialog = true })
                }
                if (state.customMissions.isEmpty()) {
                    HintText("하고 싶은 활동을 직접 미션으로 추가해 보세요.")
                } else {
                    state.customMissions.forEach { mission ->
                        MissionCard(
                            mission = mission,
                            done = false, // 내가 정한 미션은 반복 가능하므로 완료 표시를 하지 않음
                            showCheck = false,
                            startEnabled = startEnabled,
                            onStart = { onStart(mission) },
                            onDelete = { onRemoveCustom(mission.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomMissionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, minutes ->
                showAddDialog = false
                onAddCustom(title, category, minutes)
            }
        )
    }
}

/** 픽셀아트가 흐려지지 않도록 확대할 때 보간 없이(FilterQuality.None) 그립니다. */
@Composable
private fun PixelImage(
    @DrawableRes id: Int,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val bitmap = ImageBitmap.imageResource(id)
    val painter = remember(bitmap) { BitmapPainter(bitmap, filterQuality = FilterQuality.None) }
    Image(painter = painter, contentDescription = null, modifier = modifier, contentScale = contentScale)
}

/** 온보딩과 같은 계단식 종이 패널 외곽선 */
private fun paperOutline(w: Float, h: Float, inset: Float, corner: Float): Path {
    val l = inset
    val r = w - inset
    val t = inset + 4f
    val b = h - inset
    val c = corner
    return Path().apply {
        moveTo(l + c, t)
        lineTo(r - c * 1.6f, t)
        lineTo(r - c * 1.6f, t - 4f)
        lineTo(r - c * .8f, t - 4f)
        lineTo(r - c * .8f, t + c * .28f)
        lineTo(r - c * .42f, t + c * .28f)
        lineTo(r - c * .42f, t + c * .55f)
        lineTo(r, t + c * .55f)
        lineTo(r, b - c)
        lineTo(r - c * .25f, b - c)
        lineTo(r - c * .25f, b - c * .45f)
        lineTo(r - c * .55f, b - c * .45f)
        lineTo(r - c * .55f, b)
        lineTo(l + c * .6f, b)
        lineTo(l + c * .6f, b - c * .18f)
        lineTo(l + c * .28f, b - c * .18f)
        lineTo(l + c * .28f, b - c * .55f)
        lineTo(l, b - c * .55f)
        lineTo(l, t + c)
        lineTo(l + c * .3f, t + c)
        lineTo(l + c * .3f, t + c * .35f)
        lineTo(l + c, t + c * .35f)
        close()
    }
}

@Composable
private fun PaperPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val corner = 30.dp.toPx()
                drawPath(paperOutline(size.width, size.height, 0f, corner), DarkEdge)
                drawPath(paperOutline(size.width, size.height, 4.dp.toPx(), corner), Tan)
                drawPath(
                    paperOutline(size.width, size.height, 7.dp.toPx(), corner),
                    Brush.verticalGradient(listOf(PaperLight, Paper, Color(0xFFFFE9D0)))
                )
            }
            .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

/** 안쪽 종이 상자(카드) 공통 모양 */
private fun Modifier.innerBox(): Modifier = this
    .fillMaxWidth()
    .clip(RoundedCornerShape(14.dp))
    .background(InnerPaper)
    .border(2.dp, Edge, RoundedCornerShape(14.dp))

@Composable
private fun SummaryBox(state: ActivityUiState) {
    Column(Modifier.innerBox().padding(horizontal = 14.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Stat("${state.todayPoints}P", "포인트(임시)", Modifier.weight(1f))
            Stat("${state.todaySuccessCount}회", "성공한 미션", Modifier.weight(1f))
            Stat(
                String.format(Locale.KOREA, "%,d", state.todaySteps),
                if (state.stepsAreReal) "걸음 수" else "걸음 수(임시)",
                Modifier.weight(1f)
            )
        }
        if (state.dailyCapReached) {
            Text(
                "오늘 받을 수 있는 포인트를 모두 받았어요.",
                color = Ink, fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFE2C29B)))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PixelImage(R.drawable.icon_sleep, Modifier.width(44.dp).height(33.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                val w = state.sleepWindow
                Text(
                    "취침 시간 " + String.format(
                        Locale.KOREA, "%02d:%02d ~ %02d:%02d",
                        w.sleepHour, w.sleepMinute, w.wakeHour, w.wakeMinute
                    ),
                    color = Ink, fontSize = 13.sp
                )
                if (!state.canStartTimer) {
                    Text(
                        "지금은 취침 시간이라 미션을 시작할 수 없어요.",
                        color = Warn, fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black, maxLines = 1)
        Text(label, color = Ink, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )
        action?.invoke()
    }
}

@Composable
private fun HintText(text: String) {
    Text(text, color = HintInk, fontSize = 13.sp)
}

/** 종이 질감의 작은 버튼 (뽑기, 추가, 홈, 취소) */
@Composable
private fun PaperButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperLight)
            .border(2.dp, Edge, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

/** 온보딩 '다음' 버튼과 같은 색의 초록 버튼 (시작, 추가) */
@Composable
private fun GreenButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.55f)
            .clip(RoundedCornerShape(12.dp))
            .background(Green)
            .drawBehind {
                val shade = 4.dp.toPx()
                drawRect(DarkGreen, topLeft = Offset(0f, size.height - shade), size = Size(size.width, shade))
            }
            .border(3.dp, DarkEdge, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun Tag(text: String, background: Color, color: Color) {
    Text(
        text,
        color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 6.dp)
    )
}

@Composable
private fun MissionCard(
    mission: Mission,
    done: Boolean,
    showCheck: Boolean,
    startEnabled: Boolean,
    onStart: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val (tagBg, tagFg) = when (mission.type) {
        MissionType.DAILY -> Color(0xFFD7EFC0) to Color(0xFF2F6B17)
        MissionType.RANDOM -> Color(0xFFFFE08A) to Color(0xFF6B4A00)
        MissionType.CUSTOM -> Color(0xFFF1D3C0) to Color(0xFF7A3A14)
    }
    Row(
        modifier = Modifier.innerBox().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showCheck) {
            PixelImage(
                if (done) R.drawable.check_filled else R.drawable.check_empty,
                Modifier.size(28.dp)
            )
            Spacer(Modifier.width(10.dp))
        }
        Column(Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Tag(mission.type.label, tagBg, tagFg)
                Tag(mission.category.label, Color(0xFFF5D9B8), Ink)
            }
            Text(
                mission.title,
                color = Ink, fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold
            )
            Text(
                "목표 ${mission.targetMinutes}분" + if (done) " · 오늘 완료" else "",
                color = Ink, fontSize = 12.sp
            )
        }
        if (onDelete != null) {
            Text(
                "삭제",
                color = Color(0xFF9A5A32), fontSize = 12.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onDelete).padding(6.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        GreenButton(if (done) "다시" else "시작", startEnabled, onStart)
    }
}

@Composable
private fun PixelChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) Green else PaperLight)
            .border(2.dp, if (selected) DarkGreen else Color(0xFFD9B08A), RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            color = if (selected) Color.White else Ink,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun AddCustomMissionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, ActivityCategory, Int) -> Unit
) {
    // 한글 조합이 깨지지 않도록 입력 중에는 글자 수를 자르지 않고, 확인 버튼에서만 검사합니다.
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(ActivityCategory.HOBBY) }
    var minutes by rememberSaveable { mutableIntStateOf(20) }
    val tooLong = title.trim().length > ActivityController.MAX_TITLE_LENGTH
    val canAdd = title.isNotBlank() && !tooLong

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Paper,
        titleContentColor = Ink,
        textContentColor = Ink,
        title = { Text("내가 정한 미션 추가", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("미션 이름") },
                    singleLine = true,
                    isError = tooLong,
                    supportingText = {
                        Text("${title.trim().length} / ${ActivityController.MAX_TITLE_LENGTH}자")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Ink,
                        unfocusedTextColor = Ink,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = DarkEdge,
                        unfocusedBorderColor = Edge,
                        focusedLabelColor = Ink,
                        unfocusedLabelColor = Edge,
                        cursorColor = Ink
                    )
                )
                Text("활동 종류", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActivityCategory.entries.forEach { c ->
                        PixelChip(c.label, c == category) { category = c }
                    }
                }
                Text("목표 시간", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CUSTOM_DURATIONS.forEach { m ->
                        PixelChip("${m}분", m == minutes) { minutes = m }
                    }
                }
            }
        },
        confirmButton = {
            GreenButton("추가", canAdd) { onConfirm(title.trim(), category, minutes) }
        },
        dismissButton = {
            PaperButton("취소", onDismiss)
        }
    )
}

private val CUSTOM_DURATIONS = listOf(10, 15, 20, 30, 45, 60)