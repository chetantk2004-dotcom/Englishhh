package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.ActivityFeedItem
import com.example.model.AppNotification
import com.example.model.FollowButtonState
import com.example.model.FriendsSubTab
import com.example.model.UserProfile
import com.example.ui.components.ActivityFeedCard
import com.example.ui.components.FollowActionButton
import com.example.ui.components.UserAvatar
import com.example.ui.components.formatRelativeTimeHi
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    uiState: MainUiState,
    onSelectSubTab: (FriendsSubTab) -> Unit,
    onRefreshFeed: () -> Unit,
    onLoadMoreFeed: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFollow: (UserProfile) -> Unit,
    onOpenUserProfile: (String) -> Unit,
    onToggleLike: (ActivityFeedItem) -> Unit,
    onOpenComments: (ActivityFeedItem) -> Unit,
    onHideActivity: (String) -> Unit,
    onReportActivity: (ActivityFeedItem) -> Unit,
    onClickNotification: (AppNotification) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = FriendsSubTab.entries.indexOf(uiState.friendsSubTab).coerceAtLeast(0)
    val unreadCount = uiState.notifications.count { !it.isRead }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                FriendsSubTab.entries.forEachIndexed { index, subTab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onSelectSubTab(subTab) },
                        text = {
                            if (subTab == FriendsSubTab.NOTIFICATIONS && unreadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge { Text("$unreadCount") }
                                    }
                                ) {
                                    Text(
                                        text = subTab.labelHi,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            } else {
                                Text(
                                    text = subTab.labelHi,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        },
                        modifier = Modifier.testTag("friends_subtab_${subTab.name.lowercase()}")
                    )
                }
            }

            when (uiState.friendsSubTab) {
                FriendsSubTab.FEED -> FriendsFeedContent(
                    uiState = uiState,
                    onRefreshFeed = onRefreshFeed,
                    onLoadMoreFeed = onLoadMoreFeed,
                    onFindFriendsClick = { onSelectSubTab(FriendsSubTab.FIND_FRIENDS) },
                    onOpenUserProfile = onOpenUserProfile,
                    onToggleLike = onToggleLike,
                    onOpenComments = onOpenComments,
                    onHideActivity = onHideActivity,
                    onReportActivity = onReportActivity
                )

                FriendsSubTab.FIND_FRIENDS -> FindFriendsContent(
                    uiState = uiState,
                    onSearchQueryChange = onSearchQueryChange,
                    onToggleFollow = onToggleFollow,
                    onOpenUserProfile = onOpenUserProfile
                )

                FriendsSubTab.NOTIFICATIONS -> NotificationsContent(
                    notifications = uiState.notifications,
                    onClickNotification = onClickNotification,
                    onMarkAllRead = onMarkAllNotificationsRead
                )
            }
        }
    }
}

@Composable
private fun FriendsFeedContent(
    uiState: MainUiState,
    onRefreshFeed: () -> Unit,
    onLoadMoreFeed: () -> Unit,
    onFindFriendsClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    onToggleLike: (ActivityFeedItem) -> Unit,
    onOpenComments: (ActivityFeedItem) -> Unit,
    onHideActivity: (String) -> Unit,
    onReportActivity: (ActivityFeedItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "मित्रों की फ़ीड (Friends Feed)",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "आपके और आपके मित्रों की सीखने की उपलब्धियाँ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onRefreshFeed,
                    modifier = Modifier.testTag("refresh_feed_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "रिफ़्रेश करें")
                }
            }
        }

        if (uiState.isFeedLoading && uiState.friendsFeed.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.friendsFeed.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GroupAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "फ़ीड अभी खाली है!",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "कोई पाठ पूरा करें या 'मित्र खोजें' में जाकर अन्य सीखने वालों को फ़ॉलो करें।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onFindFriendsClick,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("मित्र खोजें (Find Friends)")
                        }
                    }
                }
            }
        } else {
            items(uiState.friendsFeed, key = { it.id }) { item ->
                ActivityFeedCard(
                    item = item,
                    isLiked = item.id in uiState.likedActivityIds,
                    isMyPost = item.userId == uiState.myProfile?.userId,
                    onOpenProfile = onOpenUserProfile,
                    onToggleLike = { onToggleLike(item) },
                    onOpenComments = { onOpenComments(item) },
                    onHideActivity = { onHideActivity(item.id) },
                    onReportActivity = { onReportActivity(item) }
                )
            }
            if (uiState.friendsFeed.size >= 15) {
                item {
                    OutlinedButton(
                        onClick = onLoadMoreFeed,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("और गतिविधि लोड करें (Load More)")
                    }
                }
            }
        }
    }
}

@Composable
private fun FindFriendsContent(
    uiState: MainUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleFollow: (UserProfile) -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val usersToShow = if (uiState.friendSearchQuery.isBlank()) {
        uiState.suggestedUsers
    } else {
        uiState.searchResults
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = uiState.friendSearchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("@username या Profile ID (EL...) से खोजें") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("find_friends_search_field")
            )
        }

        item {
            Text(
                text = if (uiState.friendSearchQuery.isBlank()) {
                    "लोकप्रिय व समान रुचि वाले शिक्षार्थी (Suggested Learners)"
                } else {
                    "खोज परिणाम (${usersToShow.size})"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (uiState.isSearchingFriends) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (usersToShow.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PersonSearch,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "कोई उपयोगकर्ता नहीं मिला",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "सही @username या EL से शुरू होने वाला Profile ID लिखकर खोजें।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(usersToShow, key = { it.userId }) { user ->
                val isFollowing = user.userId in uiState.followingUserIds
                val isFollower = user.userId in uiState.followerUserIds
                val followState = when {
                    isFollowing -> FollowButtonState.FOLLOWING
                    isFollower -> FollowButtonState.FOLLOW_BACK
                    else -> FollowButtonState.FOLLOW
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenUserProfile(user.userId) }
                        .testTag("friend_card_${user.userId}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            avatarUrl = user.avatarUrl,
                            displayName = user.displayName,
                            size = 48.dp
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "@${user.username} • ${user.profileId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${user.totalXp} XP • ${user.streak} दिन स्ट्रीक • ${user.followersCount} फ़ॉलोअर्स",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FollowActionButton(
                            state = followState,
                            onClick = { onToggleFollow(user) },
                            modifier = Modifier.testTag("follow_btn_${user.userId}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsContent(
    notifications: List<AppNotification>,
    onClickNotification: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "सूचनाएँ (Notifications)",
                    style = MaterialTheme.typography.titleLarge
                )
                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = onMarkAllRead) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("सभी पढ़ा मानें")
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsNone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text("कोई नई सूचना नहीं है", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "जब कोई आपको फ़ॉलो करेगा, लाइक या टिप्पणी करेगा, तो यहाँ तुरंत दिखेगा।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(notifications, key = { it.id }) { notif ->
                Card(
                    onClick = { onClickNotification(notif) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!notif.isRead) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            avatarUrl = notif.actorAvatarUrl,
                            displayName = notif.actorDisplayName,
                            size = 42.dp
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = notif.messageHi,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (!notif.isRead) FontWeight.SemiBold else FontWeight.Normal
                            )
                            Text(
                                text = "${notif.message} • ${formatRelativeTimeHi(notif.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!notif.isRead) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(10.dp)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
