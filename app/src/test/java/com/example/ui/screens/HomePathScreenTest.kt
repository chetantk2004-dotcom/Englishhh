package com.example.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.model.LessonNodeStatus
import com.example.model.NodeProgressRecord
import com.example.model.PathAttemptOutcome
import com.example.model.PathUnitTheme
import com.example.ui.components.LessonNodePopupDialog
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
class HomePathScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `renders continuous learning path with current and locked nodes`() {
        val state = MainUiState()
        var tappedNodeId: String? = null
        var openedGuidebookUnitId: String? = null

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                HomeScreen(
                    uiState = state,
                    onTapPathNode = { tappedNodeId = it.id },
                    onOpenGuidebook = { openedGuidebookUnitId = it.id },
                    onContinueCurrentNode = {},
                    onOpenDailyBooster = {},
                    onOpenSectionsMap = {},
                    onOpenProgress = {}
                )
            }
        }

        composeRule.onNodeWithTag("streak_stat_pill").assertIsDisplayed()
        composeRule.onNodeWithTag("xp_stat_pill").assertIsDisplayed()
        composeRule.onNodeWithTag("daily_booster_pill").assertIsDisplayed()

        composeRule.onNodeWithTag("learning_path_list")
            .performScrollToNode(hasTestTag("unit_banner_s1_u1"))
        composeRule.onNodeWithTag("unit_guidebook_s1_u1").performClick()
        assertEquals("s1_u1", openedGuidebookUnitId)

        composeRule.onNodeWithTag("learning_path_list")
            .performScrollToNode(hasTestTag("path_node_s1_u1_n1"))
        composeRule.onNodeWithTag("current_lesson_callout").assertIsDisplayed()
        composeRule.onNodeWithTag("path_node_s1_u1_n1").performClick()
        assertEquals("s1_u1_n1", tappedNodeId)

        composeRule.onNodeWithTag("learning_path_list")
            .performScrollToNode(hasTestTag("path_node_s1_u1_n2"))
        composeRule.onNodeWithTag("path_node_s1_u1_n2").performClick()
        assertEquals("s1_u1_n2", tappedNodeId)
    }

    @Test
    fun `node info popup enables start button for available node`() {
        val state = MainUiState()
        val availableNode = state.resolvedPathUnits.first().nodes[0]
        var started = false

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                LessonNodePopupDialog(
                    node = availableNode,
                    unitTheme = PathUnitTheme.EMERALD,
                    onStart = { started = true },
                    onDismiss = {}
                )
            }
        }

        composeRule.onNodeWithTag("node_info_popup").assertIsDisplayed()
        composeRule.onNodeWithTag("start_node_button")
            .assertIsEnabled()
            .performClick()
        assertEquals(true, started)
    }

    @Test
    fun `node info popup disables start button for locked node`() {
        val state = MainUiState()
        val lockedNode = state.resolvedPathUnits.first().nodes[1]

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                LessonNodePopupDialog(
                    node = lockedNode,
                    unitTheme = PathUnitTheme.EMERALD,
                    onStart = {},
                    onDismiss = {}
                )
            }
        }

        composeRule.onNodeWithTag("node_info_popup").assertIsDisplayed()
        composeRule.onNodeWithTag("start_node_button").assertIsNotEnabled()
    }

    @Test
    fun `node info popup displays resume CTA and best score for IN_PROGRESS node`() {
        val state = MainUiState(
            pathProgressRecords = mapOf(
                "s1_u1_n1" to NodeProgressRecord(
                    id = "s1_u1_n1",
                    nodeId = "s1_u1_n1",
                    unitId = "s1_u1",
                    status = LessonNodeStatus.IN_PROGRESS.name,
                    bestScorePercent = 50,
                    attemptsCount = 1
                )
            )
        )
        val inProgressNode = state.resolvedPathUnits.first().nodes[0]
        assertEquals(LessonNodeStatus.IN_PROGRESS, inProgressNode.status)

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                LessonNodePopupDialog(
                    node = inProgressNode,
                    unitTheme = PathUnitTheme.EMERALD,
                    onStart = {},
                    onDismiss = {}
                )
            }
        }

        composeRule.onNodeWithText("स्थिति: जारी है", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("सर्वश्रेष्ठ स्कोर: 50%", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("जारी रखें", substring = true).assertIsDisplayed()
    }

    @Test
    fun `path lesson screen saves IN_PROGRESS on early close and displays pass outcome`() {
        val state = MainUiState()
        val unit = state.resolvedPathUnits.first()
        val node = unit.nodes.first()
        var exitedNodeId: String? = null
        var backCalled = false

        composeRule.setContent {
            EnglishSeekhenTheme(darkTheme = false) {
                PathLessonScreen(
                    node = node,
                    unit = unit,
                    allUnits = state.resolvedPathUnits,
                    showPronunciation = true,
                    latestOutcome = PathAttemptOutcome(
                        nodeId = node.id,
                        passed = true,
                        scorePercent = 100,
                        passingScorePercent = 70,
                        starsEarned = 3,
                        resultingStatus = LessonNodeStatus.PERFECT,
                        xpGranted = 20,
                        isReplay = false,
                        unlockedNextUnit = false,
                        newlyUnlockedNodeIds = listOf("s1_u1_n2"),
                        nextPlayableNodeId = "s1_u1_n2"
                    ),
                    onExitMidLesson = { exitedNodeId = it },
                    onSubmitAttempt = { _, _ -> },
                    onNavigateToNode = {},
                    onBackToPath = { backCalled = true }
                )
            }
        }

        composeRule.onNodeWithTag("lesson_screen_root").assertIsDisplayed()
        composeRule.onNodeWithTag("lesson_continue_to_check_button").assertIsDisplayed()
        composeRule.onNodeWithTag("lesson_close_button").performClick()

        assertEquals(node.id, exitedNodeId)
        assertEquals(true, backCalled)
    }
}
