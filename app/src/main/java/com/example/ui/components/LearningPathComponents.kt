package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.CourseSection
import com.example.model.CourseSectionOutline
import com.example.model.DailyWord
import com.example.model.DayProgress
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.LessonNodeType
import com.example.model.PathUnit
import com.example.model.PathUnitTheme
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.IndigoTertiary
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronDeep
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.StreakFlameOrange
import com.example.ui.theme.SuccessGreen
import kotlin.math.roundToInt
import kotlin.math.sin

data class UnitPalette(
    val main: Color,
    val lip: Color,
    val softBg: Color
)

@Composable
fun unitPalette(theme: PathUnitTheme): UnitPalette = when (theme) {
    PathUnitTheme.EMERALD -> UnitPalette(
        main = EmeraldPrimary,
        lip = EmeraldDeep,
        softBg = MaterialTheme.colorScheme.primaryContainer
    )
    PathUnitTheme.INDIGO -> UnitPalette(
        main = IndigoTertiary,
        lip = IndigoDeep,
        softBg = MaterialTheme.colorScheme.tertiaryContainer
    )
    PathUnitTheme.SAFFRON -> UnitPalette(
        main = SaffronSecondary,
        lip = SaffronDark,
        softBg = MaterialTheme.colorScheme.secondaryContainer
    )
    PathUnitTheme.TEAL -> UnitPalette(
        main = EmeraldDark,
        lip = EmeraldDeep,
        softBg = MaterialTheme.colorScheme.primaryContainer
    )
}

fun nodeTypeIcon(type: LessonNodeType): ImageVector = when (type) {
    LessonNodeType.NEW_LESSON -> Icons.Filled.Star
    LessonNodeType.VOCABULARY -> Icons.Filled.Translate
    LessonNodeType.GRAMMAR -> Icons.Filled.Extension
    LessonNodeType.LISTENING -> Icons.Filled.Headphones
    LessonNodeType.SPEAKING -> Icons.Filled.Mic
    LessonNodeType.REVIEW -> Icons.Filled.Replay
    LessonNodeType.STORY -> Icons.AutoMirrored.Filled.MenuBook
    LessonNodeType.UNIT_TEST -> Icons.Filled.EmojiEvents
    LessonNodeType.CHALLENGE -> Icons.Filled.Bolt
}

fun pathOffsetFraction(indexInUnit: Int, totalInUnit: Int): Float {
    if (indexInUnit == 0 || indexInUnit == totalInUnit - 1) return 0f
    return sin(indexInUnit * 1.15f) * 0.88f
}

@Composable
fun PathTopHudBar(
    streakDays: Int,
    totalXp: Int,
    todayMinutesDone: Int,
    dailyGoalMinutes: Int,
    onOpenDailyBooster: () -> Unit,
    onOpenSectionsMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopStatPill(
                    icon = Icons.Filled.LocalFireDepartment,
                    valueText = "$streakDays",
                    tint = StreakFlameOrange,
                    containerColor = StreakFlameOrange.copy(alpha = 0.12f),
                    contentDescription = stringResource(R.string.cd_streak_icon),
                    modifier = Modifier.testTag("streak_stat_pill")
                )
                TopStatPill(
                    icon = Icons.Filled.Star,
                    valueText = "$totalXp XP",
                    tint = SaffronSecondary,
                    containerColor = SaffronSecondary.copy(alpha = 0.14f),
                    contentDescription = stringResource(R.string.cd_xp_icon),
                    modifier = Modifier.testTag("xp_stat_pill")
                )
                Surface(
                    onClick = onOpenDailyBooster,
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("daily_booster_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "आज का शब्द और लक्ष्य",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${todayMinutesDone}/${dailyGoalMinutes}m",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            IconButton(
                onClick = onOpenSectionsMap,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("sections_map_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "कोर्स रोडमैप देखें",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SectionBannerHeader(
    section: CourseSection,
    completedCount: Int,
    totalCount: Int,
    onContinueCurrent: () -> Unit,
    onOpenSectionsMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (completedCount.toFloat() / totalCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(EmeraldPrimary, EmeraldDark)
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.clickable(onClick = onOpenSectionsMap)
                        ) {
                            Text(
                                text = "SECTION ${section.orderNumber} • CEFR ${section.cefrStage.code} ▾",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = section.titleHi,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = section.subtitleHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_path_companion),
                        contentDescription = "English सीखें पथ साथी",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = SaffronSecondary,
                            trackColor = Color.White.copy(alpha = 0.28f),
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCount / $totalCount पाठ पूरे",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Button(
                        onClick = onContinueCurrent,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronSecondary,
                            contentColor = Color(0xFF1F1600)
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("continue_learning_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "अगला पाठ",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitHeaderBanner(
    unit: PathUnit,
    onOpenGuidebook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = unitPalette(unit.theme)
    val completedCount = unit.nodes.count {
        it.status == LessonNodeStatus.COMPLETED || it.status == LessonNodeStatus.PERFECT
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("unit_banner_${unit.id}"),
        shape = RoundedCornerShape(20.dp),
        color = palette.main,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "UNIT ${unit.orderNumber} • ${unit.titleEn.uppercase()} ($completedCount/${unit.nodes.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Text(
                    text = unit.titleHi,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = unit.descriptionHi,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                onClick = onOpenGuidebook,
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.22f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("unit_guidebook_${unit.id}")
            ) {
                Box(
                    modifier = Modifier.padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "गाइडबुक देखें: ${unit.titleHi}",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WindingPathNodeItem(
    node: LessonNode,
    indexInUnit: Int,
    totalInUnit: Int,
    unitTheme: PathUnitTheme,
    showCompanionOnSide: Boolean,
    onTapNode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = unitPalette(unitTheme)
    val density = LocalDensity.current
    val offsetFrac = pathOffsetFraction(indexInUnit, totalInUnit)
    val nextOffsetFrac = if (indexInUnit + 1 < totalInUnit) {
        pathOffsetFraction(indexInUnit + 1, totalInUnit)
    } else null

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxSwingDp: Dp = ((maxWidth - 150.dp) / 2f).coerceIn(0.dp, 88.dp)
        val xOffsetDp: Dp = maxSwingDp * offsetFrac
        val xOffsetPx = with(density) { xOffsetDp.toPx() }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Friendly Original Companion Illustration nestled on the opposite curve
                if (showCompanionOnSide) {
                    val companionSideBias = if (offsetFrac >= 0f) -1f else 1f
                    val companionOffsetPx = with(density) { (maxSwingDp * 0.92f * companionSideBias).roundToPx() }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset { IntOffset(companionOffsetPx, 0) }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "बहुत अच्छे! ✨",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_path_companion),
                            contentDescription = "पाठ मार्ग मित्र",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                // Lesson Node Circle + Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset { IntOffset(xOffsetPx.roundToInt(), 0) }
                ) {
                    CircularLessonNode(
                        node = node,
                        palette = palette,
                        onClick = onTapNode
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = node.titleHi,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (node.status == LessonNodeStatus.AVAILABLE || node.status == LessonNodeStatus.IN_PROGRESS) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                        color = if (node.status == LessonNodeStatus.LOCKED) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = node.type.labelHi,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Curved Bezier Path Connector to Next Node
            if (nextOffsetFrac != null) {
                val nextXPx = with(density) { (maxSwingDp * nextOffsetFrac).toPx() }
                val isSegmentCompleted = node.status == LessonNodeStatus.COMPLETED ||
                    node.status == LessonNodeStatus.PERFECT
                val trackColor = if (isSegmentCompleted) {
                    palette.main
                } else {
                    MaterialTheme.colorScheme.outline
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    val centerX = size.width / 2f
                    val start = Offset(centerX + xOffsetPx, 0f)
                    val end = Offset(centerX + nextXPx, size.height)
                    val curvePath = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(
                            start.x,
                            size.height * 0.55f,
                            end.x,
                            size.height * 0.45f,
                            end.x,
                            end.y
                        )
                    }
                    drawPath(
                        path = curvePath,
                        color = trackColor,
                        style = Stroke(
                            width = if (isSegmentCompleted) 6.dp.toPx() else 4.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = if (isSegmentCompleted) null else PathEffect.dashPathEffect(
                                floatArrayOf(12.dp.toPx(), 10.dp.toPx()),
                                0f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularLessonNode(
    node: LessonNode,
    palette: UnitPalette,
    onClick: () -> Unit
) {
    val isAvailable = node.status == LessonNodeStatus.AVAILABLE
    val isInProgress = node.status == LessonNodeStatus.IN_PROGRESS
    val isCurrent = isAvailable || isInProgress
    val isCompleted = node.status == LessonNodeStatus.COMPLETED
    val isPerfect = node.status == LessonNodeStatus.PERFECT
    val isLocked = node.status == LessonNodeStatus.LOCKED

    val infiniteTransition = rememberInfiniteTransition(label = "current_node_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "callout_bounce"
    )

    val faceColor = when {
        isPerfect -> SaffronSecondary
        isCompleted -> SuccessGreen
        isInProgress -> palette.main
        isAvailable -> palette.main
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val lipColor = when {
        isPerfect -> SaffronDark
        isCompleted -> EmeraldDeep
        isCurrent -> palette.lip
        else -> MaterialTheme.colorScheme.outline
    }
    val iconTint = when {
        isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
        isPerfect -> SaffronDeep
        else -> Color.White
    }

    val nodeSize = if (node.type == LessonNodeType.UNIT_TEST) 86.dp else 76.dp
    val displayStars = when {
        isPerfect -> maxOf(node.starsEarned, 3)
        isCompleted -> maxOf(node.starsEarned, 1)
        else -> 0
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isCurrent) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (isInProgress) SaffronDark else palette.main
                ),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .offset(y = bounceOffset.dp)
                    .padding(bottom = 6.dp)
                    .testTag("current_lesson_callout")
            ) {
                Text(
                    text = if (isInProgress) "जारी रखें • RESUME" else "शुरू करें • START",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isInProgress) SaffronDark else palette.main,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }

        Box(
            modifier = Modifier.size(nodeSize + 18.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(nodeSize * pulseScale)
                        .clip(CircleShape)
                        .background(
                            (if (isInProgress) SaffronSecondary else palette.main).copy(alpha = 0.22f)
                        )
                )
            }

            if (isInProgress) {
                Canvas(modifier = Modifier.size(nodeSize + 14.dp)) {
                    drawArc(
                        color = SaffronSecondary.copy(alpha = 0.28f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = SaffronSecondary,
                        startAngle = -90f,
                        sweepAngle = 225f,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 3D Tactile Button Base (Bottom Lip)
            Box(
                modifier = Modifier
                    .offset(y = 5.dp)
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(lipColor)
            )

            // Main Interactive Circle
            Surface(
                onClick = onClick,
                shape = CircleShape,
                color = faceColor,
                border = BorderStroke(
                    width = if (isCurrent) 3.dp else 2.dp,
                    color = if (isLocked) MaterialTheme.colorScheme.outline else Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier
                    .size(nodeSize)
                    .minimumInteractiveComponentSize()
                    .testTag("path_node_${node.id}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = nodeTypeIcon(node.type),
                        contentDescription = "${node.titleHi} (${node.type.labelHi}, ${node.status.labelHi})",
                        tint = iconTint,
                        modifier = Modifier.size(if (node.type == LessonNodeType.UNIT_TEST) 34.dp else 30.dp)
                    )
                }
            }

            // Corner Status Badge (Completed Check / Perfect Crown / In-Progress Play / Lock)
            if (isCompleted || isPerfect || isLocked || isInProgress) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            isPerfect -> SaffronDark
                            isCompleted -> SuccessGreen
                            isInProgress -> SaffronDark
                            else -> MaterialTheme.colorScheme.outline
                        }
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                isPerfect -> Icons.Filled.WorkspacePremium
                                isCompleted -> Icons.Filled.Check
                                isInProgress -> Icons.Filled.PlayArrow
                                else -> Icons.Filled.Lock
                            },
                            contentDescription = node.status.labelHi,
                            tint = when {
                                isPerfect -> SaffronDark
                                isCompleted -> SuccessGreen
                                isInProgress -> SaffronDark
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        if (displayStars > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { idx ->
                    val earned = idx < displayStars
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (earned) SaffronSecondary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LessonNodePopupDialog(
    node: LessonNode,
    unitTheme: PathUnitTheme,
    onStart: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = unitPalette(unitTheme)
    val isLocked = node.status == LessonNodeStatus.LOCKED
    val isInProgress = node.status == LessonNodeStatus.IN_PROGRESS
    val isCompleted = node.status == LessonNodeStatus.COMPLETED || node.status == LessonNodeStatus.PERFECT

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, if (isLocked) MaterialTheme.colorScheme.outline else palette.main),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("node_info_popup")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (isLocked) MaterialTheme.colorScheme.surfaceVariant else palette.softBg,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = nodeTypeIcon(node.type),
                                contentDescription = null,
                                tint = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else palette.main,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = node.type.labelHi,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else palette.main
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_node_popup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = node.titleHi,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = node.titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else palette.main
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "इस पाठ में क्या सीखेंगे:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = node.learningObjectiveHi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "~${node.estimatedMinutes} मिनट",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = SaffronSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isCompleted) "दोहराव (0 XP)" else "+${node.xpReward} XP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (node.bestScorePercent > 0 || node.status != LessonNodeStatus.LOCKED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = when (node.status) {
                                LessonNodeStatus.PERFECT -> SaffronSecondary.copy(alpha = 0.16f)
                                LessonNodeStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.14f)
                                LessonNodeStatus.IN_PROGRESS -> SaffronSecondary.copy(alpha = 0.12f)
                                else -> palette.softBg
                            }
                        ) {
                            Text(
                                text = "स्थिति: ${node.status.labelHi}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (node.status) {
                                    LessonNodeStatus.PERFECT -> SaffronDark
                                    LessonNodeStatus.COMPLETED -> EmeraldDeep
                                    LessonNodeStatus.IN_PROGRESS -> SaffronDark
                                    else -> palette.main
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        if (node.bestScorePercent > 0) {
                            Text(
                                text = "सर्वश्रेष्ठ स्कोर: ${node.bestScorePercent}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (isLocked) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (node.type == LessonNodeType.UNIT_TEST) {
                                    "यूनिट टेस्ट खोलने के लिए इस यूनिट के सभी पाठ ${node.passingScorePercent}%+ अंकों से पूरे करें।"
                                } else {
                                    "इस पाठ को अनलॉक करने के लिए पिछला पाठ (${node.passingScorePercent}%+ अंक) पूरा करें।"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Button(
                    onClick = onStart,
                    enabled = !isLocked,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant else palette.main,
                        contentColor = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_node_button")
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Filled.Lock else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isLocked -> "अभी लॉक है (Locked)"
                            isInProgress -> "जारी रखें • Resume (+${node.xpReward} XP)"
                            isCompleted -> "दोबारा अभ्यास करें (Practice • 0 XP)"
                            else -> "पाठ शुरू करें (+${node.xpReward} XP)"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitGuidebookSheet(
    unit: PathUnit,
    showPronunciation: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val palette = unitPalette(unit.theme)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = palette.softBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "UNIT ${unit.orderNumber} GUIDEBOOK",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.main,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close))
                }
            }

            Text(
                text = "${unit.titleHi} (${unit.titleEn})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "व्याकरण व उपयोगी नियम (Tips)",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.main,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = unit.guidebookSummaryHi,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "प्रमुख वाक्य पैटर्न (Key Phrases)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            unit.guidebookPatterns.forEach { pattern ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = pattern.patternEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = pattern.meaningHi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (showPronunciation) {
                            Text(
                                text = "उच्चारण: ${pattern.pronunciationHi}",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.main
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("समझ गया (Close)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyBoosterSheet(
    dailyWord: DailyWord,
    showPronunciation: Boolean,
    todayMinutesDone: Int,
    dailyGoalMinutes: Int,
    weeklyProgress: List<DayProgress>,
    onToggleSaveWord: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val goalProgress = (todayMinutesDone.toFloat() / dailyGoalMinutes.coerceAtLeast(1)).coerceIn(0f, 1f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "दैनिक लक्ष्य और आज का शब्द",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close))
                }
            }

            // Daily Goal + Weekly Strip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "आज का लक्ष्य: $todayMinutesDone / $dailyGoalMinutes मिनट",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${(goalProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { goalProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        strokeCap = StrokeCap.Round
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weeklyProgress.forEach { day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (day.goalMet) SuccessGreen.copy(alpha = 0.18f)
                                            else MaterialTheme.colorScheme.surface
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (day.goalMet) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${day.xpEarned}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Text(text = day.dayHi, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Daily Word
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "आज का नया शब्द (Daily Word)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${dailyWord.english} — ${dailyWord.hindi}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onToggleSaveWord) {
                            Icon(
                                imageVector = if (dailyWord.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = stringResource(R.string.cd_save_word),
                                tint = if (dailyWord.isSaved) SaffronSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (showPronunciation) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "उच्चारण: ${dailyWord.pronunciationHi}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "\"${dailyWord.exampleEn}\"\n${dailyWord.exampleHi}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSectionsOverviewSheet(
    sections: List<CourseSectionOutline>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "संपूर्ण कोर्स संरचना (25 इकाइयाँ)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close))
                }
            }
            sections.forEach { sec ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (sec.isActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    border = if (sec.isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "खंड ${sec.sectionNumber}: ${sec.titleHi}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${sec.titleEn} • ${sec.unitRangeLabel} • CEFR ${sec.cefrStage.code}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (sec.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = if (sec.isActive) "सक्रिय" else "आगामी",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (sec.isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
