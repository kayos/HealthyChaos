package com.kayos.healthykayos

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppNavigationBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavigationBar() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavigationBar(navController)
        }
    }

    @Test
    fun containsExpectedDestinations() {
        composeTestRule
            .onNodeWithTag("test-home-btn")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("test-recordings-btn")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("test-live-btn")
            .assertIsDisplayed()
    }
}