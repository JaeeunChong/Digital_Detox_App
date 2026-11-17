package com.team.digitaldetox.feature.interaction

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.team.digitaldetox.R

/** D owns this interaction; world growth and the three residents remain separate. */
@Composable
fun ForestHomeContent(
    nickname: String,
    character: String,
    launchHour: Int,
    modifier: Modifier = Modifier
) {
    var showGreeting by rememberSaveable { mutableStateOf(false) }
    val displayName = nickname.ifBlank { "친구" }
    val greeting = greetingForLaunchHour(launchHour)

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color(0xFFCCECDD), Color(0xFF83AF73)))
        )
    ) {
        ForestBackground(Modifier.matchParentSize())
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("OFF:ON", color = Color(0xFF254C35), style = MaterialTheme.typography.headlineMedium)
            Text("${displayName}의 숲", color = Color(0xFF254C35), style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reserve bubble space so touching the character does not move it.
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showGreeting) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFF6E5),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = greeting,
                            modifier = Modifier.padding(18.dp),
                            color = Color(0xFF354C32),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Image(
                painter = painterResource(
                    if (character == "girl") R.drawable.character_girl else R.drawable.character_boy
                ),
                contentDescription = "${displayName}의 캐릭터",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(150.dp).clickable(
                    role = Role.Button,
                    onClickLabel = "인사말 보기",
                    onClick = { showGreeting = true }
                )
            )
            Surface(shape = RoundedCornerShape(50), color = Color(0xFFEAF4DC)) {
                Text("캐릭터를 터치해 보세요", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color(0xFF354C32))
            }
        }
    }
}

/** Temporary forest drawn in Compose, independent of C's world implementation. */
@Composable
private fun ForestBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(Color(0xFFFFF2B3), radius = size.minDimension * 0.09f, center = Offset(size.width * 0.78f, size.height * 0.17f))
        drawOval(Color(0xFF729C65), topLeft = Offset(-size.width * 0.3f, size.height * 0.59f), size = Size(size.width * 1.6f, size.height * 0.7f))
        drawOval(Color(0xFFB9CC89), topLeft = Offset(size.width * 0.20f, size.height * 0.77f), size = Size(size.width * 0.6f, size.height * 0.5f))
        val trees = listOf(0.05f to 0.25f, 0.28f to 0.35f, 0.93f to 0.26f, 0.77f to 0.40f, -0.04f to 0.62f, 1.03f to 0.65f)
        trees.forEachIndexed { index, (x, y) ->
            val width = size.width * 0.30f
            val height = size.height * 0.32f
            val centerX = size.width * x
            val top = size.height * y
            drawRect(Color(0xFF78553A), Offset(centerX - width * 0.07f, top + height * 0.5f), Size(width * 0.14f, height * 0.65f))
            val crown = Path().apply {
                moveTo(centerX, top)
                lineTo(centerX - width * 0.5f, top + height)
                lineTo(centerX + width * 0.5f, top + height)
                close()
            }
            drawPath(crown, if (index % 2 == 0) Color(0xFF3F7752) else Color(0xFF54895A))
        }
    }
}
