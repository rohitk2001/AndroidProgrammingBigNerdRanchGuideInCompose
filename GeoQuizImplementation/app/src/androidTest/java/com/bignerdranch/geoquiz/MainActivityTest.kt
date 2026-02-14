package com.bignerdranch.geoquiz

import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsFirstQuestionOnLaunch() {
        // Get the expected text for the first question.
        // We use the rule's activity context to resolve the string resource,
        // ensuring the test isn't hardcoded to "Australia is a country and a continent."
        val expectedQuestionText = composeTestRule.activity.getString(R.string.question_1)

        // Find the composable node that displays the question text
        // and assert that it is actually on the screen.
        composeTestRule
            .onNodeWithText(expectedQuestionText)
            .assertIsDisplayed()
    }

    @Test
    fun showsSecondQuestionAfterNextPress() {
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        val expectedQuestionText = composeTestRule.activity.getString(R.string.question_2)
        composeTestRule.onAllNodesWithText(expectedQuestionText).assertAny(hasText(expectedQuestionText))
    }

    @Test
    fun handlesActivityRecreation() {
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // To recreate the activity, we must run the command on the main UI thread.
        // Use runOnUiThread to do this safely.
        composeTestRule.runOnUiThread {
            composeTestRule.activity.recreate()
        }

        val expectedQuestionText = composeTestRule.activity.getString(R.string.question_2)

        composeTestRule
            .onNodeWithText(expectedQuestionText)
            .assertIsDisplayed()
    }
}