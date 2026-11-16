package com.team.digitaldetox.feature.onboarding

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale

// Figma 시안에 가까운 임시 색상. 확정되면 ui/theme로 이동하세요.
private val Brown = Color(0xFF6A391E)
private val Cream = Color(0xFFFFF0D9)
private val Green = Color(0xFF77BB43)
private val Outline = Color(0xFFBF8E5C)

/**
 * drawable 파일이 있으면 사용하고 없으면 임시 글자를 보여줍니다.
 * 예: res/drawable/character_boy.png -> assetName = "character_boy"
 * 이 유연한 조회 방식은 초기 프로토타입용입니다. 리소스 확정 후 R.drawable 참조로 교체해도 됩니다.
 */
@Composable
private fun AssetOrText(
    assetName: String,
    fallback: String,
    modifier: Modifier = Modifier,
    fallbackSize: Int = 32
) {
    val context = LocalContext.current
    @Suppress("DiscouragedApi")
    val resId = remember(context, assetName) {
        context.resources.getIdentifier(assetName, "drawable", context.packageName)
    }
    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = fallback,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(text = fallback, fontSize = fallbackSize.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun NextButton(enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Green)
    ) {
        Text("다음 ❯", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
    }
}

@Composable
private fun OnboardingFrame(
    step: Int,
    title: String,
    subtitle: String? = null,
    heroName: String,
    heroEmoji: String,
    content: @Composable () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Figma 배경을 PNG로 넣으면 자동 표시. 없으면 임시 하늘/초원 배경.
        val context = LocalContext.current
        @Suppress("DiscouragedApi")
        val bgId = remember(context) {
            context.resources.getIdentifier("onboarding_background", "drawable", context.packageName)
        }
        if (bgId != 0) {
            Image(
                painter = painterResource(bgId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF9AD8FF), Color(0xFFBDE2A7), Color(0xFF8FCB72))
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AssetOrText(
                assetName = "offon_logo",
                fallback = "OFF:ON 🌿",
                modifier = Modifier.fillMaxWidth().height(62.dp),
                fallbackSize = 30
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in 1..3) {
                    Box(
                        Modifier.size(13.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i <= step) Green else Cream)
                            .border(1.dp, Outline, RoundedCornerShape(50))
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Cream,
                border = BorderStroke(3.dp, Outline),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AssetOrText(heroName, heroEmoji, Modifier.size(62.dp))
                    Text(
                        title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        lineHeight = 28.sp,
                        color = Brown,
                        textAlign = TextAlign.Center
                    )
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            fontSize = 12.sp,
                            color = Brown,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    content()
                    Spacer(Modifier.height(14.dp))
                    NextButton(enabled = nextEnabled, onClick = onNext)
                }
            }
        }
    }
}

@Composable
fun SleepSetupScreen(onNext: () -> Unit) {
    var sleepHour by rememberSaveable { mutableStateOf(23) }
    var sleepMinute by rememberSaveable { mutableStateOf(0) }
    var wakeHour by rememberSaveable { mutableStateOf(7) }
    var wakeMinute by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current

    OnboardingFrame(
        step = 1,
        title = "취침 시간을 설정해요",
        heroName = "sleep_icon",
        heroEmoji = "🌙",
        onNext = onNext,
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFE4C5),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Outline)
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    TimeRow("취침 시간", sleepHour, sleepMinute) {
                        TimePickerDialog(context, { _, h, m ->
                            sleepHour = h
                            sleepMinute = m
                        }, sleepHour, sleepMinute, true).show()
                    }
                    TimeRow("기상 시간", wakeHour, wakeMinute) {
                        TimePickerDialog(context, { _, h, m ->
                            wakeHour = h
                            wakeMinute = m
                        }, wakeHour, wakeMinute, true).show()
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "🐰  설정한 시간 동안은 디톡스 타이머가 작동하지 않아요!",
                fontSize = 13.sp,
                color = Brown,
                lineHeight = 19.sp
            )
        }
    )
}

@Composable
private fun TimeRow(label: String, hour: Int, minute: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = Brown, fontSize = 14.sp)
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Outline),
            color = Cream,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Text(
                String.format(Locale.KOREA, "%02d : %02d  ⌄", hour, minute),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                color = Brown
            )
        }
    }
}

private data class AllowedApp(val id: String, val title: String, val imageName: String, val emoji: String)
private val appRows = listOf(
    AllowedApp("calendar", "캘린더", "app_calendar", "🗓️"),
    AllowedApp("memo", "메모", "app_memo", "📝"),
    AllowedApp("file", "파일", "app_file", "📁"),
    AllowedApp("message", "메시지", "app_message", "💬"),
    AllowedApp("phone", "전화", "app_phone", "📞"),
    AllowedApp("music", "음악", "app_music", "🎵")
)

@Composable
fun AllowedAppsScreen(onNext: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf("calendar,memo,file") }

    OnboardingFrame(
        step = 2,
        title = "계속 사용할 앱을 선택해요",
        subtitle = "디톡스 타이머가 실행 중에도\n사용할 수 있는 앱을 미리 선택해요.",
        heroName = "apps_icon",
        heroEmoji = "✅",
        onNext = onNext,
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Outline),
                color = Color(0xFFFFFAF1)
            ) {
                Column(Modifier.padding(10.dp)) {
                    appRows.forEachIndexed { index, app ->
                        val isSelected = selected.split(",").contains(app.id)
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                val set = selected.split(",").filter { it.isNotBlank() }.toMutableSet()
                                if (isSelected) set.remove(app.id) else set.add(app.id)
                                selected = set.joinToString(",")
                            }.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssetOrText(app.imageName, app.emoji, Modifier.size(27.dp), 19)
                            Spacer(Modifier.width(12.dp))
                            Text(app.title, Modifier.weight(1f), fontSize = 14.sp, color = Brown)
                            AssetOrText(
                                if (isSelected) "check_filled" else "check_empty",
                                if (isSelected) "✅" else "☐",
                                Modifier.size(24.dp),
                                19
                            )
                        }
                        if (index < appRows.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE6D5BC)))
                        }
                    }
                }
            }
            Text(
                "※ 현재는 앱 선택 UI만 구현됩니다. 실제 앱 차단·예외 적용은 별도 개발이 필요합니다.",
                fontSize = 11.sp,
                color = Brown
            )
        }
    )
}

@Composable
fun CharacterSelectScreen(onFinish: (String, String) -> Unit) {
    var selectedCharacter by rememberSaveable { mutableStateOf("boy") }
    var nickname by rememberSaveable { mutableStateOf("") }

    OnboardingFrame(
        step = 3,
        title = "함께할 캐릭터를 선택하고\n이름을 지어주세요",
        subtitle = "디지털 디톡스 여정을 함께할 친구예요.",
        heroName = "character_hat",
        heroEmoji = "👒",
        onNext = { onFinish(selectedCharacter, nickname.trim()) },
        nextEnabled = nickname.isNotBlank(),
        content = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CharacterCard(
                    id = "boy",
                    imageName = "character_boy",
                    emoji = "🧑‍🌾",
                    checked = selectedCharacter == "boy",
                    onClick = { selectedCharacter = "boy" },
                    modifier = Modifier.weight(1f)
                )
                CharacterCard(
                    id = "girl",
                    imageName = "character_girl",
                    emoji = "👩‍🌾",
                    checked = selectedCharacter == "girl",
                    onClick = { selectedCharacter = "girl" },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                placeholder = { Text("닉네임을 입력하세요") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                isError = false,
                supportingText = {
                    Text("현재 입력 길이: ${nickname.length}자 · 입력 제한 임시 해제")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Composable
private fun CharacterCard(
    id: String,
    imageName: String,
    emoji: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (checked) Color(0xFFDFF3CB) else Color(0xFFFFF8ED))
            .border(2.dp, if (checked) Green else Outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AssetOrText(imageName, emoji, Modifier.fillMaxWidth().height(112.dp), 55)
        Spacer(Modifier.height(4.dp))
        Text(if (checked) "✔ 선택됨" else "선택", fontSize = 12.sp, color = Brown)
    }
}
