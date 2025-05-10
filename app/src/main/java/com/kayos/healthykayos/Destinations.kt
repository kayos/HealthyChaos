package com.kayos.healthykayos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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

object RecordingsScreen : Destination {
    override val icon = Icons.AutoMirrored.Filled.List
    override val route = "recordings"
}

object LiveHeartRateScreen : Destination {
    override val icon = Icons.Filled.Favorite
    override val route = "heartrate"
}