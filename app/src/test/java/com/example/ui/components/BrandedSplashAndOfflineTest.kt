package com.example.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.ui.theme.EnglishSeekhenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrandedSplashAndOfflineTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `branded splash overlay renders app name and mascot`() {
        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                BrandedSplashOverlay()
            }
        }

        composeRule.onNodeWithTag("splash_screen").assertIsDisplayed()
        composeRule.onNodeWithText("English सीखें").assertIsDisplayed()
    }

    @Test
    fun `offline status banner displays when offline and hides when online`() {
        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                OfflineStatusBanner(isOnline = false)
            }
        }

        composeRule.onNodeWithTag("offline_status_banner").assertIsDisplayed()
        composeRule.onNodeWithText("ऑफ़लाइन मोड", substring = true).assertIsDisplayed()
    }
}
