package com.example.touriststreasurehunt

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.touriststreasurehunt.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class CoreUserFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun objectiveSelectionScreen_displaysCoreElements() {
        composeTestRule.onNodeWithText("Choose objectives").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scenic").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Adventure").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Hunt").assertIsDisplayed()
    }

    @Test
    fun startHunt_opensNextScreen() {
        composeTestRule.onNodeWithText("Scenic").performClick()
        composeTestRule.onNodeWithText("Start Hunt").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Choose Objectives").assertDoesNotExist()
    }
}