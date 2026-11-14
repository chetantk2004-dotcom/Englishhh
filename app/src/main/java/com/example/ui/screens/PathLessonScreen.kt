package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.LessonNodeType
import com.example.model.PathAttemptOutcome
import com.example.model.PathUnit
import com.example.ui.components.nodeTypeIcon
import com.example.ui.components.unitPalette
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SuccessGreen

private enum class LessonStage {
    CONCEPT,
    CHECK,
    OUTCOME
}

private data class LessonCheckItem(
    val promptHi: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
fun PathLessonScreen(
    node: LessonNode,
    unit: PathUnit,
    allUnits: List<PathUnit>,
    showPronunciation: Boolean,
    latestOutcome: PathAttemptOutcome?,
    onExitMidLesson: (String) -> Unit,
    onSubmitAttempt: (String, Int) -> Unit,
    onNavigateToNode: (String) -> Unit,
    onBackToPath: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = unitPalette(unit.theme)
    val checkItems = remember(node.id, unit.id) { buildLessonCheckItems(node, unit) }

    var stage by remember(node.id) { mutableStateOf(LessonStage.CONCEPT) }
    var currentQuestionIndex by remember(node.id) { mutableIntStateOf(0) }
    var selectedOptionIndex by remember(node.id) { mutableStateOf<Int?>(null) }
    var correctAnswersCount by remember(node.id) { mutableIntStateOf(0) }

    val handleExit: () -> Unit = {
        if (stage != LessonStage.OUTCOME) {
            onExitMidLesson(node.id)
        }
        onBackToPath()
    }

    BackHandler(onBack = handleExit)

    val progressFraction = when (stage) {
        LessonStage.CONCEPT -> 0.2f
        LessonStage.CHECK -> 0.25f + (0.65f * ((currentQuestionIndex + 1).toFloat() / checkItems.size.coerceAtLeast(1)))
        LessonStage.OUTCOME -> 1.0f
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("lesson_screen_root"),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Progress & Exit Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = handleExit,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("lesson_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .testTag("lesson_progress_bar"),
                        color = palette.main,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = palette.softBg
                    ) {
                        Text(
                            text = "पास ${node.passingScorePercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = palette.main,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Header Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = palette.softBg,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = nodeTypeIcon(node.type),
                                contentDescription = null,
                                tint = palette.main,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${unit.titleHi} • ${node.type.labelHi}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = palette.main
                            )
                        }
                    }

                    if (node.xpAwarded) {
                        Text(
                            text = "दोहराव मोड (0 XP)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "+${node.xpReward} XP",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = SaffronDark
                        )
                    }
                }

                // Main Stage Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (stage) {
                        LessonStage.CONCEPT -> {
                            ConceptTeachingStep(
                                node = node,
                                paletteMain = palette.main,
                                showPronunciation = showPronunciation
                            )
                        }

                        LessonStage.CHECK -> {
                            val question = checkItems[currentQuestionIndex]
                            QuestionCheckStep(
                                question = question,
                                questionIndex = currentQuestionIndex,
                                totalQuestions = checkItems.size,
                                selectedOptionIndex = selectedOptionIndex,
                                accentColor = palette.main,
                                onSelectOption = { selected ->
                                    if (selectedOptionIndex == null) {
                                        selectedOptionIndex = selected
                                        if (selected == question.correctIndex) {
                                            correctAnswersCount += 1
                                        }
                                    }
                                }
                            )
                        }

                        LessonStage.OUTCOME -> {
                            val outcome = latestOutcome
                            if (outcome != null) {
                                val allNodes = remember(allUnits) { allUnits.flatMap { it.nodes } }
                                val nextNode = outcome.nextPlayableNodeId?.let { nextId ->
                                    allNodes.firstOrNull { it.id == nextId }
                                }
                                LessonOutcomeStep(
                                    node = node,
                                    outcome = outcome,
                                    nextNode = nextNode
                                )
                            }
                        }
                    }
                }

                // Bottom Action Bar
                when (stage) {
                    LessonStage.CONCEPT -> {
                        Button(
                            onClick = {
                                currentQuestionIndex = 0
                                selectedOptionIndex = null
                                correctAnswersCount = 0
                                stage = LessonStage.CHECK
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.main,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("lesson_continue_to_check_button")
                        ) {
                            Text(
                                text = if (node.type == LessonNodeType.UNIT_TEST) {
                                    "चेकपॉइंट परीक्षा शुरू करें (${checkItems.size} सवाल)"
                                } else {
                                    "अभ्यास जाँच शुरू करें (${checkItems.size} सवाल)"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    LessonStage.CHECK -> {
                        val hasAnswered = selectedOptionIndex != null
                        val isLastQuestion = currentQuestionIndex >= checkItems.lastIndex
                        Button(
                            onClick = {
                                if (!isLastQuestion) {
                                    currentQuestionIndex += 1
                                    selectedOptionIndex = null
                                } else {
                                    val scorePercent = ((correctAnswersCount * 100f) / checkItems.size.coerceAtLeast(1)).toInt()
                                    onSubmitAttempt(node.id, scorePercent)
                                    stage = LessonStage.OUTCOME
                                }
                            },
                            enabled = hasAnswered,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.main,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("lesson_next_question_button")
                        ) {
                            Text(
                                text = if (isLastQuestion) "परिणाम देखें" else "अगला सवाल",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LessonStage.OUTCOME -> {
                        val outcome = latestOutcome
                        val allNodes = remember(allUnits) { allUnits.flatMap { it.nodes } }
                        val nextNode = outcome?.nextPlayableNodeId?.let { nextId ->
                            allNodes.firstOrNull { it.id == nextId }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (outcome != null && !outcome.passed) {
                                Button(
                                    onClick = {
                                        currentQuestionIndex = 0
                                        selectedOptionIndex = null
                                        correctAnswersCount = 0
                                        stage = LessonStage.CHECK
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SaffronDark,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("lesson_retry_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "फिर से प्रयास करें (कम से कम ${node.passingScorePercent}% चाहिए)",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (outcome != null && nextNode != null && nextNode.status != LessonNodeStatus.LOCKED) {
                                Button(
                                    onClick = { onNavigateToNode(nextNode.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = palette.main,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("lesson_next_node_button")
                                ) {
                                    Text(
                                        text = "अगला पाठ: ${nextNode.titleHi}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onBackToPath,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("lesson_back_to_path_button")
                            ) {
                                Text(
                                    text = "लर्निंग पाथ पर लौटें",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConceptTeachingStep(
    node: LessonNode,
    paletteMain: Color,
    showPronunciation: Boolean
) {
    val preview = node.samplePreview

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = node.titleHi,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = node.learningObjectiveHi,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, paletteMain.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "मुख्य अंग्रेज़ी वाक्य",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = paletteMain
                )
                Text(
                    text = preview.englishPhrase,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = preview.hindiMeaning,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (showPronunciation) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "उच्चारण: ${preview.pronunciationHi}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "सरल नियम और उदाहरण",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = preview.explanationHi,
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = preview.realLifeExampleEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = preview.realLifeExampleHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCheckStep(
    question: LessonCheckItem,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOptionIndex: Int?,
    accentColor: Color,
    onSelectOption: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "सवाल ${questionIndex + 1} / $totalQuestions",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = question.promptHi,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        question.options.forEachIndexed { idx, optionText ->
            val isSelected = selectedOptionIndex == idx
            val isCorrect = idx == question.correctIndex
            val showReveal = selectedOptionIndex != null

            val containerColor = when {
                showReveal && isCorrect -> SuccessGreen.copy(alpha = 0.14f)
                showReveal && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val borderColor = when {
                showReveal && isCorrect -> SuccessGreen
                showReveal && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                isSelected -> accentColor
                else -> MaterialTheme.colorScheme.outline
            }

            Surface(
                onClick = { onSelectOption(idx) },
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                border = BorderStroke(1.5.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("lesson_check_option_$idx")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected || (showReveal && isCorrect)) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    if (showReveal && isCorrect) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "सही उत्तर",
                            tint = SuccessGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        if (selectedOptionIndex != null) {
            val answeredRight = selectedOptionIndex == question.correctIndex
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (answeredRight) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = if (answeredRight) {
                        "शाबाश! बिल्कुल सही उत्तर।"
                    } else {
                        "सही उत्तर है: ${question.options[question.correctIndex]}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (answeredRight) EmeraldDeep else MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun LessonOutcomeStep(
    node: LessonNode,
    outcome: PathAttemptOutcome,
    nextNode: LessonNode?
) {
    val passed = outcome.passed

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (passed) SuccessGreen.copy(alpha = 0.10f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (passed) SuccessGreen else SaffronDark
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lesson_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = if (passed) SaffronSecondary else SaffronDark,
                modifier = Modifier.size(56.dp)
            )

            Text(
                text = when {
                    !passed -> "थोड़ा और अभ्यास करें!"
                    outcome.resultingStatus == LessonNodeStatus.PERFECT -> "शानदार! परफ़ेक्ट स्कोर 🎉"
                    else -> "बहुत अच्छे! पाठ पूरा हुआ ✓"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Star Rating Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { idx ->
                    val earned = idx < outcome.starsEarned
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (earned) SaffronSecondary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${outcome.scorePercent}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (passed) EmeraldDeep else SaffronDark
                        )
                        Text(
                            text = "स्कोर (पास: ${outcome.passingScorePercent}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (outcome.xpGranted > 0) "+${outcome.xpGranted} XP" else "0 XP",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (outcome.xpGranted > 0) SaffronDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when {
                                outcome.isReplay -> "दोहराव (XP पहले मिला)"
                                !passed -> "पास होने पर मिलेगा"
                                else -> "नया इनाम"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (outcome.unlockedNextUnit) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "बधाई! यूनिट परीक्षा पास करने पर अगली यूनिट अनलॉक हो गई है।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            } else if (passed && outcome.newlyUnlockedNodeIds.isNotEmpty() && nextNode != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                ) {
                    Text(
                        text = "अनलॉक हुआ: ${nextNode.titleHi} (${nextNode.titleEn})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            } else if (!passed) {
                Text(
                    text = "${node.titleHi} को पूरा करने के लिए कम से कम ${outcome.passingScorePercent}% अंक ज़रूरी हैं। आपकी प्रगति 'जारी है' में सहेज दी गई है।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun buildLessonCheckItems(node: LessonNode, unit: PathUnit): List<LessonCheckItem> {
    val preview = node.samplePreview
    val otherMeanings = (unit.nodes
        .filter { it.id != node.id }
        .map { it.samplePreview.hindiMeaning } +
        unit.guidebookPatterns.map { it.meaningHi })
        .distinct()
        .filter { it != preview.hindiMeaning && it != preview.realLifeExampleHi }

    val fallbackDistractors = listOf(
        "कृपया कल दोबारा आइए।",
        "मेरा बैग मेज़ पर है।",
        "मुझे थोड़ा समय चाहिए।"
    )

    val q1 = LessonCheckItem(
        promptHi = preview.quizQuestionHi,
        options = preview.quizOptions,
        correctIndex = preview.correctOptionIndex.coerceIn(0, preview.quizOptions.lastIndex.coerceAtLeast(0))
    )

    val q2Distractors = (otherMeanings + fallbackDistractors)
        .filter { it != preview.hindiMeaning }
        .distinct()
        .take(2)
    val q2Options = listOf(q2Distractors.getOrElse(0) { fallbackDistractors[0] }, preview.hindiMeaning, q2Distractors.getOrElse(1) { fallbackDistractors[1] })
    val q2 = LessonCheckItem(
        promptHi = "इस वाक्य का सही हिंदी अर्थ चुनें:\n\"${preview.englishPhrase}\"",
        options = q2Options,
        correctIndex = 1
    )

    val q3Distractors = (otherMeanings.reversed() + fallbackDistractors)
        .filter { it != preview.realLifeExampleHi && it != preview.hindiMeaning }
        .distinct()
        .take(2)
    val q3Options = listOf(preview.realLifeExampleHi, q3Distractors.getOrElse(0) { fallbackDistractors[0] }, q3Distractors.getOrElse(1) { fallbackDistractors[2] })
    val q3 = LessonCheckItem(
        promptHi = "दैनिक उदाहरण का सही अर्थ पहचानें:\n\"${preview.realLifeExampleEn}\"",
        options = q3Options,
        correctIndex = 0
    )

    val pattern = unit.guidebookPatterns.getOrNull(node.orderInUnit % unit.guidebookPatterns.size.coerceAtLeast(1))
    val q4 = if (pattern != null) {
        val q4Distractors = (otherMeanings + fallbackDistractors)
            .filter { it != pattern.meaningHi }
            .distinct()
            .take(2)
        LessonCheckItem(
            promptHi = "इस यूनिट पैटर्न का सही मतलब चुनें:\n\"${pattern.patternEn}\"",
            options = listOf(q4Distractors.getOrElse(0) { fallbackDistractors[1] }, q4Distractors.getOrElse(1) { fallbackDistractors[2] }, pattern.meaningHi),
            correctIndex = 2
        )
    } else {
        LessonCheckItem(
            promptHi = "\"${preview.englishPhrase}\" का हिंदी उच्चारण किस प्रकार बोला जाता है?",
            options = listOf(preview.pronunciationHi, "गुड बाय, सी यू!", "थैंक यू वेरी मच!"),
            correctIndex = 0
        )
    }

    return listOf(q1, q2, q3, q4)
}
