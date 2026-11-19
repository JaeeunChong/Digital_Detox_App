package com.team.digitaldetox.feature.world

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team.digitaldetox.R

// Same palette as the onboarding paper panels.
private val Ink = Color(0xFF552B12)
private val DarkEdge = Color(0xFF69370F)
private val Paper = Color(0xFFFDEBD2)
private val BarTrack = Color(0xFFF5D9B8)
private val Green = Color(0xFF73C33B)

/**
 * C. Village screen: the village map with the level (affinity) card and leaf counter on top.
 *
 * @param onBack Returns to the test home.
 * @param progress Numbers to show. Defaults to [VillageProgress.Sample] until the data
 *   sources (residents' affinity, mission coins) are agreed in issue #4.
 * @param character "boy" or "girl", the character chosen in onboarding.
 */
@Composable
fun WorldScreen(
    onBack: () -> Unit,
    progress: VillageProgress = VillageProgress.Sample,
    character: String = "boy"
) {
    // Decode the character once and share it: the PNG is large, so loading it twice wastes memory.
    val characterBitmap = ImageBitmap.imageResource(
        if (character == "girl") R.drawable.character_girl else R.drawable.character_boy
    )
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.village_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.offon_logo),
                    contentDescription = "OFF:ON",
                    modifier = Modifier.height(36.dp)
                )
                Spacer(Modifier.weight(1f))
                LeafCounter(progress.leaves)
            }
            Spacer(Modifier.height(12.dp))
            LevelCard(progress, characterBitmap, Modifier.align(Alignment.Start))
            Spacer(Modifier.weight(1f))
            BouncingCharacter(characterBitmap)
            Spacer(Modifier.weight(1f))
            Button(onClick = onBack) { Text("홈으로 돌아가기") }
        }
    }
}

/**
 * Village character that bobs in place forever: the body squashes down and stretches back
 * while the feet stay on the ground. Uses the existing image (no extra frames).
 */
@Composable
private fun BouncingCharacter(characterBitmap: ImageBitmap) {
    val bounce = rememberInfiniteTransition(label = "characterBounce")
    val heightScale by bounce.animateFloat(
        initialValue = 1f,
        targetValue = SquashedHeightScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = BounceHalfCycleMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "characterHeightScale"
    )
    Image(
        bitmap = characterBitmap,
        contentDescription = "마을에 있는 캐릭터",
        modifier = Modifier
            .size(140.dp)
            // Scale in the draw phase only, so the layout around the character stays still.
            .graphicsLayer {
                transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = FeetLine)
                scaleY = heightScale
            },
        filterQuality = FilterQuality.None
    )
}

/** Height of the character at the lowest point of a bob (1.0 = normal height). */
private const val SquashedHeightScale = 0.94f

/** Time to squash once (the same time again to stretch back), in milliseconds. */
private const val BounceHalfCycleMillis = 450

/**
 * Where the feet are, as a share of the image height. character_boy and character_girl
 * both have about 7.5% transparent space under the feet, so the pivot sits at 92.5%.
 */
private const val FeetLine = 0.925f

/** Leaf currency pill in the top right corner. */
@Composable
private fun LeafCounter(leaves: Int) {
    val text = formatLeaves(leaves)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Paper)
            .border(2.5.dp, DarkEdge, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            // Read as one phrase; the emoji and number inside are not read again.
            .clearAndSetSemantics { contentDescription = "나뭇잎 $text 개" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // TODO: replace with a leaf icon from the designer.
        Text("🍃", fontSize = 20.sp)
        Text(text, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}

/** Level, cumulative affinity bar and affinity numbers next to the character portrait. */
@Composable
private fun LevelCard(progress: VillageProgress, characterBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .widthIn(max = 260.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Paper)
            .border(2.5.dp, DarkEdge, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            bitmap = characterBitmap,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            filterQuality = FilterQuality.None
        )
        Column(Modifier.weight(1f)) {
            Text("Lv. ${progress.level}", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Box(
                Modifier
                    .padding(vertical = 6.dp)
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(BarTrack)
                    .semantics {
                        contentDescription = "호감도"
                        progressBarRangeInfo = ProgressBarRangeInfo(progress.affinityFraction, 0f..1f)
                    }
            ) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress.affinityFraction).background(Green))
            }
            Text(
                "${progress.affinity} / ${progress.affinityToNextLevel}",
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
