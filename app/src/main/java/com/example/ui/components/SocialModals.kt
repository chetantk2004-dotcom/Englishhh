package com.example.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ActivityFeedItem
import com.example.model.AvatarPresets
import com.example.model.BlockedUser
import com.example.model.FollowButtonState
import com.example.model.FollowRelation
import com.example.model.UserAchievement
import com.example.model.UserProfile
import com.example.ui.theme.GentleErrorRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.viewmodel.EditProfileFocus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSocialProfileSheet(
    profile: UserProfile,
    isMe: Boolean,
    followState: FollowButtonState,
    achievements: List<UserAchievement>,
    activities: List<ActivityFeedItem>,
    likedIds: Set<String>,
    onToggleFollow: () -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
    onBlockUser: () -> Unit,
    onReportUser: () -> Unit,
    onToggleLike: (ActivityFeedItem) -> Unit,
    onOpenComments: (ActivityFeedItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val amIFollowing = followState == FollowButtonState.FOLLOWING
    val canViewPrivateContent = isMe || !profile.isPrivate || amIFollowing

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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "ID: ${profile.profileId}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
                Row {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "English सीखें पर मुझसे जुड़ें! नाम: ${profile.displayName}, यूज़रनेम: @${profile.username}, Profile ID: ${profile.profileId}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "प्रोफ़ाइल साझा करें"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "साझा करें")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "बंद करें")
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    avatarUrl = profile.avatarUrl,
                    displayName = profile.displayName,
                    size = 72.dp
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    if (profile.bio.isNotBlank()) {
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!isMe) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FollowActionButton(
                        state = followState,
                        onClick = onToggleFollow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_sheet_follow_btn")
                    )
                    OutlinedButton(
                        onClick = onReportUser,
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("रिपोर्ट")
                    }
                    OutlinedButton(
                        onClick = onBlockUser,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, GentleErrorRed.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.Filled.Block,
                            contentDescription = null,
                            tint = GentleErrorRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ब्लॉक", color = GentleErrorRed)
                    }
                }
            }

            // Stats Row (Followers, Following, XP, Streak)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(
                        value = "${profile.followersCount}",
                        label = "फ़ॉलोअर्स",
                        onClick = if (canViewPrivateContent) onOpenFollowers else null
                    )
                    ProfileStatItem(
                        value = "${profile.followingCount}",
                        label = "फ़ॉलोइंग",
                        onClick = if (canViewPrivateContent) onOpenFollowing else null
                    )
                    ProfileStatItem(value = "${profile.totalXp}", label = "XP")
                    ProfileStatItem(value = "${profile.streak} दिन", label = "स्ट्रीक 🔥")
                }
            }

            if (!canViewPrivateContent) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "यह प्रोफ़ाइल प्राइवेट है (Private Profile)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "इनकी उपलब्धियाँ और सीखने की गतिविधि देखने के लिए इन्हें फ़ॉलो करें।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "उपलब्धियाँ (${achievements.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                if (achievements.isEmpty()) {
                    Text(
                        text = "अभी कोई बैज अनलॉक नहीं हुआ है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        achievements.take(6).forEach { ach ->
                            Surface(
                                color = SaffronSecondary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        tint = SaffronSecondary
                                    )
                                    Column {
                                        Text(
                                            text = "${ach.achievementTitleHi} (${ach.achievementTitle})",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            text = ach.descriptionHi,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "सीखने की गतिविधि (${activities.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                if (activities.isEmpty()) {
                    Text(
                        text = "अभी कोई सार्वजनिक गतिविधि नहीं है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    activities.take(5).forEach { act ->
                        ActivityFeedCard(
                            item = act,
                            isLiked = act.id in likedIds,
                            isMyPost = isMe,
                            onOpenProfile = {},
                            onToggleLike = { onToggleLike(act) },
                            onOpenComments = { onOpenComments(act) },
                            onHideActivity = {},
                            onReportActivity = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    value: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) {
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        } else {
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        }
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListSheet(
    title: String,
    relations: List<FollowRelation>,
    showFollowers: Boolean,
    onOpenUser: (String) -> Unit,
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "बंद करें")
                }
            }

            if (relations.isEmpty()) {
                Text(
                    text = if (showFollowers) "अभी कोई फ़ॉलोअर नहीं है।" else "अभी किसी को फ़ॉलो नहीं किया है।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                relations.forEach { rel ->
                    val uid = if (showFollowers) rel.followerUserId else rel.followedUserId
                    val name = if (showFollowers) rel.followerDisplayName else rel.followedDisplayName
                    val uname = if (showFollowers) rel.followerUsername else rel.followedUsername
                    val avatar = if (showFollowers) rel.followerAvatarUrl else rel.followedAvatarUrl

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenUser(uid) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(avatarUrl = avatar, displayName = name, size = 44.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = name, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "@$uname",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
    profile: UserProfile,
    initialFocus: EditProfileFocus = EditProfileFocus.ALL,
    isSaving: Boolean = false,
    onSave: (displayName: String, username: String, bio: String, avatarUrl: String, interests: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var displayName by remember(profile.userId) { mutableStateOf(profile.displayName) }
    var username by remember(profile.userId) { mutableStateOf(profile.username) }
    var bio by remember(profile.userId) { mutableStateOf(profile.bio) }
    var avatarUrl by remember(profile.userId) { mutableStateOf(profile.avatarUrl) }
    var selectedInterests by remember(profile.userId) { mutableStateOf(profile.interests) }

    val displayNameFocus = remember { FocusRequester() }
    val usernameFocus = remember { FocusRequester() }
    val bioFocus = remember { FocusRequester() }

    LaunchedEffect(initialFocus) {
        when (initialFocus) {
            EditProfileFocus.DISPLAY_NAME -> runCatching { displayNameFocus.requestFocus() }
            EditProfileFocus.USERNAME -> runCatching { usernameFocus.requestFocus() }
            EditProfileFocus.BIO -> runCatching { bioFocus.requestFocus() }
            EditProfileFocus.AVATAR, EditProfileFocus.ALL -> Unit
        }
    }

    val dialogTitle = when (initialFocus) {
        EditProfileFocus.AVATAR -> "अवतार व फ़ोटो बदलें (Edit Avatar)"
        EditProfileFocus.DISPLAY_NAME -> "नाम संपादित करें (Edit Name)"
        EditProfileFocus.USERNAME -> "यूज़रनेम बदलें (Edit Username)"
        EditProfileFocus.BIO -> "परिचय संपादित करें (Edit Bio)"
        EditProfileFocus.ALL -> "प्रोफ़ाइल संपादित करें (Edit Profile)"
    }

    val availableInterests = listOf(
        "Daily Conversation",
        "Basic Grammar",
        "Job Interviews",
        "Vocabulary",
        "Speaking Confidence"
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            encodeUriToCompactAvatarDataUri(context.contentResolver, uri)?.let { encoded ->
                avatarUrl = encoded
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(dialogTitle) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (initialFocus == EditProfileFocus.AVATAR) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    },
                    border = if (initialFocus == EditProfileFocus.AVATAR) {
                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    } else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UserAvatar(avatarUrl = avatarUrl, displayName = displayName, size = 56.dp)
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = !isSaving,
                                modifier = Modifier.testTag("edit_dialog_photo_picker_button")
                            ) {
                                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("फ़ोटो चुनें")
                            }
                        }

                        Text("या अवतार चुनें:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AvatarPresets.allPresets.forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    border = if (avatarUrl == preset) {
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    } else null,
                                    modifier = Modifier.clickable(enabled = !isSaving) { avatarUrl = preset }
                                ) {
                                    UserAvatar(avatarUrl = preset, displayName = preset, size = 38.dp)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { if (it.length <= 40) displayName = it },
                    enabled = !isSaving,
                    label = { Text("नाम (Display Name)") },
                    supportingText = { Text("${displayName.length}/40 अक्षर") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(displayNameFocus)
                        .testTag("edit_dialog_display_name_input")
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        val cleaned = it.replace("@", "").filter { ch -> ch.isLetterOrDigit() || ch == '_' }
                        if (cleaned.length <= 24) username = cleaned
                    },
                    enabled = !isSaving,
                    label = { Text("यूज़रनेम (@ के बिना)") },
                    prefix = { Text("@") },
                    isError = username.length < 3,
                    supportingText = {
                        if (username.length < 3) {
                            Text("कम से कम 3 अक्षर (a-z, 0-9, _) आवश्यक हैं")
                        } else {
                            Text("Profile ID (${profile.profileId}) हमेशा एक जैसा रहेगा")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(usernameFocus)
                        .testTag("edit_dialog_username_input")
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 160) bio = it },
                    enabled = !isSaving,
                    label = { Text("परिचय (Bio)") },
                    supportingText = { Text("${bio.length}/160 अक्षर") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(bioFocus)
                        .testTag("edit_dialog_bio_input")
                )
                Text("सीखने की रुचियाँ:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableInterests.forEach { interest ->
                        val active = interest in selectedInterests
                        FilterChip(
                            selected = active,
                            enabled = !isSaving,
                            onClick = {
                                selectedInterests = if (active) {
                                    selectedInterests - interest
                                } else if (selectedInterests.size < 5) {
                                    selectedInterests + interest
                                } else selectedInterests
                            },
                            label = { Text(interest, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(displayName, username, bio, avatarUrl, selectedInterests) },
                enabled = !isSaving && displayName.isNotBlank() && username.length >= 3,
                modifier = Modifier.testTag("edit_dialog_save_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("सहेज रहे हैं...")
                } else {
                    Text("सहेजें (Save)")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("रद्द करें")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySafetySheet(
    profile: UserProfile,
    blockedUsers: List<BlockedUser>,
    onUpdatePrivacy: (isPrivate: Boolean, shareAchievements: Boolean, notifyActivity: Boolean) -> Unit,
    onUnblockUser: (String) -> Unit,
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("गोपनीयता व सुरक्षा (Privacy & Safety)", style = MaterialTheme.typography.titleLarge)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "बंद करें")
                }
            }

            PrivacySwitchRow(
                titleHi = "प्राइवेट प्रोफ़ाइल (Private Profile)",
                descHi = "केवल आपके फ़ॉलोअर्स ही आपकी गतिविधियाँ और बैज देख सकेंगे",
                checked = profile.isPrivate,
                onCheckedChange = {
                    onUpdatePrivacy(it, profile.shareAchievements, profile.notifyActivity)
                }
            )
            PrivacySwitchRow(
                titleHi = "उपलब्धियाँ साझा करें (Achievement Sharing)",
                descHi = "नया बैज मिलने पर मित्रों की फ़ीड में स्वतः पोस्ट बनाएँ",
                checked = profile.shareAchievements,
                onCheckedChange = {
                    onUpdatePrivacy(profile.isPrivate, it, profile.notifyActivity)
                }
            )
            PrivacySwitchRow(
                titleHi = "मित्रों की गतिविधि सूचनाएँ",
                descHi = "मित्र नया बैज जीतें तो ऐप में सूचना पाएँ",
                checked = profile.notifyActivity,
                onCheckedChange = {
                    onUpdatePrivacy(profile.isPrivate, profile.shareAchievements, it)
                }
            )

            HorizontalDivider()
            Text(
                text = "ब्लॉक किए गए उपयोगकर्ता (${blockedUsers.size})",
                style = MaterialTheme.typography.titleMedium
            )
            if (blockedUsers.isEmpty()) {
                Text(
                    text = "आपने किसी को ब्लॉक नहीं किया है।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                blockedUsers.forEach { blocked ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(blocked.blockedDisplayName, style = MaterialTheme.typography.titleSmall)
                            Text("@${blocked.blockedUsername}", style = MaterialTheme.typography.bodySmall)
                        }
                        OutlinedButton(onClick = { onUnblockUser(blocked.blockedUserId) }) {
                            Text("अनब्लॉक करें")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacySwitchRow(
    titleHi: String,
    descHi: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(titleHi, style = MaterialTheme.typography.titleSmall)
            Text(descHi, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ReportContentDialog(
    targetLabel: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val presets = listOf(
        "अनुचित या अभद्र भाषा (Inappropriate language)",
        "स्पैम या प्रचार (Spam)",
        "परेशान करना (Harassment)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("रिपोर्ट करें: $targetLabel") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("कृपया रिपोर्ट का कारण चुनें या लिखें:", style = MaterialTheme.typography.bodySmall)
                presets.forEach { preset ->
                    OutlinedButton(
                        onClick = { reason = preset },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(
                            1.dp,
                            if (reason == preset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(preset, style = MaterialTheme.typography.bodySmall)
                    }
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { if (it.length <= 300) reason = it },
                    label = { Text("अन्य कारण लिखें") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(reason.trim()) },
                enabled = reason.trim().length >= 3,
                colors = ButtonDefaults.buttonColors(containerColor = GentleErrorRed)
            ) {
                Text("रिपोर्ट भेजें")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
