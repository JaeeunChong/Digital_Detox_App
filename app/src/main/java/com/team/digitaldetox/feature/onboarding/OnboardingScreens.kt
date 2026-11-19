package com.team.digitaldetox.feature.onboarding

import android.app.TimePickerDialog
import com.team.digitaldetox.feature.usage.SleepScheduleRepository
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/*
 * OFF:ON Figma 대비 레이아웃 수정본
 * 기존 AppNavGraph의 시그니처를 그대로 유지합니다.
 *
 * 기존 drawable: offon_logo, icon_sleep, icon_rabbit, icon_phone_check,
 * icon_hat, btn_next, app_calendar, app_memo, app_file, app_message,
 * app_phone, app_music, check_filled, check_empty,
 * character_boy, character_girl
 *
 * 온보딩 배경:
 * village_background.png 한 가지만 사용합니다.
 * 해당 파일이 없을 때만 임시 그라데이션을 표시합니다.
 *
 * 종이 패널은 별도 이미지가 없는 상태라 Canvas로 계단식 테두리를 구현했습니다.
 */
private val Ink = Color(0xFF552B12)
private val DarkEdge = Color(0xFF69370F)
private val Edge = Color(0xFFA66A3B)
private val Paper = Color(0xFFFDEBD2)
private val PaperLight = Color(0xFFFFF1DE)
private val InnerPaper = Color(0xFFFFE9CA)
private val Green = Color(0xFF73C33B)
private val DarkGreen = Color(0xFF418C22)

@Composable
private fun imageId(name: String): Int {
    val context = LocalContext.current
    @Suppress("DiscouragedApi")
    return remember(context, name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}

@Composable
private fun DesignImage(
    name: String,
    fallback: String,
    modifier: Modifier = Modifier,
    fallbackSp: Int = 28,
    scale: ContentScale = ContentScale.Fit
) {
    val id = imageId(name)
    if (id != 0) {
        Image(
            painter = painterResource(id),
            contentDescription = fallback,
            modifier = modifier,
            contentScale = scale
        )
    } else {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(fallback, fontSize = fallbackSp.sp, color = Ink)
        }
    }
}

@Composable
private fun ForestBackground() {
    Box(Modifier.fillMaxSize()) {
        // 온보딩 배경은 village_background.png로 고정합니다.
        // 파일 위치: app/src/main/res/drawable/village_background.png
        val bg = imageId("village_background")
        if (bg != 0) {
            Image(
                painter = painterResource(bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0f to Color(0xFF8CCAFF),
                        .47f to Color(0xFF9BDAEE),
                        .73f to Color(0xFFAFE17A),
                        1f to Color(0xFF8ED16B)
                    )
                )
            )
            // 원본 픽셀 숲 배경을 받기 전의 임시 장식입니다.
            if (imageId("icon_tree") != 0) {
                DesignImage(
                    "icon_tree", "",
                    Modifier.align(Alignment.TopStart).offset(x = (-68).dp, y = (-65).dp).size(210.dp)
                )
                DesignImage(
                    "icon_tree", "",
                    Modifier.align(Alignment.TopEnd).offset(x = 65.dp, y = (-66).dp).size(210.dp)
                )
            }
        }
    }
}

/** 지나온 단계까지 초록색으로 칠하는 누적 진행 표시 */
@Composable
private fun StepProgress(current: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (step in 1..3) {
            Box(
                Modifier.size(29.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(if (step <= current) Green else Color(0xFFFFECD7))
                    .border(1.5.dp, if (step <= current) DarkGreen else Color(0xFFCB9D79), CircleShape)
            )
            if (step != 3) {
                Box(
                    Modifier.width(42.dp).height(7.dp)
                        .background(if (step < current) Green else Color(0xFFF5D9B8))
                )
            }
        }
    }
}

/** 종이패널의 계단형 외곽선. 별도 패널 PNG를 받으면 이미지로 바꿔도 됩니다. */
private fun paperOutline(w: Float, h: Float, inset: Float, corner: Float): Path {
    val l = inset
    val r = w - inset
    val t = inset + 4f
    val b = h - inset
    val c = corner
    return Path().apply {
        moveTo(l + c, t)
        lineTo(r - c * 1.6f, t)
        // 우측 위의 작은 픽셀 돌출부
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
private fun PaperPanel(
    modifier: Modifier,
    topPadding: Dp = 34.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val corner = 30.dp.toPx()
            drawPath(paperOutline(w, h, 0f, corner), DarkEdge)
            drawPath(paperOutline(w, h, 4.dp.toPx(), corner), Color(0xFFDCAD79))
            drawPath(
                paperOutline(w, h, 7.dp.toPx(), corner),
                Brush.verticalGradient(listOf(PaperLight, Paper, Color(0xFFFFE9D0)))
            )
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(
                start = 23.dp, end = 23.dp, top = topPadding, bottom = 37.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

/**
 * 디자인 리소스의 투명 테두리를 제거합니다.
 * 원본 btn_next.png의 투명 여백까지 함께 축소되면 실제 버튼이 납작하게 보이므로
 * 보이는 그림 영역만 추출해서 지정한 크기만큼 채웁니다.
 */
private fun trimTransparentEdges(original: Bitmap): Bitmap {
    val width = original.width
    val height = original.height
    val pixels = IntArray(width * height)
    original.getPixels(pixels, 0, width, 0, 0, width, height)
    var left = width
    var right = -1
    var top = height
    var bottom = -1
    for (y in 0 until height) {
        for (x in 0 until width) {
            val alpha = pixels[y * width + x] ushr 24
            if (alpha > 16) {
                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }
    }
    return if (right < left || bottom < top) original
    else Bitmap.createBitmap(original, left, top, right - left + 1, bottom - top + 1)
}

@Composable
private fun NextImageButton(enabled: Boolean = true, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageResource = imageId("btn_next")
    // 리소스가 바뀌지 않는 동안에는 이미지를 한 번만 가공합니다.
    val buttonBitmap = remember(context, imageResource) {
        if (imageResource == 0) null
        else runCatching {
            val options = BitmapFactory.Options().apply { inScaled = false }
            BitmapFactory.decodeResource(context.resources, imageResource, options)
                ?.let(::trimTransparentEdges)
                ?.asImageBitmap()
        }.getOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.84f)
            .requiredHeight(58.dp) // 버튼 공간의 높이를 유지해 세로로 눌리지 않도록 함
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (buttonBitmap != null) {
            Image(
                bitmap = buttonBitmap,
                contentDescription = "다음",
                modifier = Modifier.fillMaxSize().alpha(if (enabled) 1f else 0.55f),
                // 가로·세로 비율을 유지해 원본 버튼 이미지가 찌그러지지 않도록 합니다.
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                Modifier.fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (enabled) Green else Color.LightGray)
                    .border(3.dp, DarkEdge, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("다음 ❯", color = Color.White,
                    fontWeight = FontWeight.Black, fontSize = 23.sp)
            }
        }
    }
}

@Composable
private fun PageTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        color = Ink,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
    )
    if (subtitle != null) {
        Spacer(Modifier.height(10.dp))
        Text(
            text = subtitle,
            color = Ink,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingScaffold(
    stage: Int,
    body: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        ForestBackground()
        // 세로로 긴 에뮬레이터에서도 패널이 아래까지 이어지도록 크기를 계산합니다.
        val paperHeight: Dp = (maxHeight - 225.dp).coerceAtLeast(620.dp)
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 13.dp, end = 13.dp, top = 4.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DesignImage(
                "offon_logo", "OFF:ON", Modifier.fillMaxWidth(.43f).height(47.dp), 27
            )
            Spacer(Modifier.height(2.dp))
            StepProgress(stage)
            Spacer(Modifier.height(11.dp))
            PaperPanel(
                modifier = Modifier.fillMaxWidth().height(paperHeight),
                topPadding = if (stage == 3) 28.dp else 34.dp,
                content = body
            )
        }
    }
}

@Composable
fun SleepSetupScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    val storedSleepTime = remember(context) {
        SleepScheduleRepository(context).getSchedule()
    }
    var sleepHour by rememberSaveable { mutableStateOf(storedSleepTime.bedHour) }
    var sleepMinute by rememberSaveable { mutableStateOf(storedSleepTime.bedMinute) }
    var wakeHour by rememberSaveable { mutableStateOf(storedSleepTime.wakeHour) }
    var wakeMinute by rememberSaveable { mutableStateOf(storedSleepTime.wakeMinute) }

    OnboardingScaffold(stage = 1) {
        DesignImage("icon_sleep", "🌙", Modifier.size(90.dp), 52)
        Spacer(Modifier.height(13.dp))
        PageTitle("취침 시간을 설정해요")
        Spacer(Modifier.height(34.dp))

        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(InnerPaper)
                .border(2.dp, Color(0xFFC79A72), RoundedCornerShape(21.dp))
                .padding(horizontal = 17.dp, vertical = 19.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TimeChoice("취침 시간", sleepHour, sleepMinute) {
                    TimePickerDialog(
                        context, { _, h, m -> sleepHour = h; sleepMinute = m },
                        sleepHour, sleepMinute, true
                    ).show()
                }
                TimeChoice("기상 시간", wakeHour, wakeMinute) {
                    TimePickerDialog(
                        context, { _, h, m -> wakeHour = h; wakeMinute = m },
                        wakeHour, wakeMinute, true
                    ).show()
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DesignImage("icon_rabbit", "🐰", Modifier.size(94.dp), 55)
            Spacer(Modifier.width(5.dp))
            Box(Modifier.width(185.dp)) {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFFFF2DC))
                        .border(2.5.dp, DarkEdge, RoundedCornerShape(22.dp))
                        .padding(horizontal = 7.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "설정한 시간 동안은\n디톡스 타이머가\n작동하지 않아요!",
                        color = Ink, fontSize = 17.sp, lineHeight = 23.sp,
                        fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.weight(.8f))
        NextImageButton(onClick = {
            SleepScheduleRepository(context).save(
                bedHour = sleepHour,
                bedMinute = sleepMinute,
                wakeHour = wakeHour,
                wakeMinute = wakeMinute
            )
            onNext()
        })
    }
}

@Composable
private fun TimeChoice(label: String, hour: Int, minute: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1.38f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFFFF0D9))
                .border(2.dp, Color(0xFFBA8763), RoundedCornerShape(28.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 7.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    String.format(Locale.KOREA, "%02d : %02d", hour, minute),
                    color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Black
                )
                Spacer(Modifier.width(7.dp))
                Text("⌄", color = Edge, fontWeight = FontWeight.Black, fontSize = 21.sp)
            }
        }
    }
}

private data class AppOption(val key: String, val title: String, val asset: String, val fallback: String)
private val appOptions = listOf(
    AppOption("calendar", "캘린더", "app_calendar", "🗓️"),
    AppOption("memo", "메모", "app_memo", "📝"),
    AppOption("file", "파일", "app_file", "📁"),
    AppOption("message", "메시지", "app_message", "💬"),
    AppOption("phone", "전화", "app_phone", "📞"),
    AppOption("music", "음악", "app_music", "🎵")
)

@Composable
fun AllowedAppsScreen(onNext: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf("calendar,memo,file") }
    OnboardingScaffold(stage = 2) {
        DesignImage("icon_phone_check", "✅", Modifier.size(64.dp), 48)
        Spacer(Modifier.height(5.dp))
        PageTitle(
            title = "계속 사용할 앱을 선택해요",
            subtitle = "디톡스 타이머가 실행 중에도\n사용할 수 있는 앱을 미리 선택해요."
        )
        Spacer(Modifier.height(15.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFFF5E7))
                .border(2.dp, Color(0xFFAA7E59), RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column {
                appOptions.forEachIndexed { index, option ->
                    val checked = option.key in selected.split(",")
                    Row(
                        modifier = Modifier.fillMaxWidth().height(52.dp).clickable {
                            val keys = selected.split(",").filter { it.isNotBlank() }.toMutableSet()
                            if (checked) keys.remove(option.key) else keys.add(option.key)
                            selected = keys.joinToString(",")
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DesignImage(option.asset, option.fallback, Modifier.size(31.dp), 22)
                        Spacer(Modifier.width(18.dp))
                        Text(
                            option.title,
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF23170E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (checked) {
                            // 선택된 상태: 디자이너가 준 체크 PNG 사용
                            DesignImage("check_filled", "✓", Modifier.size(29.dp), 23)
                        } else {
                            Box(
                                Modifier.size(28.dp)
                                    .border(1.4.dp, Color(0xFFBDB6AD), RoundedCornerShape(8.dp))
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                    }
                    if (index != appOptions.lastIndex) {
                        Box(
                            Modifier.fillMaxWidth().height(1.dp)
                                .background(Color(0xFFC9B9A6))
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        NextImageButton(onClick = onNext)
    }
}

@Composable
fun CharacterSelectScreen(onFinish: (String, String) -> Unit) {
    var selectedCharacter by rememberSaveable { mutableStateOf("boy") }
    var nickname by rememberSaveable { mutableStateOf("") }
    // 한글 입력 중 조합이 끊기는 현상을 막기 위해 onValueChange에서 자르지 않습니다.
    val visibleCount = nickname.codePointCount(0, nickname.length)

    OnboardingScaffold(stage = 3) {
        DesignImage("icon_hat", "👒", Modifier.size(85.dp), 52)
        Spacer(Modifier.height(4.dp))
        PageTitle(
            title = "함께할 캐릭터를 선택하고\n이름을 지어주세요",
            subtitle = "디지털 디톡스 여정을\n함께할 친구예요."
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CharacterChoice(
                asset = "character_boy", fallback = "🧑‍🌾",
                selected = selectedCharacter == "boy",
                onClick = { selectedCharacter = "boy" },
                modifier = Modifier.weight(1f)
            )
            CharacterChoice(
                asset = "character_girl", fallback = "👩‍🌾",
                selected = selectedCharacter == "girl",
                onClick = { selectedCharacter = "girl" },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(InnerPaper)
                .border(2.dp, Color(0xFFCFA580), RoundedCornerShape(20.dp))
                .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 9.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "닉네임", color = Ink, fontSize = 20.sp,
                        fontWeight = FontWeight.Black, modifier = Modifier.width(78.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(27.dp))
                            .background(PaperLight)
                            .border(2.dp, Color(0xFFB78B66), RoundedCornerShape(27.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 17.sp,
                                color = Ink
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (nickname.isEmpty()) {
                                        Text(
                                            "닉네임을 입력하세요",
                                            fontSize = 16.sp,
                                            color = Color(0xFFAA927D)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "$visibleCount / 10",
                    color = Color(0xFFAA8A72),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        // 이전 수정에서 한국어 입력이 정상 작동하도록 글자 수 강제 제한을 해제했습니다.
        // 10자 표시는 디자인용이며, 추후 별도로 유효성 검사를 추가하세요.
        NextImageButton(
            enabled = nickname.isNotBlank(),
            onClick = { onFinish(selectedCharacter, nickname.trim()) }
        )
    }
}

@Composable
private fun CharacterChoice(
    asset: String,
    fallback: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(178.dp).clickable(onClick = onClick)) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 10.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(Color(0xFFE3F3CE))
                .border(
                    3.dp,
                    if (selected) Green else Color(0xFFC29B77),
                    RoundedCornerShape(21.dp)
                ).padding(7.dp),
            contentAlignment = Alignment.Center
        ) {
            // PNG 내부 투명 여백 차이 때문에 여자가 더 커 보일 수 있습니다.
            // 남녀 이미지 비율을 고려해 여자 크기를 93×115dp로 조정합니다.
            DesignImage(
                name = asset,
                fallback = fallback,
                modifier = Modifier.size(
                    width = if (asset == "character_girl") 93.dp else 111.dp,
                    height = if (asset == "character_girl") 115.dp else 126.dp
                ),
                fallbackSp = 50,
                scale = ContentScale.Fit
            )
        }
        if (selected) {
            DesignImage(
                "check_filled", "✓",
                modifier = Modifier.align(Alignment.TopEnd).size(43.dp),
                fallbackSp = 23
            )
        }
    }
}