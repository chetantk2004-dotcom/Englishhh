package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GentleErrorRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeScreen(
    uiState: MainUiState,
    onSelectChallengeOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val challengeOptions = listOf(
        "कठोर / तेज़",
        "विनम्र / सभ्य",
        "जल्दीबाज़"
    )
    val correctIndex = 1

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "अभ्यास और शब्दावली (Practice)",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "शब्द ज्ञान, आसान हिंदी व्याकरण और दैनिक चुनौती",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Interactive Daily Word Challenge
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, SaffronSecondary.copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "दैनिक शब्द चुनौती • +15 XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.Translate,
                                contentDescription = null,
                                tint = SaffronSecondary
                            )
                        }

                        Text(
                            text = "‘Polite’ (पो-लाइट) शब्द का सही हिंदी अर्थ चुनें:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        challengeOptions.forEachIndexed { index, option ->
                            val selected = uiState.practiceChallengeSelectedIndex == index
                            val isCorrect = index == correctIndex
                            val borderColor = when {
                                uiState.practiceChallengeSelectedIndex == null -> MaterialTheme.colorScheme.outline
                                selected && isCorrect -> SuccessGreen
                                selected && !isCorrect -> GentleErrorRed
                                isCorrect -> SuccessGreen.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.outline
                            }
                            OutlinedButton(
                                onClick = { onSelectChallengeOption(index) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("practice_challenge_option_$index"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, borderColor),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = when {
                                        selected && isCorrect -> SuccessGreen.copy(alpha = 0.12f)
                                        selected && !isCorrect -> GentleErrorRed.copy(alpha = 0.10f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (selected) {
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                                            contentDescription = null,
                                            tint = if (isCorrect) SuccessGreen else GentleErrorRed
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = uiState.practiceChallengeSelectedIndex != null) {
                            val correct = uiState.practiceChallengeSelectedIndex == correctIndex
                            Surface(
                                color = if (correct) SuccessGreen.copy(alpha = 0.14f) else GentleErrorRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (correct) {
                                        "बहुत बढ़िया! ‘Polite’ का मतलब विनम्र / सभ्य होता है। (+15 XP)"
                                    } else {
                                        "थोड़ा और सोचें! उदाहरण: Always be polite to elders."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 12 Vocabulary Categories
            item {
                SectionHeader(
                    titleHi = "शब्दावली श्रेणियाँ (12 Categories)",
                    subtitleEn = "Vocabulary organized by everyday topics"
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 2
                ) {
                    uiState.vocabCategories.forEach { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.48f)
                                .testTag("vocab_category_${category.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = category.titleHi,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = category.titleEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                LinearProgressIndicator(
                                    progress = { category.learnedCount.toFloat() / category.wordCount.coerceAtLeast(1) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp),
                                    color = EmeraldPrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round
                                )
                                Text(
                                    text = "${category.learnedCount}/${category.wordCount} शब्द",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Simple Hindi Grammar Section
            item {
                SectionHeader(
                    titleHi = "आसान हिंदी में व्याकरण (Grammar)",
                    subtitleEn = "Simple rules without heavy jargon"
                )
            }

            items(uiState.grammarPreviews, key = { it.id }) { topic ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${topic.titleEn} • ${topic.titleHi}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = topic.summaryHi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "नियम: ${topic.structurePattern}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        Text(
                            text = "उदाहरण: ${topic.positiveExampleEn} (${topic.positiveExampleHi})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = topic.commonMistakeHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Upcoming Phase Modules Preview (Speaking/Listening + Smart Revision)
            item {
                SectionHeader(
                    titleHi = "आगामी अभ्यास मॉड्यूल (Roadmap Preview)",
                    subtitleEn = "Built cleanly in upcoming phases"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    UpcomingFeatureCard(
                        icon = Icons.Filled.RecordVoiceOver,
                        titleHi = "बोलना और सुनना अभ्यास (Speaking & Listening)",
                        descHi = "चरण 6 में मुफ़्त Android Text-to-Speech और आवाज़ अभ्यास जुड़ेगा।",
                        badge = "Phase 6"
                    )
                    UpcomingFeatureCard(
                        icon = Icons.Filled.AutoFixHigh,
                        titleHi = "स्मार्ट दोहराव प्रणाली (Smart Revision)",
                        descHi = "ग़लत उत्तरों और कठिन शब्दों को अपने-आप दोहराने की प्रणाली।",
                        badge = "Phase 4–5"
                    )
                    UpcomingFeatureCard(
                        icon = Icons.Filled.GraphicEq,
                        titleHi = "9 प्रकार के इंटरैक्टिव क्विज़ (Quiz Engine)",
                        descHi = "वाक्य जोड़ना, खाली स्थान भरना, मिलान और अनुवाद अभ्यास।",
                        badge = "Phase 3"
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun UpcomingFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleHi: String,
    descHi: String,
    badge: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = titleHi,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descHi,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
