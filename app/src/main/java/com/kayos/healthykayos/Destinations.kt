package com.kayos.healthykayos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

interface Destination {
    val icon: ImageVector
    val route: String
}

object HomeScreen : Destination {
    override val icon = Icons.Filled.Home
    override val route = "home"
}

object ConnectionScreen : Destination {
    override val icon = Icons.Filled.Add
    override val route = "connection"
}