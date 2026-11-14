package com.example.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.model.AvatarPresets
import com.example.model.UserProfile
import com.example.ui.theme.EnglishSeekhenTheme
import com.example.ui.viewmodel.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `displays firestore profile data and invokes edit callbacks`() {
        val firestoreProfile = UserProfile(
            id = "uid_1",
            userId = "uid_1",
            profileId = "EL10245",
            username = "rahullearns",
            usernameLower = "rahullearns",
            displayName = "Rahul Sharma",
            avatarUrl = AvatarPresets.OWL,
            bio = "Learning fluent English every day!",
            totalXp = 2450L,
            streak = 12L,
            lessonsCompleted = 24L,
            followersCount = 125L,
            followingCount = 80L,
            interests = listOf("Daily Conversation", "Job Interviews")
        )

        val state = MainUiState(
            currentUserId = "uid_1",
            myProfile = firestoreProfile,
            isProfileLoading = false,
            totalXp = 2450,
            currentStreakDays = 12,
            lessonsCompleted = 24
        )

        var editAllClicks = 0
        var editAvatarClicks = 0
        var editNameClicks = 0
        var editUsernameClicks = 0
        var editBioClicks = 0

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                ProfileScreen(
                    uiState = state,
                    onSelectDailyGoal = {},
                    onToggleDarkTheme = {},
                    onTogglePronunciationGuide = {},
                    onOpenEditProfile = { editAllClicks++ },
                    onEditAvatar = { editAvatarClicks++ },
                    onEditDisplayName = { editNameClicks++ },
                    onEditUsername = { editUsernameClicks++ },
                    onEditBio = { editBioClicks++ },
                    onOpenPrivacySafety = {},
                    onOpenFollowers = {},
                    onOpenFollowing = {},
                    onOpenProgress = {},
                    onOpenFindFriends = {},
                    onSignOut = {}
                )
            }
        }

        composeRule.onNodeWithTag("profile_display_name_text")
            .assertIsDisplayed()
            .assertTextEquals("Rahul Sharma")

        composeRule.onNodeWithTag("profile_username_text")
            .assertIsDisplayed()
            .assertTextEquals("@rahullearns")

        composeRule.onNodeWithTag("profile_bio_text")
            .assertIsDisplayed()
            .assertTextEquals("Learning fluent English every day!")

        composeRule.onNodeWithTag("edit_avatar_button").performClick()
        composeRule.onNodeWithTag("profile_lazy_column")
            .performScrollToNode(hasTestTag("edit_profile_button"))
        composeRule.onNodeWithTag("edit_profile_button").performClick()

        composeRule.onNodeWithTag("profile_lazy_column")
            .performScrollToNode(hasTestTag("edit_display_name_button"))
        composeRule.onNodeWithTag("edit_display_name_button").performClick()

        composeRule.onNodeWithTag("profile_lazy_column")
            .performScrollToNode(hasTestTag("edit_username_button"))
        composeRule.onNodeWithTag("edit_username_button").performClick()

        composeRule.onNodeWithTag("profile_lazy_column")
            .performScrollToNode(hasTestTag("edit_bio_button"))
        composeRule.onNodeWithTag("edit_bio_button").performClick()

        assertEquals(1, editAvatarClicks)
        assertEquals(1, editAllClicks)
        assertEquals(1, editNameClicks)
        assertEquals(1, editUsernameClicks)
        assertEquals(1, editBioClicks)
    }

    @Test
    fun `shows loading indicator while profile is syncing from firestore`() {
        val loadingState = MainUiState(
            currentUserId = "uid_1",
            myProfile = null,
            isProfileLoading = true
        )

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                ProfileScreen(
                    uiState = loadingState,
                    onSelectDailyGoal = {},
                    onToggleDarkTheme = {},
                    onTogglePronunciationGuide = {},
                    onOpenEditProfile = {},
                    onOpenPrivacySafety = {},
                    onOpenFollowers = {},
                    onOpenFollowing = {},
                    onOpenProgress = {},
                    onOpenFindFriends = {},
                    onSignOut = {}
                )
            }
        }

        composeRule.onNodeWithTag("profile_loading_indicator").assertIsDisplayed()
    }
}
