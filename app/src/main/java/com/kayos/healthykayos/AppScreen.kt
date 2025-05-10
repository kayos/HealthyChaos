package com.kayos.healthykayos

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {DeviceBar()},
        content = { _ -> HomeScreen() }
    )
}