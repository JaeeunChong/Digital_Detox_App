package com.team.digitaldetox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team.digitaldetox.feature.activity.ActivityScreen
import com.team.digitaldetox.feature.home.HomeScreen
import com.team.digitaldetox.feature.interaction.InteractionScreen
import com.team.digitaldetox.feature.onboarding.AllowedAppsScreen
import com.team.digitaldetox.feature.onboarding.CharacterSelectScreen
import com.team.digitaldetox.feature.onboarding.SleepSetupScreen
import com.team.digitaldetox.feature.usage.UsageScreen
import com.team.digitaldetox.feature.world.WorldScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // 초기 데모에서는 앱을 재실행할 때 온보딩이 다시 나옵니다.
    // 추후 DataStore에 온보딩 완료 여부/닉네임/캐릭터를 저장하세요.
    var nickname by rememberSaveable { mutableStateOf("") }
    var character by rememberSaveable { mutableStateOf("boy") }

    NavHost(navController = navController, startDestination = "onboarding/sleep") {
        composable("onboarding/sleep") {
            SleepSetupScreen(onNext = { navController.navigate("onboarding/apps") })
        }
        composable("onboarding/apps") {
            AllowedAppsScreen(onNext = { navController.navigate("onboarding/character") })
        }
        composable("onboarding/character") {
            CharacterSelectScreen(onFinish = { chosenCharacter, chosenNickname ->
                character = chosenCharacter
                nickname = chosenNickname
                navController.navigate("home") {
                    popUpTo("onboarding/sleep") { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable("home") {
            HomeScreen(
                onUsageClick = { navController.navigate("usage") },
                onActivityClick = { navController.navigate("activity") },
                onWorldClick = { navController.navigate("world") },
                onInteractionClick = { navController.navigate("interaction") }
            )
        }
        composable("usage") { UsageScreen(onBack = { navController.popBackStack() }) }
        composable("activity") { ActivityScreen(onBack = { navController.popBackStack() }) }
        composable("world") { WorldScreen(onBack = { navController.popBackStack() }) }
        composable("interaction") { InteractionScreen(onBack = { navController.popBackStack() }) }
    }
}
