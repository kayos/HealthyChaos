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
        startDestination = Home,
        modifier = Modifier.Companion.padding(innerPadding)
    ) {
        composable<Home>{
            HomeScreen()
        }
        composable<Connection> {
            ConnectionScreen(
                { navController.navigate(route = Recordings) },
                { navController.navigate(route = Live) }
            )
        }
        composable<Recordings> {
            RecordingsScreen()
        }
        composable<Live> {
            LiveHeartRateScreen()
        }
    }
}