package com.team.digitaldetox.feature.interaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team.digitaldetox.R

internal enum class Resident(
    val displayName: String,
    @get:DrawableRes val imageRes: Int,
    val reward: String,
    val greeting: String,
    val missionLine: String,
    val rewardLine: String,
) {
    BIRD("새", R.drawable.resident_bird, "재화", "안녕! 오늘은 어떤 미션을 해 볼래?",
        "미션 성공 소식이 여기까지 들렸어!", "날아다니며 모은 재화야. 받아 줘!"),
    SQUIRREL("다람쥐", R.drawable.resident_squirrel, "선물 아이템", "도토리를 모으고 있었어. 너는 뭐 하고 있어?",
        "해냈구나! 네 소식을 들으니 기뻐.", "도토리 창고에 숨겨 둔 선물이야!"),
    RABBIT("토끼", R.drawable.resident_rabbit, "호감도 보너스", "깡충! 오늘도 만나서 반가워.",
        "한 걸음 더 나아갔네! 멋져.", "우리 더 친해졌네! 호감도 보너스를 줄게."),
}

private const val MissionsPerReward = 3

@Composable
internal fun ResidentReactionCard(resident: Resident) {
    var completedMissions by rememberSaveable(resident.name) { mutableIntStateOf(0) }
    val affinity = completedMissions % MissionsPerReward
    val rewardCount = completedMissions / MissionsPerReward
    val dialogue = when {
        completedMissions == 0 -> resident.greeting
        affinity == 0 -> resident.rewardLine
        else -> resident.missionLine
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painter = painterResource(resident.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                )
                Text(resident.displayName, style = MaterialTheme.typography.titleMedium)
            }
            Text(dialogue)
            Text("호감도 $affinity / $MissionsPerReward")
            LinearProgressIndicator(
                progress = { affinity.toFloat() / MissionsPerReward },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("획득한 보상 · ${resident.reward} ${rewardCount}회")
            Button(onClick = { completedMissions++ }) {
                Text("${resident.displayName} 미션 성공")
            }
        }
    }
}
