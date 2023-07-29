package com.vproject.brushai

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import com.vproject.brushai.main.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.properties.ReadOnlyProperty
import com.vproject.brushai.feature.generate.R as generateR
import com.vproject.brushai.feature.explore.R as exploreR

@HiltAndroidTest
class NavigationTest {
    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
        ReadOnlyProperty<Any?, String> { _, _ -> activity.getString(resId) }

    // The strings used for matching in these tests
    private val appName by composeTestRule.stringResource(R.string.app_name)
    private val generate by composeTestRule.stringResource(generateR.string.generate)
    private val explore by composeTestRule.stringResource(exploreR.string.explore)

    @Test
    fun whenAppStarted_thenFirstScreenIsGenerate() {
        composeTestRule.apply {
            onNodeWithText(generate).assertIsSelected()
        }
    }

    @Test
    fun whenAppStarted_thenShowSettingsIcon() {
        composeTestRule.apply {
            onNodeWithContentDescription("settings").assertExists()

            onNodeWithText(explore).performClick()
            onNodeWithContentDescription("settings").assertExists()
        }
    }

    @Test(expected = NoActivityResumedException::class)
    fun givenAppStarted_whenPressingBackButton_thenQuittingApp() {
        composeTestRule.apply {
            onNodeWithText(explore).performClick()
            onNodeWithText(generate).performClick()
            Espresso.pressBack()
        }
    }

    @Test
    fun whenSelectingExploreTab_thenShowExploreNavigationTab() {
        composeTestRule.apply {
            // Verify that the top bar contains the app name on the first screen.
            onNodeWithText(appName).assertExists()

            // Go to the explore tab, verify that the top bar contains "Explore". This means
            // we'll have 2 elements with the text "Explore" on screen. One in the top bar, and
            // one in the bottom navigation.
            onNodeWithText(explore).performClick()
            onAllNodesWithText(explore).assertCountEquals(2)
        }
    }
}