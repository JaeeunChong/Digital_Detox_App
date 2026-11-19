package com.team.digitaldetox.feature.usage

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 사용 정보 접근(Usage Access)은 일반적인 런타임 권한 팝업으로 요청할 수 없습니다.
 * 사용자가 Android 설정 화면에서 직접 허용해야 합니다.
 * AndroidManifest.xml에 PACKAGE_USAGE_STATS 선언도 필요합니다.
 */
object UsagePermissionHelper {
    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE)
                as? AppOpsManager ?: return false

        return try {
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: SecurityException) {
            false
        }
    }

    fun settingsIntent(): Intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
}

/**
 * A 화면에서 호출하는 권한 안내 카드.
 * 설정에서 돌아오면 권한 상태를 다시 확인합니다.
 */
@Composable
fun UsagePermissionSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(UsagePermissionHelper.hasUsageAccess(context))
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        granted = UsagePermissionHelper.hasUsageAccess(context)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFDEBD2)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "스마트폰 사용 정보 접근 권한",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF303D32)
            )

            Text(
                text = if (granted) "접근 허용됨" else "접근 허용 필요",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (granted) Color(0xFF477A55) else Color(0xFF9B5937)
            )

            Text(
                text = if (granted) {
                    "이제 실제 앱별 사용 기록을 조회할 수 있어요. " +
                            "실제 데이터 표시는 다음 단계에서 연결합니다."
                } else {
                    "실제 앱 사용 시간을 확인하려면 Android 설정에서 " +
                            "OFF:ON의 '사용 정보 접근'을 허용해야 해요."
                },
                fontSize = 14.sp,
                color = Color(0xFF303D32)
            )

            if (!granted) {
                Button(
                    onClick = {
                        try {
                            settingsLauncher.launch(UsagePermissionHelper.settingsIntent())
                        } catch (_: ActivityNotFoundException) {
                            settingsLauncher.launch(Intent(Settings.ACTION_SETTINGS))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF477A55)
                    )
                ) {
                    Text("권한 설정 열기")
                }
            }
        }
    }
}

