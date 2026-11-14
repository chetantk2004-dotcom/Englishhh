package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.PathUnit
import com.example.ui.components.PathTopHudBar
import com.example.ui.components.SectionBannerHeader
import com.example.ui.components.UnitHeaderBanner
import com.example.ui.components.WindingPathNodeItem
import com.example.ui.viewmodel.MainUiState

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onTapPathNode: (LessonNode) -> Unit,
    onOpenGuidebook: (PathUnit) -> Unit,
    onContinueCurrentNode: () -> Unit,
    onOpenDailyBooster: () -> Unit,
    onOpenSectionsMap: () -> Unit,
    onOpenProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val units = uiState.resolvedPathUnits
    val allNodes = units.flatMap { it.nodes }
    val completedCount = allNodes.count {
        it.status == LessonNodeStatus.COMPLETED || it.status == LessonNodeStatus.PERFECT
    }
    val listState = rememberLazyListState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pinned Gamified Learning Path HUD (Streak, XP, Daily Goal + Word, Section Map)
        PathTopHudBar(
            streakDays = uiState.currentStreakDays,
            totalXp = uiState.totalXp,
            todayMinutesDone = uiState.todayMinutesDone,
            dailyGoalMinutes = uiState.dailyGoalMinutes,
            onOpenDailyBooster = onOpenDailyBooster,
            onOpenSectionsMap = onOpenSectionsMap,
            modifier = Modifier.widthIn(max = 680.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .testTag("learning_path_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active Section Banner + "Next Lesson" CTA
                item(key = "section_banner") {
                    SectionBannerHeader(
                        section = uiState.currentSection,
                        completedCount = completedCount,
                        totalCount = allNodes.size,
                        onContinueCurrent = onContinueCurrentNode,
                        onOpenSectionsMap = onOpenSectionsMap
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Continuous Vertical Winding Learning Path across Units
                units.forEach { unit ->
                    item(key = "unit_header_${unit.id}") {
                        UnitHeaderBanner(
                            unit = unit,
                            onOpenGuidebook = { onOpenGuidebook(unit) },
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )
                    }

                    itemsIndexed(
                        items = unit.nodes,
                        key = { _, node -> node.id }
                    ) { index, node ->
                        WindingPathNodeItem(
                            node = node,
                            indexInUnit = index,
                            totalInUnit = unit.nodes.size,
                            unitTheme = unit.theme,
                            showCompanionOnSide = index == 3 && unit.nodes.size >= 5,
                            onTapNode = { onTapPathNode(node) }
                        )
                    }
                }

                // Upcoming Course Milestone Footer
                item(key = "upcoming_units_footer") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LockClock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "आगामी इकाइयाँ (Units 4–25)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Unit 4 (Everyday Objects), Unit 5 (Is/Am/Are) और Sections 2–4 अगले चरणों में जुड़ेंगे।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = onOpenSectionsMap,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("footer_sections_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("सभी 4 खंड देखें")
                                }
                                TextButton(
                                    onClick = onOpenProgress,
                                    modifier = Modifier.testTag("home_view_progress_button")
                                ) {
                                    Text("प्रगति चार्ट")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "विस्तृत प्रगति देखें",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}
