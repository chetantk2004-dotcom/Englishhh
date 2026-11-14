package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CourseProgressLocalStore
import com.example.data.CourseProgressRepository
import com.example.data.LearningPathRepository
import com.example.data.PathProgressionEngine
import com.example.data.SampleCurriculumRepository
import com.example.data.SocialRepository
import com.example.data.friendlyFirestoreMessageHi
import com.example.model.AchievementBadge
import com.example.model.AchievementCatalog
import com.example.model.ActivityFeedItem
import com.example.model.AppNotification
import com.example.model.BlockedUser
import com.example.model.CourseSection
import com.example.model.CourseSectionOutline
import com.example.model.DailyWord
import com.example.model.DayProgress
import com.example.model.FollowRelation
import com.example.model.FriendsSubTab
import com.example.model.GrammarPreview
import com.example.model.LearningLevel
import com.example.model.LearningUnit
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.NodeProgressRecord
import com.example.model.PathAttemptOutcome
import com.example.model.PathUnit
import com.example.model.PhaseRoadmapItem
import com.example.model.PostComment
import com.example.model.TopicStrength
import com.example.model.UserAchievement
import com.example.model.UserProfile
import com.example.model.VocabCategory
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportTarget(
    val reportedUserId: String,
    val contentType: String,
    val contentId: String,
    val targetLabel: String
)

enum class EditProfileFocus {
    ALL,
    AVATAR,
    DISPLAY_NAME,
    USERNAME,
    BIO
}

data class MainUiState(
    val currentUserId: String = "",
    val learnerName: String = "सीखने वाले मित्र",
    val currentLevelNumber: Int = 1,
    val selectedLearnLevel: Int = 1,
    val totalXp: Int = 240,
    val currentStreakDays: Int = 5,
    val dailyGoalMinutes: Int = 10,
    val todayMinutesDone: Int = 6,
    val lessonsCompleted: Int = 9,
    val wordsLearned: Int = 38,
    val grammarTopicsDone: Int = 1,
    val totalLearningMinutes: Int = 85,
    val isDarkTheme: Boolean = false,
    val showHindiPronunciation: Boolean = true,
    val levels: List<LearningLevel> = SampleCurriculumRepository.levels,
    val currentSection: CourseSection = LearningPathRepository.phase1Section,
    val sectionOutlines: List<CourseSectionOutline> = LearningPathRepository.sectionOutlines,
    val pathProgressRecords: Map<String, NodeProgressRecord> =
        PathProgressionEngine.initialProgressMap(LearningPathRepository.phase1Section),
    val latestAttemptOutcome: PathAttemptOutcome? = null,
    val selectedPathNode: LessonNode? = null,
    val activePathNode: LessonNode? = null,
    val pathNodeFirstAttempt: Boolean = true,
    val activeGuidebookUnit: PathUnit? = null,
    val showDailyBoosterSheet: Boolean = false,
    val showSectionsOverviewSheet: Boolean = false,
    val dailyWord: DailyWord = SampleCurriculumRepository.initialDailyWord,
    val vocabCategories: List<VocabCategory> = SampleCurriculumRepository.vocabCategories,
    val grammarPreviews: List<GrammarPreview> = SampleCurriculumRepository.grammarPreviews,
    val weeklyProgress: List<DayProgress> = SampleCurriculumRepository.weeklyProgress,
    val topicStrengths: List<TopicStrength> = SampleCurriculumRepository.topicStrengths,
    val achievements: List<AchievementBadge> = SampleCurriculumRepository.achievements,
    val roadmapPhases: List<PhaseRoadmapItem> = SampleCurriculumRepository.roadmapPhases,
    val activePreviewUnit: LearningUnit? = null,
    val previewQuizSelectedIndex: Int? = null,
    val previewQuizRewarded: Boolean = false,
    val practiceChallengeSelectedIndex: Int? = null,
    val practiceChallengeRewarded: Boolean = false,
    // Social state
    val myProfile: UserProfile? = null,
    val isProfileLoading: Boolean = true,
    val isSavingProfile: Boolean = false,
    val myAchievements: List<UserAchievement> = emptyList(),
    val friendsSubTab: FriendsSubTab = FriendsSubTab.FEED,
    val friendsFeed: List<ActivityFeedItem> = emptyList(),
    val feedLimitPerChunk: Long = 20L,
    val isFeedLoading: Boolean = false,
    val likedActivityIds: Set<String> = emptySet(),
    val followingRelations: List<FollowRelation> = emptyList(),
    val followerRelations: List<FollowRelation> = emptyList(),
    val followingUserIds: Set<String> = emptySet(),
    val followerUserIds: Set<String> = emptySet(),
    val blockedUsers: List<BlockedUser> = emptyList(),
    val blockedUserIds: Set<String> = emptySet(),
    val notifications: List<AppNotification> = emptyList(),
    val friendSearchQuery: String = "",
    val searchResults: List<UserProfile> = emptyList(),
    val suggestedUsers: List<UserProfile> = emptyList(),
    val isSearchingFriends: Boolean = false,
    val followActionBusyIds: Set<String> = emptySet(),
    val viewedUserProfile: UserProfile? = null,
    val viewedUserAchievements: List<UserAchievement> = emptyList(),
    val viewedUserActivities: List<ActivityFeedItem> = emptyList(),
    val followListModalTitle: String? = null,
    val followListRelations: List<FollowRelation> = emptyList(),
    val followListShowFollowers: Boolean = true,
    val activeCommentActivity: ActivityFeedItem? = null,
    val activeComments: List<PostComment> = emptyList(),
    val replyingToComment: PostComment? = null,
    val showEditProfileDialog: Boolean = false,
    val editProfileFocus: EditProfileFocus = EditProfileFocus.ALL,
    val showPrivacySafetySheet: Boolean = false,
    val reportTarget: ReportTarget? = null,
    val snackbarMessage: String? = null
) {
    val activeUnit: LearningUnit
        get() = levels.firstOrNull { it.levelNumber == 1 }?.units?.getOrNull(2)
            ?: levels.first().units.first()

    val resolvedPathUnits: List<PathUnit>
        get() = PathProgressionEngine.resolveUnits(currentSection, pathProgressRecords)

    val nextAvailablePathNode: LessonNode?
        get() = resolvedPathUnits
            .flatMap { it.nodes }
            .firstOrNull {
                it.status == LessonNodeStatus.IN_PROGRESS || it.status == LessonNodeStatus.AVAILABLE
            }
            ?: resolvedPathUnits.firstOrNull()?.nodes?.firstOrNull()

    val dailyGoalProgress: Float
        get() = (todayMinutesDone.toFloat() / dailyGoalMinutes.coerceAtLeast(1)).coerceIn(0f, 1f)

    val currentLearnLevelData: LearningLevel
        get() = levels.firstOrNull { it.levelNumber == selectedLearnLevel } ?: levels.first()

    val unreadNotificationsCount: Int
        get() = notifications.count { !it.isRead }
}

class AppViewModel(
    private val socialRepository: SocialRepository,
    private val currentUserId: String,
    private val initialDisplayName: String?,
    private val courseProgressRepository: CourseProgressRepository? = null,
    private val courseProgressLocalStore: CourseProgressLocalStore? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(currentUserId = currentUserId))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var commentsJob: Job? = null

    init {
        hydrateLocalCourseProgress()
        initializeUserAndSync()
    }

    private fun hydrateLocalCourseProgress() {
        val localRecords = courseProgressLocalStore?.loadProgress(currentUserId).orEmpty()
        if (localRecords.isNotEmpty()) {
            _uiState.update { state ->
                state.copy(
                    pathProgressRecords = PathProgressionEngine.mergeRecords(
                        state.pathProgressRecords,
                        localRecords
                    )
                )
            }
        }
    }

    private fun initializeUserAndSync() {
        viewModelScope.launch {
            socialRepository.ensureUserProfile(
                preferredDisplayName = initialDisplayName,
                initialXp = _uiState.value.totalXp.toLong(),
                initialStreak = _uiState.value.currentStreakDays.toLong(),
                initialLessons = _uiState.value.lessonsCompleted.toLong(),
                initialWords = _uiState.value.wordsLearned.toLong()
            ).onSuccess { profile ->
                _uiState.update { state ->
                    state.copy(
                        myProfile = profile,
                        isProfileLoading = false,
                        learnerName = profile.displayName,
                        totalXp = profile.totalXp.toInt(),
                        currentStreakDays = profile.streak.toInt(),
                        lessonsCompleted = profile.lessonsCompleted.toInt(),
                        wordsLearned = profile.wordsLearned.toInt()
                    )
                }
                refreshMyAchievements()
                loadSuggestedUsers()
                refreshFeed()
            }.onFailure { error ->
                _uiState.update { it.copy(isProfileLoading = false) }
                if (error is CancellationException) throw error
                showError(error)
            }
        }

        viewModelScope.launch {
            socialRepository.observeUserProfile(currentUserId)
                .catch { error ->
                    _uiState.update { it.copy(isProfileLoading = false) }
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { profile ->
                    if (profile != null) {
                        _uiState.update { state ->
                            state.copy(
                                myProfile = profile,
                                isProfileLoading = false,
                                learnerName = profile.displayName,
                                totalXp = profile.totalXp.toInt(),
                                currentStreakDays = profile.streak.toInt(),
                                lessonsCompleted = profile.lessonsCompleted.toInt(),
                                wordsLearned = profile.wordsLearned.toInt(),
                                viewedUserProfile = if (state.viewedUserProfile?.userId == profile.userId) {
                                    profile
                                } else {
                                    state.viewedUserProfile
                                }
                            )
                        }
                    }
                }
        }

        viewModelScope.launch {
            socialRepository.observeFollowingRelations(currentUserId)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { relations ->
                    val ids = relations.map { it.followedUserId }.toSet()
                    _uiState.update {
                        it.copy(
                            followingRelations = relations,
                            followingUserIds = ids
                        )
                    }
                    refreshFeed()
                }
        }

        viewModelScope.launch {
            socialRepository.observeFollowerRelations(currentUserId)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { relations ->
                    val ids = relations.map { it.followerUserId }.toSet()
                    _uiState.update {
                        it.copy(
                            followerRelations = relations,
                            followerUserIds = ids
                        )
                    }
                }
        }

        viewModelScope.launch {
            socialRepository.observeBlockedUsers(currentUserId)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { blocked ->
                    val blockedIds = blocked.map { it.blockedUserId }.toSet()
                    _uiState.update { state ->
                        state.copy(
                            blockedUsers = blocked,
                            blockedUserIds = blockedIds,
                            friendsFeed = state.friendsFeed.filter { it.userId !in blockedIds },
                            suggestedUsers = state.suggestedUsers.filter { it.userId !in blockedIds },
                            searchResults = state.searchResults.filter { it.userId !in blockedIds }
                        )
                    }
                    refreshFeed()
                }
        }

        viewModelScope.launch {
            socialRepository.observeNotifications(currentUserId)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { items ->
                    _uiState.update { it.copy(notifications = items) }
                }
        }

        viewModelScope.launch {
            socialRepository.observeMyLikedActivityIds(currentUserId)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { likedIds ->
                    _uiState.update { it.copy(likedActivityIds = likedIds) }
                }
        }

        courseProgressRepository?.let { progressRepo ->
            viewModelScope.launch {
                progressRepo.observeCourseProgress(currentUserId)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        showError(firestoreError ?: error)
                    }
                    .collectLatest { remoteRecords ->
                        if (remoteRecords.isNotEmpty()) {
                            val merged = PathProgressionEngine.mergeRecords(
                                _uiState.value.pathProgressRecords,
                                remoteRecords
                            )
                            courseProgressLocalStore?.saveAll(currentUserId, merged)
                            _uiState.update { it.copy(pathProgressRecords = merged) }
                        }
                    }
            }
        }
    }

    private fun refreshMyAchievements() {
        viewModelScope.launch {
            socialRepository.getUserAchievements(currentUserId, amIFollowing = true)
                .onSuccess { earnedList ->
                    val earnedTypes = earnedList.map { it.achievementType }.toSet()
                    val mergedBadges = AchievementCatalog.all.map { def ->
                        val unlocked = def.type in earnedTypes
                        AchievementBadge(
                            id = def.type,
                            titleHi = def.titleHi,
                            titleEn = def.titleEn,
                            descriptionHi = def.descriptionHi,
                            isUnlocked = unlocked,
                            progressText = if (unlocked) "प्राप्त ✓" else "बाकी"
                        )
                    }
                    _uiState.update {
                        it.copy(
                            myAchievements = earnedList,
                            achievements = mergedBadges
                        )
                    }
                }
        }
    }

    // --- Learning Actions ---

    fun selectLearnLevel(levelNumber: Int) {
        _uiState.update { it.copy(selectedLearnLevel = levelNumber) }
    }

    fun toggleDailyWordSaved() {
        _uiState.update { state ->
            state.copy(
                dailyWord = state.dailyWord.copy(isSaved = !state.dailyWord.isSaved)
            )
        }
    }

    fun setDailyGoalMinutes(minutes: Int) {
        _uiState.update { it.copy(dailyGoalMinutes = minutes) }
    }

    fun toggleDarkTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun togglePronunciationGuide() {
        _uiState.update { it.copy(showHindiPronunciation = !it.showHindiPronunciation) }
    }

    fun openUnitPreview(unit: LearningUnit) {
        _uiState.update {
            it.copy(
                activePreviewUnit = unit,
                previewQuizSelectedIndex = null,
                previewQuizRewarded = false
            )
        }
    }

    fun closeUnitPreview() {
        _uiState.update {
            it.copy(
                activePreviewUnit = null,
                previewQuizSelectedIndex = null,
                previewQuizRewarded = false
            )
        }
    }

    // --- Learning Path Actions (Phase 2 Progression + Persistence) ---

    fun onTapPathNode(node: LessonNode) {
        _uiState.update { it.copy(selectedPathNode = node) }
    }

    fun dismissPathNodePopup() {
        _uiState.update { it.copy(selectedPathNode = null) }
    }

    fun beginPathNodeSession(nodeId: String) {
        _uiState.update {
            it.copy(
                selectedPathNode = null,
                latestAttemptOutcome = null
            )
        }
    }

    fun markPathNodeInProgress(nodeId: String) {
        val currentState = _uiState.value
        val (updatedMap, changedRecord) = PathProgressionEngine.markInProgress(
            section = currentState.currentSection,
            records = currentState.pathProgressRecords,
            nodeId = nodeId
        )
        if (changedRecord == null) return

        _uiState.update { it.copy(pathProgressRecords = updatedMap) }
        courseProgressLocalStore?.saveRecord(currentUserId, changedRecord)
        if (changedRecord.status == LessonNodeStatus.IN_PROGRESS.name) {
            viewModelScope.launch {
                courseProgressRepository?.saveNodeProgress(changedRecord)
                    ?.onFailure { error ->
                        if (error is CancellationException) throw error
                    }
            }
        }
    }

    fun submitPathNodeAttempt(nodeId: String, scorePercent: Int) {
        val currentState = _uiState.value
        val node = PathProgressionEngine.findNodeById(
            currentState.currentSection,
            currentState.pathProgressRecords,
            nodeId
        ) ?: return

        val (updatedMap, outcome, changedRecords) = PathProgressionEngine.recordAttempt(
            section = currentState.currentSection,
            records = currentState.pathProgressRecords,
            nodeId = nodeId,
            scorePercent = scorePercent
        )

        val rewardGranted = outcome.xpGranted > 0
        _uiState.update { state ->
            state.copy(
                pathProgressRecords = updatedMap,
                latestAttemptOutcome = outcome,
                totalXp = if (rewardGranted) state.totalXp + outcome.xpGranted else state.totalXp,
                lessonsCompleted = if (rewardGranted) state.lessonsCompleted + 1 else state.lessonsCompleted,
                todayMinutesDone = if (rewardGranted) {
                    (state.todayMinutesDone + node.estimatedMinutes).coerceAtMost(state.dailyGoalMinutes + 15)
                } else {
                    state.todayMinutesDone
                }
            )
        }

        courseProgressLocalStore?.saveAll(currentUserId, updatedMap)
        viewModelScope.launch {
            courseProgressRepository?.saveMultipleProgress(changedRecords)
                ?.onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }

        if (rewardGranted) {
            syncLearningProgressToCloud(
                xpDelta = outcome.xpGranted.toLong(),
                lessonsDelta = 1L,
                wordsDelta = 2L
            )
        }
    }

    fun clearLatestAttemptOutcome() {
        _uiState.update { it.copy(latestAttemptOutcome = null) }
    }

    fun startPathNode(node: LessonNode) {
        if (node.status == LessonNodeStatus.LOCKED) return
        _uiState.update {
            it.copy(
                selectedPathNode = null,
                activePathNode = node,
                pathNodeFirstAttempt = true,
                previewQuizSelectedIndex = null,
                previewQuizRewarded = false
            )
        }
    }

    fun closeActivePathNode() {
        _uiState.update {
            it.copy(
                activePathNode = null,
                pathNodeFirstAttempt = true,
                previewQuizSelectedIndex = null,
                previewQuizRewarded = false
            )
        }
    }

    fun selectPathNodeQuizOption(index: Int) {
        val currentState = _uiState.value
        val node = currentState.activePathNode ?: return
        val isCorrect = index == node.samplePreview.correctOptionIndex

        if (!isCorrect) {
            _uiState.update {
                it.copy(
                    previewQuizSelectedIndex = index,
                    pathNodeFirstAttempt = false
                )
            }
            return
        }

        val score = if (currentState.pathNodeFirstAttempt) 100 else 80
        submitPathNodeAttempt(node.id, score)
        _uiState.update { state ->
            val updatedNode = PathProgressionEngine.findNodeById(
                state.currentSection,
                state.pathProgressRecords,
                node.id
            )
            state.copy(
                previewQuizSelectedIndex = index,
                previewQuizRewarded = true,
                activePathNode = updatedNode ?: node
            )
        }
    }

    fun openUnitGuidebook(unit: PathUnit) {
        _uiState.update { it.copy(activeGuidebookUnit = unit) }
    }

    fun closeUnitGuidebook() {
        _uiState.update { it.copy(activeGuidebookUnit = null) }
    }

    fun openDailyBoosterSheet() {
        _uiState.update { it.copy(showDailyBoosterSheet = true) }
    }

    fun closeDailyBoosterSheet() {
        _uiState.update { it.copy(showDailyBoosterSheet = false) }
    }

    fun openSectionsOverviewSheet() {
        _uiState.update { it.copy(showSectionsOverviewSheet = true) }
    }

    fun closeSectionsOverviewSheet() {
        _uiState.update { it.copy(showSectionsOverviewSheet = false) }
    }

    fun selectPreviewQuizOption(index: Int) {
        val currentState = _uiState.value
        val unit = currentState.activePreviewUnit ?: return
        val isCorrect = index == unit.sampleLesson.correctOptionIndex
        val shouldReward = isCorrect && !currentState.previewQuizRewarded

        _uiState.update { state ->
            state.copy(
                previewQuizSelectedIndex = index,
                previewQuizRewarded = state.previewQuizRewarded || isCorrect,
                totalXp = if (shouldReward) state.totalXp + unit.sampleLesson.xpReward else state.totalXp,
                lessonsCompleted = if (shouldReward) state.lessonsCompleted + 1 else state.lessonsCompleted,
                todayMinutesDone = if (shouldReward) {
                    (state.todayMinutesDone + 2).coerceAtMost(state.dailyGoalMinutes + 10)
                } else {
                    state.todayMinutesDone
                }
            )
        }

        if (shouldReward) {
            syncLearningProgressToCloud(
                xpDelta = unit.sampleLesson.xpReward.toLong(),
                lessonsDelta = 1L,
                wordsDelta = 2L
            )
        }
    }

    fun selectPracticeChallengeOption(index: Int) {
        val currentState = _uiState.value
        val isCorrect = index == 1 // "विनम्र / सभ्य" for "Polite"
        val shouldReward = isCorrect && !currentState.practiceChallengeRewarded

        _uiState.update { state ->
            state.copy(
                practiceChallengeSelectedIndex = index,
                practiceChallengeRewarded = state.practiceChallengeRewarded || isCorrect,
                totalXp = if (shouldReward) state.totalXp + 15 else state.totalXp,
                wordsLearned = if (shouldReward) state.wordsLearned + 1 else state.wordsLearned
            )
        }

        if (shouldReward) {
            syncLearningProgressToCloud(
                xpDelta = 15L,
                lessonsDelta = 0L,
                wordsDelta = 1L
            )
        }
    }

    private fun syncLearningProgressToCloud(
        xpDelta: Long,
        lessonsDelta: Long,
        wordsDelta: Long
    ) {
        val me = _uiState.value.myProfile ?: return
        viewModelScope.launch {
            socialRepository.recordLearningProgressAndAchievements(
                me = me,
                xpDelta = xpDelta,
                lessonsDelta = lessonsDelta,
                wordsDelta = wordsDelta
            ).onSuccess { newlyEarned ->
                if (newlyEarned.isNotEmpty()) {
                    val names = newlyEarned.joinToString(", ") { it.titleHi }
                    _uiState.update {
                        it.copy(snackbarMessage = "बधाई हो! नई उपलब्धि मिली: $names 🏆")
                    }
                    refreshMyAchievements()
                    refreshFeed()
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error
                showError(error)
            }
        }
    }

    // --- Friends & Feed Actions ---

    fun selectFriendsSubTab(subTab: FriendsSubTab) {
        _uiState.update { it.copy(friendsSubTab = subTab) }
        if (subTab == FriendsSubTab.FIND_FRIENDS && _uiState.value.suggestedUsers.isEmpty()) {
            loadSuggestedUsers()
        } else if (subTab == FriendsSubTab.FEED && _uiState.value.friendsFeed.isEmpty()) {
            refreshFeed()
        }
    }

    fun refreshFeed() {
        val state = _uiState.value
        if (state.myProfile == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isFeedLoading = true) }
            socialRepository.loadFriendsFeed(
                followingUserIds = _uiState.value.followingUserIds.toList(),
                blockedUserIds = _uiState.value.blockedUserIds,
                limitPerChunk = _uiState.value.feedLimitPerChunk
            ).onSuccess { feed ->
                _uiState.update {
                    it.copy(
                        friendsFeed = feed,
                        isFeedLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isFeedLoading = false) }
                if (error is CancellationException) throw error
                showError(error)
            }
        }
    }

    fun loadMoreFeed() {
        _uiState.update { it.copy(feedLimitPerChunk = it.feedLimitPerChunk + 15L) }
        refreshFeed()
    }

    fun loadSuggestedUsers() {
        viewModelScope.launch {
            socialRepository.getSuggestedUsers(_uiState.value.blockedUserIds)
                .onSuccess { users ->
                    _uiState.update { it.copy(suggestedUsers = users) }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                }
        }
    }

    fun onFriendSearchQueryChange(query: String) {
        _uiState.update { it.copy(friendSearchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearchingFriends = false) }
            loadSuggestedUsers()
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearchingFriends = true) }
            delay(300L)
            socialRepository.searchUsers(query, _uiState.value.blockedUserIds)
                .onSuccess { results ->
                    _uiState.update {
                        it.copy(
                            searchResults = results,
                            isSearchingFriends = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSearchingFriends = false) }
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun toggleFollow(target: UserProfile) {
        val state = _uiState.value
        val me = state.myProfile ?: return
        if (target.userId == currentUserId) return
        if (target.userId in state.followActionBusyIds) return

        val currentlyFollowing = target.userId in state.followingUserIds
        _uiState.update { it.copy(followActionBusyIds = it.followActionBusyIds + target.userId) }

        viewModelScope.launch {
            val result = if (currentlyFollowing) {
                socialRepository.unfollowUser(me, target.userId)
            } else {
                socialRepository.followUser(me, target)
            }

            _uiState.update { it.copy(followActionBusyIds = it.followActionBusyIds - target.userId) }

            result.onSuccess {
                if (state.viewedUserProfile?.userId == target.userId) {
                    openUserSocialProfile(target.userId)
                }
                loadSuggestedUsers()
                if (_uiState.value.friendSearchQuery.isNotBlank()) {
                    onFriendSearchQueryChange(_uiState.value.friendSearchQuery)
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error
                showError(error)
            }
        }
    }

    fun openUserSocialProfile(targetUserId: String) {
        if (targetUserId in _uiState.value.blockedUserIds) {
            _uiState.update { it.copy(snackbarMessage = "आपने इस उपयोगकर्ता को ब्लॉक किया हुआ है।") }
            return
        }
        viewModelScope.launch {
            socialRepository.getUserProfileById(targetUserId)
                .onSuccess { profile ->
                    if (profile == null) {
                        _uiState.update { it.copy(snackbarMessage = "यह प्रोफ़ाइल उपलब्ध नहीं है।") }
                        return@onSuccess
                    }
                    val amIFollowing = targetUserId in _uiState.value.followingUserIds
                    val isMe = targetUserId == currentUserId
                    val canSee = isMe || !profile.isPrivate || amIFollowing

                    val achievements = if (canSee) {
                        socialRepository.getUserAchievements(targetUserId, amIFollowing).getOrDefault(emptyList())
                    } else emptyList()

                    val activities = if (canSee) {
                        socialRepository.getUserActivities(
                            targetUserId = targetUserId,
                            targetIsPrivate = profile.isPrivate,
                            amIFollowing = amIFollowing
                        ).getOrDefault(emptyList())
                    } else emptyList()

                    _uiState.update {
                        it.copy(
                            viewedUserProfile = profile,
                            viewedUserAchievements = achievements,
                            viewedUserActivities = activities
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun closeUserSocialProfile() {
        _uiState.update {
            it.copy(
                viewedUserProfile = null,
                viewedUserAchievements = emptyList(),
                viewedUserActivities = emptyList()
            )
        }
    }

    fun openFollowersList(userId: String) {
        viewModelScope.launch {
            socialRepository.getFollowersForUser(userId)
                .onSuccess { relations ->
                    _uiState.update {
                        it.copy(
                            followListModalTitle = "फ़ॉलोअर्स (${relations.size})",
                            followListRelations = relations,
                            followListShowFollowers = true
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun openFollowingList(userId: String) {
        viewModelScope.launch {
            socialRepository.getFollowingForUser(userId)
                .onSuccess { relations ->
                    _uiState.update {
                        it.copy(
                            followListModalTitle = "फ़ॉलोइंग (${relations.size})",
                            followListRelations = relations,
                            followListShowFollowers = false
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun closeFollowList() {
        _uiState.update {
            it.copy(
                followListModalTitle = null,
                followListRelations = emptyList()
            )
        }
    }

    // --- Likes & Comments ---

    fun toggleLike(activity: ActivityFeedItem) {
        val me = _uiState.value.myProfile ?: return
        val wasLiked = activity.id in _uiState.value.likedActivityIds
        val delta = if (wasLiked) -1L else 1L

        // Optimistic UI update
        _uiState.update { state ->
            val newLikedIds = if (wasLiked) {
                state.likedActivityIds - activity.id
            } else {
                state.likedActivityIds + activity.id
            }
            state.copy(
                likedActivityIds = newLikedIds,
                friendsFeed = state.friendsFeed.map {
                    if (it.id == activity.id) it.copy(likesCount = (it.likesCount + delta).coerceAtLeast(0L)) else it
                },
                viewedUserActivities = state.viewedUserActivities.map {
                    if (it.id == activity.id) it.copy(likesCount = (it.likesCount + delta).coerceAtLeast(0L)) else it
                }
            )
        }

        viewModelScope.launch {
            socialRepository.toggleLike(me, activity)
                .onFailure { error ->
                    // Revert optimistic change on failure
                    _uiState.update { state ->
                        val revertedIds = if (wasLiked) {
                            state.likedActivityIds + activity.id
                        } else {
                            state.likedActivityIds - activity.id
                        }
                        state.copy(
                            likedActivityIds = revertedIds,
                            friendsFeed = state.friendsFeed.map {
                                if (it.id == activity.id) it.copy(likesCount = activity.likesCount) else it
                            },
                            viewedUserActivities = state.viewedUserActivities.map {
                                if (it.id == activity.id) it.copy(likesCount = activity.likesCount) else it
                            }
                        )
                    }
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun openComments(activity: ActivityFeedItem) {
        commentsJob?.cancel()
        _uiState.update {
            it.copy(
                activeCommentActivity = activity,
                activeComments = emptyList(),
                replyingToComment = null
            )
        }
        commentsJob = viewModelScope.launch {
            socialRepository.observeComments(activity.id)
                .catch { error ->
                    val firestoreError = error as? FirebaseFirestoreException
                    if (firestoreError == null && error is CancellationException) throw error
                    showError(firestoreError ?: error)
                }
                .collectLatest { comments ->
                    _uiState.update { it.copy(activeComments = comments) }
                }
        }
    }

    fun closeComments() {
        commentsJob?.cancel()
        _uiState.update {
            it.copy(
                activeCommentActivity = null,
                activeComments = emptyList(),
                replyingToComment = null
            )
        }
    }

    fun setReplyingToComment(comment: PostComment?) {
        _uiState.update { it.copy(replyingToComment = comment) }
    }

    fun sendComment(commentText: String) {
        val state = _uiState.value
        val me = state.myProfile ?: return
        val activity = state.activeCommentActivity ?: return
        val replyingTo = state.replyingToComment

        viewModelScope.launch {
            socialRepository.addComment(me, activity, commentText, replyingTo)
                .onSuccess {
                    _uiState.update { s ->
                        s.copy(
                            replyingToComment = null,
                            friendsFeed = s.friendsFeed.map {
                                if (it.id == activity.id) it.copy(commentsCount = it.commentsCount + 1L) else it
                            },
                            viewedUserActivities = s.viewedUserActivities.map {
                                if (it.id == activity.id) it.copy(commentsCount = it.commentsCount + 1L) else it
                            },
                            activeCommentActivity = s.activeCommentActivity?.let {
                                if (it.id == activity.id) it.copy(commentsCount = it.commentsCount + 1L) else it
                            }
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun deleteComment(comment: PostComment) {
        viewModelScope.launch {
            socialRepository.deleteComment(comment.activityId, comment.id)
                .onSuccess {
                    _uiState.update { s ->
                        s.copy(
                            friendsFeed = s.friendsFeed.map {
                                if (it.id == comment.activityId) {
                                    it.copy(commentsCount = (it.commentsCount - 1L).coerceAtLeast(0L))
                                } else it
                            },
                            viewedUserActivities = s.viewedUserActivities.map {
                                if (it.id == comment.activityId) {
                                    it.copy(commentsCount = (it.commentsCount - 1L).coerceAtLeast(0L))
                                } else it
                            }
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun hideActivity(activityId: String) {
        viewModelScope.launch {
            socialRepository.hideMyActivity(activityId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            friendsFeed = state.friendsFeed.filterNot { it.id == activityId },
                            viewedUserActivities = state.viewedUserActivities.filterNot { it.id == activityId },
                            snackbarMessage = "गतिविधि फ़ीड से छिपाई गई।"
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    // --- Notifications ---

    fun onClickNotification(notification: AppNotification) {
        viewModelScope.launch {
            if (!notification.isRead) {
                socialRepository.markNotificationRead(notification.id)
            }
            if (notification.activityId.isNotBlank()) {
                val matchingPost = _uiState.value.friendsFeed.firstOrNull { it.id == notification.activityId }
                if (matchingPost != null) {
                    openComments(matchingPost)
                } else {
                    openUserSocialProfile(notification.actorUserId)
                }
            } else if (notification.actorUserId.isNotBlank()) {
                openUserSocialProfile(notification.actorUserId)
            }
        }
    }

    fun markAllNotificationsRead() {
        val current = _uiState.value.notifications
        viewModelScope.launch {
            socialRepository.markAllNotificationsRead(current)
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    // --- Profile Edit, Privacy, Block & Report ---

    fun openEditProfileDialog(focus: EditProfileFocus = EditProfileFocus.ALL) {
        _uiState.update {
            it.copy(
                showEditProfileDialog = true,
                editProfileFocus = focus
            )
        }
    }

    fun closeEditProfileDialog() {
        _uiState.update {
            it.copy(
                showEditProfileDialog = false,
                editProfileFocus = EditProfileFocus.ALL,
                isSavingProfile = false
            )
        }
    }

    fun saveProfile(
        displayName: String,
        username: String,
        bio: String,
        avatarUrl: String,
        interests: List<String>
    ) {
        if (_uiState.value.isSavingProfile) return
        _uiState.update { it.copy(isSavingProfile = true) }
        viewModelScope.launch {
            socialRepository.updateProfile(displayName, username, bio, avatarUrl, interests)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showEditProfileDialog = false,
                            editProfileFocus = EditProfileFocus.ALL,
                            isSavingProfile = false,
                            snackbarMessage = "प्रोफ़ाइल सफलतापूर्वक सहेजी गई!"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSavingProfile = false) }
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun openPrivacySafetySheet() {
        _uiState.update { it.copy(showPrivacySafetySheet = true) }
    }

    fun closePrivacySafetySheet() {
        _uiState.update { it.copy(showPrivacySafetySheet = false) }
    }

    fun updatePrivacySettings(
        isPrivate: Boolean,
        shareAchievements: Boolean,
        notifyActivity: Boolean
    ) {
        viewModelScope.launch {
            socialRepository.updatePrivacySettings(isPrivate, shareAchievements, notifyActivity)
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun blockUser(target: UserProfile) {
        val me = _uiState.value.myProfile ?: return
        viewModelScope.launch {
            socialRepository.blockUser(me, target)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            viewedUserProfile = null,
                            snackbarMessage = "${target.displayName} को ब्लॉक किया गया।"
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun unblockUser(targetUserId: String) {
        viewModelScope.launch {
            socialRepository.unblockUser(targetUserId)
                .onSuccess {
                    _uiState.update { it.copy(snackbarMessage = "उपयोगकर्ता को अनब्लॉक किया गया।") }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    showError(error)
                }
        }
    }

    fun openReportDialog(target: ReportTarget) {
        _uiState.update { it.copy(reportTarget = target) }
    }

    fun closeReportDialog() {
        _uiState.update { it.copy(reportTarget = null) }
    }

    fun submitReport(reason: String) {
        val target = _uiState.value.reportTarget ?: return
        viewModelScope.launch {
            socialRepository.reportContent(
                reportedUserId = target.reportedUserId,
                contentType = target.contentType,
                contentId = target.contentId,
                reason = reason
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        reportTarget = null,
                        snackbarMessage = "आपकी रिपोर्ट दर्ज कर ली गई है। धन्यवाद!"
                    )
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error
                showError(error)
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun showError(error: Throwable) {
        val msg = when {
            error is FirebaseFirestoreException -> friendlyFirestoreMessageHi(error)
            !error.localizedMessage.isNullOrBlank() -> error.localizedMessage!!
            else -> "कुछ गड़बड़ हुई। कृपया पुनः प्रयास करें।"
        }
        _uiState.update { it.copy(snackbarMessage = msg) }
    }
}
