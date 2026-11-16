package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.auth.signOutFromApp
import com.example.data.CourseProgressLocalStore
import com.example.data.CourseProgressRepository
import com.example.data.NetworkMonitor
import com.example.data.SocialRepository
import com.example.model.AppTab
import com.example.model.FollowButtonState
import com.example.model.FriendsSubTab
import com.example.model.LearningUnit
import com.example.model.PathUnitTheme
import com.example.model.ROUTE_LESSON
import com.example.model.ROUTE_LESSON_ARG_NODE_ID
import com.example.model.ROUTE_LESSON_PREFIX
import com.example.model.ROUTE_PROGRESS
import com.example.model.UnitStatus
import com.example.model.lessonRoute
import com.example.ui.components.BrandedSplashOverlay
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CourseSectionsOverviewSheet
import com.example.ui.components.DailyBoosterSheet
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.FollowListSheet
import com.example.ui.components.LessonNodePopupDialog
import com.example.ui.components.LessonPreviewSheet
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.PrivacySafetySheet
import com.example.ui.components.ReportContentDialog
import com.example.ui.components.UnitGuidebookSheet
import com.example.ui.components.UserSocialProfileSheet
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.PathLessonScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.theme.EnglishSeekhenTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.EditProfileFocus
import com.example.ui.viewmodel.ReportTarget
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun EnglishSeekhenApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val currentUser by remember { Firebase.auth.authStateFlow() }
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)
    var syncedUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    LaunchedEffect(currentUser) {
        syncedUser = currentUser
    }

    val isOnline by remember(context) { NetworkMonitor.observeIsOnline(context) }
        .collectAsStateWithLifecycle(initialValue = NetworkMonitor.isCurrentlyOnline(context))
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser == null && isOnline) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { syncedUser = Firebase.auth.currentUser },
                onUnauthenticated = {},
                scope = scope
            )
        }
        delay(850L)
        showSplash = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val user = currentUser ?: syncedUser
        if (user == null) {
            EnglishSeekhenTheme(darkTheme = false) {
                AuthScreen(
                    onAuthSuccess = { syncedUser = Firebase.auth.currentUser },
                    isOnline = isOnline
                )
            }
        } else {
            key(user.uid) {
                val appContext = context.applicationContext
                val db = remember(appContext) {
                    FirebaseFirestore.getInstance(
                        appContext.getString(R.string.firestore_database_id)
                    )
                }
                val socialRepository = remember(db) { SocialRepository(db) }
                val courseProgressRepository = remember(db) { CourseProgressRepository(db) }
                val courseProgressLocalStore = remember(appContext) { CourseProgressLocalStore(appContext) }
                val appViewModel: AppViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            AppViewModel(
                                socialRepository = socialRepository,
                                currentUserId = user.uid,
                                initialDisplayName = user.displayName,
                                courseProgressRepository = courseProgressRepository,
                                courseProgressLocalStore = courseProgressLocalStore
                            )
                        }
                    }
                )
                AuthenticatedAppContent(
                    viewModel = appViewModel,
                    isOnline = isOnline,
                    onSignOut = {
                        signOutFromApp(
                            credentialManager = credentialManager,
                            scope = scope,
                            onSignOutComplete = { syncedUser = null }
                        )
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showSplash,
            exit = fadeOut()
        ) {
            EnglishSeekhenTheme(darkTheme = false) {
                BrandedSplashOverlay()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedAppContent(
    viewModel: AppViewModel,
    isOnline: Boolean,
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppTab.HOME.route
    val isLessonRoute = currentRoute.startsWith("$ROUTE_LESSON_PREFIX/")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    val navigateToTab: (AppTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    EnglishSeekhenTheme(darkTheme = uiState.isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavRail = maxWidth >= 600.dp

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = if (isLessonRoute) WindowInsets(0, 0, 0, 0) else WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    if (!isLessonRoute) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                },
                bottomBar = {
                    if (!useNavRail && !isLessonRoute) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp
                        ) {
                            AppTab.entries.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab) },
                                    icon = {
                                        TabIconWithBadge(
                                            tab = tab,
                                            selected = selected,
                                            unreadCount = uiState.unreadNotificationsCount
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.labelHi,
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.route}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavRail && !isLessonRoute) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            AppTab.entries.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab) },
                                    icon = {
                                        TabIconWithBadge(
                                            tab = tab,
                                            selected = selected,
                                            unreadCount = uiState.unreadNotificationsCount
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.labelHi,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("rail_tab_${tab.route}")
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        OfflineStatusBanner(isOnline = isOnline)
                        NavHost(
                            navController = navController,
                            startDestination = AppTab.HOME.route,
                            modifier = Modifier.weight(1f)
                        ) {
                        composable(AppTab.HOME.route) {
                            HomeScreen(
                                uiState = uiState,
                                onTapPathNode = viewModel::onTapPathNode,
                                onOpenGuidebook = viewModel::openUnitGuidebook,
                                onContinueCurrentNode = {
                                    uiState.nextAvailablePathNode?.let(viewModel::onTapPathNode)
                                },
                                onOpenDailyBooster = viewModel::openDailyBoosterSheet,
                                onOpenSectionsMap = viewModel::openSectionsOverviewSheet,
                                onOpenProgress = { navController.navigate(ROUTE_PROGRESS) }
                            )
                        }
                        composable(AppTab.LEARN.route) {
                            LearnScreen(
                                uiState = uiState,
                                onSelectLevel = viewModel::selectLearnLevel,
                                onOpenUnitPreview = viewModel::openUnitPreview
                            )
                        }
                        composable(AppTab.PRACTICE.route) {
                            PracticeScreen(
                                uiState = uiState,
                                onSelectChallengeOption = viewModel::selectPracticeChallengeOption
                            )
                        }
                        composable(AppTab.FRIENDS.route) {
                            FriendsScreen(
                                uiState = uiState,
                                onSelectSubTab = viewModel::selectFriendsSubTab,
                                onRefreshFeed = viewModel::refreshFeed,
                                onLoadMoreFeed = viewModel::loadMoreFeed,
                                onSearchQueryChange = viewModel::onFriendSearchQueryChange,
                                onToggleFollow = viewModel::toggleFollow,
                                onOpenUserProfile = viewModel::openUserSocialProfile,
                                onToggleLike = viewModel::toggleLike,
                                onOpenComments = viewModel::openComments,
                                onHideActivity = viewModel::hideActivity,
                                onReportActivity = { act ->
                                    viewModel.openReportDialog(
                                        ReportTarget(
                                            reportedUserId = act.userId,
                                            contentType = "ACTIVITY",
                                            contentId = act.id,
                                            targetLabel = "@${act.username} की पोस्ट"
                                        )
                                    )
                                },
                                onClickNotification = viewModel::onClickNotification,
                                onMarkAllNotificationsRead = viewModel::markAllNotificationsRead
                            )
                        }
                        composable(AppTab.PROFILE.route) {
                            ProfileScreen(
                                uiState = uiState,
                                onSelectDailyGoal = viewModel::setDailyGoalMinutes,
                                onToggleDarkTheme = viewModel::toggleDarkTheme,
                                onTogglePronunciationGuide = viewModel::togglePronunciationGuide,
                                onOpenEditProfile = { viewModel.openEditProfileDialog(EditProfileFocus.ALL) },
                                onEditAvatar = { viewModel.openEditProfileDialog(EditProfileFocus.AVATAR) },
                                onEditDisplayName = { viewModel.openEditProfileDialog(EditProfileFocus.DISPLAY_NAME) },
                                onEditUsername = { viewModel.openEditProfileDialog(EditProfileFocus.USERNAME) },
                                onEditBio = { viewModel.openEditProfileDialog(EditProfileFocus.BIO) },
                                onOpenPrivacySafety = viewModel::openPrivacySafetySheet,
                                onOpenFollowers = {
                                    viewModel.openFollowersList(uiState.currentUserId)
                                },
                                onOpenFollowing = {
                                    viewModel.openFollowingList(uiState.currentUserId)
                                },
                                onOpenProgress = { navController.navigate(ROUTE_PROGRESS) },
                                onOpenFindFriends = {
                                    viewModel.selectFriendsSubTab(FriendsSubTab.FIND_FRIENDS)
                                    navigateToTab(AppTab.FRIENDS)
                                },
                                onSignOut = onSignOut
                            )
                        }
                        composable(ROUTE_PROGRESS) {
                            ProgressScreen(
                                uiState = uiState,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = ROUTE_LESSON,
                            arguments = listOf(
                                navArgument(ROUTE_LESSON_ARG_NODE_ID) { type = NavType.StringType }
                            )
                        ) { entry ->
                            val nodeId = entry.arguments?.getString(ROUTE_LESSON_ARG_NODE_ID).orEmpty()
                            val parentUnit = uiState.resolvedPathUnits.firstOrNull { unit ->
                                unit.nodes.any { it.id == nodeId }
                            }
                            val targetNode = parentUnit?.nodes?.firstOrNull { it.id == nodeId }
                            if (parentUnit != null && targetNode != null) {
                                PathLessonScreen(
                                    node = targetNode,
                                    unit = parentUnit,
                                    allUnits = uiState.resolvedPathUnits,
                                    showPronunciation = uiState.showHindiPronunciation,
                                    latestOutcome = uiState.latestAttemptOutcome?.takeIf { it.nodeId == targetNode.id },
                                    onExitMidLesson = viewModel::markPathNodeInProgress,
                                    onSubmitAttempt = viewModel::submitPathNodeAttempt,
                                    onNavigateToNode = { nextId ->
                                        viewModel.beginPathNodeSession(nextId)
                                        navController.navigate(lessonRoute(nextId)) {
                                            popUpTo(ROUTE_LESSON) { inclusive = true }
                                        }
                                    },
                                    onBackToPath = {
                                        viewModel.clearLatestAttemptOutcome()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        }
                    }
                }
            }

            // Lesson Preview Sheet (from Learn tab)
            val previewUnit = uiState.activePreviewUnit
            if (previewUnit != null) {
                LessonPreviewSheet(
                    unit = previewUnit,
                    showPronunciation = uiState.showHindiPronunciation,
                    selectedOptionIndex = uiState.previewQuizSelectedIndex,
                    isRewarded = uiState.previewQuizRewarded,
                    onSelectOption = viewModel::selectPreviewQuizOption,
                    onDismiss = viewModel::closeUnitPreview
                )
            }

            // Path Node Info Popup -> launches dedicated lesson screen route
            val selectedPathNode = uiState.selectedPathNode
            if (selectedPathNode != null) {
                val nodeUnitTheme = uiState.resolvedPathUnits
                    .firstOrNull { it.id == selectedPathNode.unitId }?.theme
                    ?: PathUnitTheme.EMERALD
                LessonNodePopupDialog(
                    node = selectedPathNode,
                    unitTheme = nodeUnitTheme,
                    onStart = {
                        viewModel.beginPathNodeSession(selectedPathNode.id)
                        navController.navigate(lessonRoute(selectedPathNode.id))
                    },
                    onDismiss = viewModel::dismissPathNodePopup
                )
            }

            // Phase 1 Active Path Node Starter Lesson Sheet
            val activePathNode = uiState.activePathNode
            if (activePathNode != null) {
                val parentUnit = uiState.resolvedPathUnits.firstOrNull { it.id == activePathNode.unitId }
                val adaptedUnit = LearningUnit(
                    id = activePathNode.id,
                    levelNumber = 1,
                    orderNumber = parentUnit?.orderNumber ?: 1,
                    titleEn = activePathNode.titleEn,
                    titleHi = activePathNode.titleHi,
                    lessonsCount = parentUnit?.nodes?.size ?: 1,
                    completedLessons = activePathNode.orderInUnit,
                    estimatedMinutes = activePathNode.estimatedMinutes,
                    xpReward = activePathNode.xpReward,
                    status = UnitStatus.ACTIVE,
                    sampleLesson = activePathNode.samplePreview
                )
                LessonPreviewSheet(
                    unit = adaptedUnit,
                    showPronunciation = uiState.showHindiPronunciation,
                    selectedOptionIndex = uiState.previewQuizSelectedIndex,
                    isRewarded = uiState.previewQuizRewarded,
                    onSelectOption = viewModel::selectPathNodeQuizOption,
                    onDismiss = viewModel::closeActivePathNode
                )
            }

            // Phase 1 Unit Guidebook Sheet
            val guidebookUnit = uiState.activeGuidebookUnit
            if (guidebookUnit != null) {
                UnitGuidebookSheet(
                    unit = guidebookUnit,
                    showPronunciation = uiState.showHindiPronunciation,
                    onDismiss = viewModel::closeUnitGuidebook
                )
            }

            // Daily Word & Weekly Goal Booster Sheet
            if (uiState.showDailyBoosterSheet) {
                DailyBoosterSheet(
                    dailyWord = uiState.dailyWord,
                    showPronunciation = uiState.showHindiPronunciation,
                    todayMinutesDone = uiState.todayMinutesDone,
                    dailyGoalMinutes = uiState.dailyGoalMinutes,
                    weeklyProgress = uiState.weeklyProgress,
                    onToggleSaveWord = viewModel::toggleDailyWordSaved,
                    onDismiss = viewModel::closeDailyBoosterSheet
                )
            }

            // Course Sections Overview Sheet
            if (uiState.showSectionsOverviewSheet) {
                CourseSectionsOverviewSheet(
                    sections = uiState.sectionOutlines,
                    onDismiss = viewModel::closeSectionsOverviewSheet
                )
            }

            // Other User Profile Sheet
            val viewedProfile = uiState.viewedUserProfile
            if (viewedProfile != null) {
                val isFollowing = viewedProfile.userId in uiState.followingUserIds
                val isFollower = viewedProfile.userId in uiState.followerUserIds
                val followState = when {
                    isFollowing -> FollowButtonState.FOLLOWING
                    isFollower -> FollowButtonState.FOLLOW_BACK
                    else -> FollowButtonState.FOLLOW
                }
                UserSocialProfileSheet(
                    profile = viewedProfile,
                    isMe = viewedProfile.userId == uiState.currentUserId,
                    followState = followState,
                    achievements = uiState.viewedUserAchievements,
                    activities = uiState.viewedUserActivities,
                    likedIds = uiState.likedActivityIds,
                    onToggleFollow = { viewModel.toggleFollow(viewedProfile) },
                    onOpenFollowers = { viewModel.openFollowersList(viewedProfile.userId) },
                    onOpenFollowing = { viewModel.openFollowingList(viewedProfile.userId) },
                    onBlockUser = { viewModel.blockUser(viewedProfile) },
                    onReportUser = {
                        viewModel.openReportDialog(
                            ReportTarget(
                                reportedUserId = viewedProfile.userId,
                                contentType = "USER",
                                contentId = viewedProfile.userId,
                                targetLabel = "@${viewedProfile.username}"
                            )
                        )
                    },
                    onToggleLike = viewModel::toggleLike,
                    onOpenComments = viewModel::openComments,
                    onDismiss = viewModel::closeUserSocialProfile
                )
            }

            // Followers / Following List Sheet
            val followListTitle = uiState.followListModalTitle
            if (followListTitle != null) {
                FollowListSheet(
                    title = followListTitle,
                    relations = uiState.followListRelations,
                    showFollowers = uiState.followListShowFollowers,
                    onOpenUser = { uid ->
                        viewModel.closeFollowList()
                        viewModel.openUserSocialProfile(uid)
                    },
                    onDismiss = viewModel::closeFollowList
                )
            }

            // Comments Bottom Sheet
            val commentActivity = uiState.activeCommentActivity
            if (commentActivity != null) {
                CommentsBottomSheet(
                    activity = commentActivity,
                    comments = uiState.activeComments,
                    currentUserId = uiState.currentUserId,
                    replyingTo = uiState.replyingToComment,
                    onSetReplyingTo = viewModel::setReplyingToComment,
                    onSendComment = viewModel::sendComment,
                    onDeleteComment = viewModel::deleteComment,
                    onReportComment = { comment ->
                        viewModel.openReportDialog(
                            ReportTarget(
                                reportedUserId = comment.userId,
                                contentType = "COMMENT",
                                contentId = comment.id,
                                targetLabel = "@${comment.username} की टिप्पणी"
                            )
                        )
                    },
                    onOpenUserProfile = { uid ->
                        viewModel.closeComments()
                        viewModel.openUserSocialProfile(uid)
                    },
                    onDismiss = viewModel::closeComments
                )
            }

            // Edit Profile Dialog
            val myProfile = uiState.myProfile
            if (uiState.showEditProfileDialog && myProfile != null) {
                EditProfileDialog(
                    profile = myProfile,
                    initialFocus = uiState.editProfileFocus,
                    isSaving = uiState.isSavingProfile,
                    onSave = viewModel::saveProfile,
                    onDismiss = viewModel::closeEditProfileDialog
                )
            }

            // Privacy & Safety Sheet
            if (uiState.showPrivacySafetySheet && myProfile != null) {
                PrivacySafetySheet(
                    profile = myProfile,
                    blockedUsers = uiState.blockedUsers,
                    onUpdatePrivacy = viewModel::updatePrivacySettings,
                    onUnblockUser = viewModel::unblockUser,
                    onDismiss = viewModel::closePrivacySafetySheet
                )
            }

            // Report Dialog
            val reportTarget = uiState.reportTarget
            if (reportTarget != null) {
                ReportContentDialog(
                    targetLabel = reportTarget.targetLabel,
                    onSubmit = viewModel::submitReport,
                    onDismiss = viewModel::closeReportDialog
                )
            }
        }
    }
}

@Composable
private fun TabIconWithBadge(
    tab: AppTab,
    selected: Boolean,
    unreadCount: Int
) {
    val icon = tabIcon(tab, selected)
    if (tab == AppTab.FRIENDS && unreadCount > 0) {
        BadgedBox(
            badge = {
                Badge {
                    Text(if (unreadCount > 9) "9+" else "$unreadCount")
                }
            }
        ) {
            Icon(imageVector = icon, contentDescription = tab.labelHi)
        }
    } else {
        Icon(imageVector = icon, contentDescription = tab.labelHi)
    }
}

private fun tabIcon(tab: AppTab, selected: Boolean): ImageVector {
    return when (tab) {
        AppTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        AppTab.LEARN -> if (selected) Icons.Filled.School else Icons.Outlined.School
        AppTab.PRACTICE -> if (selected) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter
        AppTab.FRIENDS -> if (selected) Icons.Filled.Group else Icons.Outlined.Group
        AppTab.PROFILE -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
    }
}
