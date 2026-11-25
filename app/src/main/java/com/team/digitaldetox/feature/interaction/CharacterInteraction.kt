package com.team.digitaldetox.feature.interaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.team.digitaldetox.R
import kotlin.random.Random

val defaultCharacterDialogues: List<String> = listOf(
    "안녕하세요! 오늘도 만나서 반가워요.",
    "잠깐 휴대폰을 내려놓고 기지개를 켜 볼까요?",
    "작은 쉼도 멋진 시작이에요!",
    "오늘은 어떤 즐거운 일이 있었나요?",
    "서두르지 않아도 괜찮아요. 함께 천천히 걸어요."
)

/** Chooses another item uniformly, without retry loops or immediate repeats. */
internal fun nextDialogueIndex(count: Int, previousIndex: Int, random: Random = Random.Default): Int {
    require(count > 0)
    if (count == 1) return 0
    if (previousIndex !in 0 until count) return random.nextInt(count)
    val candidate = random.nextInt(count - 1)
    return if (candidate >= previousIndex) candidate + 1 else candidate
}

/**
 * Background-free D component for placement over C's future village UI.
 * State is local UI state only; no profile storage, rewards, or navigation.
 * Example: CharacterInteraction(modifier = Modifier.align(Alignment.BottomCenter))
 */
@Composable
fun CharacterInteraction(
    modifier: Modifier = Modifier,
    character: String = "boy",
    nickname: String = "친구",
    dialogues: List<String> = defaultCharacterDialogues,
    characterSize: Dp = 150.dp
) {
    val messages = remember(dialogues) {
        dialogues.filter { it.isNotBlank() }.distinct().ifEmpty { defaultCharacterDialogues }
    }
    var selectedIndex by rememberSaveable(messages, character) { mutableStateOf(-1) }
    val message = messages.getOrNull(selectedIndex)
    val displayName = nickname.ifBlank { "친구" }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Keep the touch target in place when a short bubble appears.
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = 112.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            contentAlignment = Alignment.Center
        ) {
            if (message != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFF6E5),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(18.dp),
                        color = Color(0xFF354C32),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Image(
            bitmap = ImageBitmap.imageResource(
                id = if (character == "girl") R.drawable.character_girl else R.drawable.character_boy
            ),
            contentDescription = "${displayName}의 캐릭터",
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None,
            modifier = Modifier.size(characterSize).clickable(
                role = Role.Button,
                onClickLabel = "다른 대사 보기",
                onClick = { selectedIndex = nextDialogueIndex(messages.size, selectedIndex) }
            )
        )
    }
}
