const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_1";
const BOB_UID = "bob_uid_2";
const CHARLIE_UID = "charlie_uid_3";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastTimestamp() {
  return new Date(Date.now() - 2000);
}

function makeUser(uid, profileId, username, isPrivate = false) {
  const ts = pastTimestamp();
  return {
    userId: uid,
    profileId,
    username,
    usernameLower: username.toLowerCase(),
    displayName: username,
    avatarUrl: "preset:sparrow",
    bio: "Learning English daily",
    totalXp: 100,
    streak: 3,
    lessonsCompleted: 2,
    wordsLearned: 15,
    achievementsCount: 0,
    followersCount: 0,
    followingCount: 0,
    isPrivate,
    shareAchievements: true,
    notifyActivity: true,
    interests: ["Daily Conversation"],
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or list users, activity_feed, or notifications", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("activity_feed").get());
  await assertFails(unauthDb.collection("notifications").get());
});

test("User can create own profile with immutable profileId, cannot spoof another user or change profileId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(makeUser(ALICE_UID, "EL10245", "alice_learns"))
  );
  // Cannot spoof Bob's user doc
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set(makeUser(BOB_UID, "EL99999", "bob_learns"))
  );
  // Cannot mutate profileId after creation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({ profileId: "EL00001", updatedAt: pastTimestamp() })
  );
  // Can update displayName and bio
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Alice Sharma",
      bio: "Speaking English confidently",
      updatedAt: pastTimestamp(),
    })
  );
});

test("Cannot follow self; atomic follow updates follower/following counts; unfollow decrements cleanly", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("users").doc(ALICE_UID).set(makeUser(ALICE_UID, "EL10001", "alice"));
    await db.collection("users").doc(BOB_UID).set(makeUser(BOB_UID, "EL10002", "bob"));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Self-follow forbidden
  await assertFails(
    aliceDb.collection("follows").doc(`${ALICE_UID}_${ALICE_UID}`).set({
      followerUserId: ALICE_UID,
      followedUserId: ALICE_UID,
      followerDisplayName: "alice",
      followerUsername: "alice",
      followerAvatarUrl: "preset:sparrow",
      followedDisplayName: "alice",
      followedUsername: "alice",
      followedAvatarUrl: "preset:sparrow",
      isPublicFollow: true,
      createdAt: pastTimestamp(),
    })
  );

  // Valid atomic follow batch
  const followBatch = aliceDb.batch();
  followBatch.set(aliceDb.collection("follows").doc(`${ALICE_UID}_${BOB_UID}`), {
    followerUserId: ALICE_UID,
    followedUserId: BOB_UID,
    followerDisplayName: "alice",
    followerUsername: "alice",
    followerAvatarUrl: "preset:sparrow",
    followedDisplayName: "bob",
    followedUsername: "bob",
    followedAvatarUrl: "preset:owl",
    isPublicFollow: true,
    createdAt: pastTimestamp(),
  });
  followBatch.update(aliceDb.collection("users").doc(ALICE_UID), {
    followingCount: 1,
    updatedAt: pastTimestamp(),
  });
  followBatch.update(aliceDb.collection("users").doc(BOB_UID), {
    followersCount: 1,
    updatedAt: pastTimestamp(),
  });
  await assertSucceeds(followBatch.commit());

  // Spoofing Bob's followersCount without creating a follow doc fails
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      followersCount: 99,
      updatedAt: pastTimestamp(),
    })
  );

  // Atomic unfollow batch
  const unfollowBatch = aliceDb.batch();
  unfollowBatch.delete(aliceDb.collection("follows").doc(`${ALICE_UID}_${BOB_UID}`));
  unfollowBatch.update(aliceDb.collection("users").doc(ALICE_UID), {
    followingCount: 0,
    updatedAt: pastTimestamp(),
  });
  unfollowBatch.update(aliceDb.collection("users").doc(BOB_UID), {
    followersCount: 0,
    updatedAt: pastTimestamp(),
  });
  await assertSucceeds(unfollowBatch.commit());
});

test("Likes are unique per user and comments reject empty text and protect ownership", async () => {
  const activityId = "act_bob_1";
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("users").doc(ALICE_UID).set(makeUser(ALICE_UID, "EL10001", "alice"));
    await db.collection("users").doc(BOB_UID).set(makeUser(BOB_UID, "EL10002", "bob"));
    await db.collection("activity_feed").doc(activityId).set({
      userId: BOB_UID,
      username: "bob",
      displayName: "bob",
      avatarUrl: "preset:owl",
      profileId: "EL10002",
      activityType: "ACHIEVEMENT",
      achievementId: `${BOB_UID}_FIRST_LESSON`,
      achievementTitle: "First Lesson Completed",
      description: "Bob completed his first English lesson!",
      descriptionHi: "बॉब ने अपना पहला अंग्रेज़ी पाठ पूरा किया!",
      visibility: "PUBLIC",
      likesCount: 0,
      commentsCount: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  // Atomic like
  const likeBatch = aliceDb.batch();
  likeBatch.set(aliceDb.collection("post_likes").doc(`${activityId}_${ALICE_UID}`), {
    activityId,
    userId: ALICE_UID,
    postOwnerId: BOB_UID,
    createdAt: pastTimestamp(),
  });
  likeBatch.update(aliceDb.collection("activity_feed").doc(activityId), {
    likesCount: 1,
    updatedAt: pastTimestamp(),
  });
  await assertSucceeds(likeBatch.commit());

  // Empty comment rejected
  await assertFails(
    aliceDb.collection("comments").doc("c_empty").set({
      activityId,
      postOwnerId: BOB_UID,
      userId: ALICE_UID,
      username: "alice",
      displayName: "alice",
      avatarUrl: "preset:sparrow",
      commentText: "   ",
      parentCommentId: "",
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  // Valid comment with emoji allowed
  await assertSucceeds(
    aliceDb.collection("comments").doc("c_valid").set({
      activityId,
      postOwnerId: BOB_UID,
      userId: ALICE_UID,
      username: "alice",
      displayName: "alice",
      avatarUrl: "preset:sparrow",
      commentText: "Great job! Keep learning 🔥",
      parentCommentId: "",
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  // Charlie cannot delete Alice's comment
  await assertFails(charlieDb.collection("comments").doc("c_valid").delete());
  // Alice can delete her own comment
  await assertSucceeds(aliceDb.collection("comments").doc("c_valid").delete());
});

test("Blocked users cannot interact and private profile activities are hidden from non-followers", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("users").doc(ALICE_UID).set(makeUser(ALICE_UID, "EL10001", "alice", false));
    await db.collection("users").doc(BOB_UID).set(makeUser(BOB_UID, "EL10002", "bob", true));
    await db.collection("users").doc(CHARLIE_UID).set(makeUser(CHARLIE_UID, "EL10003", "charlie", false));
    await db.collection("users").doc(BOB_UID).collection("follower_activities").doc("priv_1").set({
      userId: BOB_UID,
      username: "bob",
      displayName: "bob",
      avatarUrl: "preset:owl",
      profileId: "EL10002",
      activityType: "ACHIEVEMENT",
      achievementId: `${BOB_UID}_XP_100`,
      achievementTitle: "First 100 XP",
      description: "Bob earned 100 XP!",
      descriptionHi: "बॉब ने 100 XP अर्जित किए!",
      visibility: "FOLLOWERS",
      likesCount: 0,
      commentsCount: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    });
    await db.collection("follows").doc(`${ALICE_UID}_${BOB_UID}`).set({
      followerUserId: ALICE_UID,
      followedUserId: BOB_UID,
      followerDisplayName: "alice",
      followerUsername: "alice",
      followerAvatarUrl: "preset:sparrow",
      followedDisplayName: "bob",
      followedUsername: "bob",
      followedAvatarUrl: "preset:owl",
      isPublicFollow: false,
      createdAt: pastTimestamp(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  // Alice follows Bob -> can read private follower_activities
  await assertSucceeds(
    aliceDb.collection("users").doc(BOB_UID).collection("follower_activities").get()
  );
  // Charlie does NOT follow Bob -> denied!
  await assertFails(
    charlieDb.collection("users").doc(BOB_UID).collection("follower_activities").get()
  );

  // Bob blocks Charlie
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(
    bobDb.collection("blocked_users").doc(`${BOB_UID}_${CHARLIE_UID}`).set({
      blockerUserId: BOB_UID,
      blockedUserId: CHARLIE_UID,
      blockedUsername: "charlie",
      blockedDisplayName: "charlie",
      createdAt: pastTimestamp(),
    })
  );

  // Charlie cannot read Bob's profile or send notification to Bob
  await assertFails(charlieDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    charlieDb.collection("notifications").doc("notif_blocked").set({
      recipientUserId: BOB_UID,
      actorUserId: CHARLIE_UID,
      actorUsername: "charlie",
      actorDisplayName: "charlie",
      actorAvatarUrl: "preset:star",
      notificationType: "FOLLOW",
      message: "charlie started following you.",
      messageHi: "charlie ने आपको फ़ॉलो किया।",
      activityId: "",
      commentId: "",
      isRead: false,
      createdAt: pastTimestamp(),
    })
  );
});

test("Notifications are isolated to recipient only", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("notifications").doc("n_bob").set({
      recipientUserId: BOB_UID,
      actorUserId: ALICE_UID,
      actorUsername: "alice",
      actorDisplayName: "alice",
      actorAvatarUrl: "preset:sparrow",
      notificationType: "FOLLOW",
      message: "alice started following you.",
      messageHi: "alice ने आपको फ़ॉलो किया।",
      activityId: "",
      commentId: "",
      isRead: false,
      createdAt: pastTimestamp(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertFails(aliceDb.collection("notifications").where("recipientUserId", "==", BOB_UID).get());
  await assertSucceeds(bobDb.collection("notifications").where("recipientUserId", "==", BOB_UID).get());
});

test("Course progress is strictly owner-isolated and validates fields cleanly", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ts = pastTimestamp();

  const aliceDocRef = aliceDb.collection("users").doc(ALICE_UID).collection("course_progress").doc("s1_u1_n1");

  // Unauthenticated caller denied
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("course_progress").get()
  );

  // Alice creates valid course progress
  await assertSucceeds(
    aliceDocRef.set({
      nodeId: "s1_u1_n1",
      unitId: "s1_u1",
      status: "IN_PROGRESS",
      bestScorePercent: 50,
      lastScorePercent: 50,
      starsEarned: 0,
      attemptsCount: 1,
      xpAwarded: false,
      passingScorePercent: 70,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Alice updates valid progress to PERFECT
  await assertSucceeds(
    aliceDocRef.update({
      status: "PERFECT",
      bestScorePercent: 100,
      lastScorePercent: 100,
      starsEarned: 3,
      attemptsCount: 2,
      xpAwarded: true,
      updatedAt: pastTimestamp(),
    })
  );

  // Invalid status or out-of-range score rejected
  await assertFails(
    aliceDocRef.update({
      status: "HACKED",
      updatedAt: pastTimestamp(),
    })
  );
  await assertFails(
    aliceDocRef.update({
      bestScorePercent: 150,
      updatedAt: pastTimestamp(),
    })
  );

  // Bob cannot read, write, or delete Alice's course_progress
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("course_progress").doc("s1_u1_n1").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("course_progress").doc("s1_u1_n2").set({
      nodeId: "s1_u1_n2",
      unitId: "s1_u1",
      status: "AVAILABLE",
      bestScorePercent: 0,
      lastScorePercent: 0,
      starsEarned: 0,
      attemptsCount: 0,
      xpAwarded: false,
      passingScorePercent: 70,
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertFails(aliceDocRef.delete());
});

