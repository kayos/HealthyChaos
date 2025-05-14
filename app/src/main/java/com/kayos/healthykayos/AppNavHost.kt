package com.kayos.healthykayos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
        modifier = Modifier.Companion.padding(innerPadding)
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen()
        }
        composable(route = ConnectionScreen.route) {
            ConnectionScreen(
                { navController.navigate(RecordingsScreen.route) },
                { navController.navigate(LiveHeartRateScreen.route) }
            )
        }
        composable(route = RecordingsScreen.route) {
            RecordingsScreen()
        }
        composable(route = LiveHeartRateScreen.route) {
            LiveHeartRateScreen()
        }
    }
}