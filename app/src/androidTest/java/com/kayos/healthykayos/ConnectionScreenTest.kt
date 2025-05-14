package com.kayos.healthykayos

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.polar.Device
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class ConnectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenDevicesAvailable_displayedAsAvailable() {
        val available = listOf(Device("123", "Avail1"),Device("345", "StillHere"))
        composeTestRule.setContent {
            ConnectionScreen(
                onConnectClick = {},
                onSearchClick = {},
                uiState = ConnectionUiState(available, null)
            )
        }

        composeTestRule.onNodeWithTag("test-avail1-available-item").isDisplayed()
        composeTestRule.onNodeWithTag("test-stillhere-available-item").isDisplayed()
    }

    @Test
    fun whenDeviceConnected_displayedAsConnected() {
        composeTestRule.setContent {
            ConnectionScreen(
                onConnectClick = {},
                onSearchClick = {},
                uiState = ConnectionUiState(emptyList(), Device("123","here"))
            )
        }

        composeTestRule.onNodeWithTag("test-here-connected-item").isDisplayed()
    }

    @Test
    fun whenAvailableDeviceClicked_executesConnectionEvent() {
        var doConnect = false
        composeTestRule.setContent {
            ConnectionScreen(
                onConnectClick = {doConnect = true},
                onSearchClick = {},
                uiState = ConnectionUiState(listOf(Device("123","avail")), null)
            )
        }

        composeTestRule.onNodeWithTag("test-avail-available-item").performClick()

        assertTrue(doConnect)
    }

    @Test
    fun whenSearchClicked_executesSearchEvent() {
        var doSearch = false
        composeTestRule.setContent {
            ConnectionScreen(
                onConnectClick = {},
                onSearchClick = {doSearch = true},
                uiState = ConnectionUiState(emptyList(), null)
            )
        }

        composeTestRule.onNodeWithTag("test-search-btn").performClick()

        assertTrue(doSearch)
    }
}