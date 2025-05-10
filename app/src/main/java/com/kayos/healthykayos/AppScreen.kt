package com.kayos.healthykayos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            DeviceBar(
                onAddDeviceClick = {
                    navController.navigate(ConnectionScreen.route)
                }
            )},
        content = { innerPadding ->
            NavHost(
            navController = navController,
            startDestination = HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = HomeScreen.route) {
                HomeScreen()
            }
            composable(route = ConnectionScreen.route) {
                ConnectionScreen(
                    { },{  }
                )
            }
        } }
    )
}