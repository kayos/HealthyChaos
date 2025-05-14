package com.kayos.healthykayos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object Home
@Serializable
object Recordings
@Serializable
object Live
@Serializable
object Connection

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Recordings", Recordings, Icons.AutoMirrored.Filled.List),
    TopLevelRoute("Home", Home, Icons.Filled.Home),
    TopLevelRoute("Live", Live, Icons.Filled.Favorite)
)
