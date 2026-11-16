package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest

private const val AUTH_TAG = "Auth"

internal fun findActivity(context: Context): Activity? {
    var current: Context? = context
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return current as? Activity
}

@Suppress("DEPRECATION")
internal fun getSigningCertSha1(context: Context): String {
    return try {
        val pm = context.packageManager
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            info.signingInfo?.apkContentsSigners
        } else {
            val info = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            info.signatures
        }
        val certBytes = signatures?.firstOrNull()?.toByteArray() ?: return "unavailable"
        val digest = MessageDigest.getInstance("SHA-1").digest(certBytes)
        digest.joinToString(":") { "%02X".format(it) }
    } catch (e: Exception) {
        Log.w(AUTH_TAG, "Unable to read APK signing SHA-1: ${e.message}")
        "unavailable"
    }
}

internal fun isConfigOrPlayServicesCancellation(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val lower = message.lowercase()
    return listOf(
        "28444",
        "developer",
        "not set up",
        "sha",
        "oauth",
        "caller",
        "blocked",
        "provider",
        "failed",
        "error",
        "[10]",
        "[16]"
    ).any { it in lower }
}

internal fun mapAuthExceptionToUserMessage(
    error: Exception,
    packageName: String,
    sha1: String
): String {
    return when (error) {
        is NoCredentialException -> {
            "इस फ़ोन पर कोई Google खाता उपलब्ध नहीं मिला, या Google Sign-In/SHA-1 कॉन्फ़िगरेशन मेल नहीं खा रहा है। (NoCredential | SHA-1: $sha1)"
        }
        is GetCredentialProviderConfigurationException -> {
            "Google Play Services Credential Provider उपलब्ध नहीं है। कृपया Google Play Services अपडेट करें।"
        }
        is GetCredentialException -> {
            val msg = error.errorMessage?.toString().orEmpty()
            if (isConfigOrPlayServicesCancellation(msg)) {
                "Google OAuth / SHA-1 कॉन्फ़िगरेशन मेल नहीं खा रहा है। Firebase Console में यह SHA-1 जोड़ें: $sha1 (Package: $packageName)"
            } else {
                "Google Sign-In असफल रहा (${error.type}): ${msg.ifBlank { "कृपया पुनः प्रयास करें।" }}"
            }
        }
        is FirebaseAuthException -> {
            when (error.errorCode) {
                "ERROR_OPERATION_NOT_ALLOWED" ->
                    "Firebase Authentication में Google Sign-In प्रदाता (Provider) बंद है। कृपया Firebase Console में Google Sign-In चालू करें।"
                "ERROR_INVALID_CREDENTIAL" ->
                    "Google ID Token अमान्य है (Web Client ID जांचें)। (${error.errorCode})"
                "ERROR_USER_DISABLED" ->
                    "यह उपयोगकर्ता खाता निष्क्रिय कर दिया गया है।"
                else ->
                    "Firebase लॉगिन त्रुटि (${error.errorCode}): ${error.localizedMessage ?: "पुनः प्रयास करें।"}"
            }
        }
        is FirebaseNetworkException -> {
            "इंटरनेट या Firebase सर्वर से कनेक्शन नहीं हो पा रहा है। कृपया नेटवर्क जांचकर पुनः प्रयास करें।"
        }
        else -> {
            error.localizedMessage ?: "Google Sign-In सफल नहीं हो सका।"
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        Log.i(AUTH_TAG, "Existing Firebase session restored: uid=${Firebase.auth.currentUser?.uid}")
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        Log.e(AUTH_TAG, "default_web_client_id missing during auto sign-in", e)
        onUnauthenticated()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val activityContext = findActivity(context) ?: context
            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                Log.i(AUTH_TAG, "Auto sign-in succeeded: uid=${authResult.user?.uid}")
                onAuthSuccess()
            } else {
                Log.w(AUTH_TAG, "Auto sign-in returned non-Google credential: ${credential.type}")
                onUnauthenticated()
            }
        } catch (e: Exception) {
            Log.d(AUTH_TAG, "Silent auto sign-in skipped: ${e::class.java.simpleName}: ${e.message}")
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        Log.e(AUTH_TAG, "default_web_client_id resource missing in APK", e)
        onAuthError("Google Sign-In कॉन्फ़िगरेशन (default_web_client_id) उपलब्ध नहीं है।")
        return
    }

    val sha1 = getSigningCertSha1(context)
    Log.i(
        AUTH_TAG,
        "Starting Google Sign-In | pkg=${context.packageName} | sha1=$sha1 | webClientPrefix=${clientId.take(18)}..."
    )

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    scope.launch {
        try {
            val activityContext = findActivity(context)
            if (activityContext == null) {
                Log.e(AUTH_TAG, "Activity context unavailable for CredentialManager.getCredential")
                onAuthError("लॉगिन स्क्रीन तैयार नहीं है। कृपया ऐप को फिर से खोलें।")
                return@launch
            }
            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Log.d(AUTH_TAG, "Received Google ID Token (length=${googleIdToken.length}), signing in to Firebase...")
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val signedInUser = authResult.user ?: Firebase.auth.currentUser
                if (signedInUser != null) {
                    Log.i(
                        AUTH_TAG,
                        "Firebase signInWithCredential succeeded: uid=${signedInUser.uid}, isNewUser=${authResult.additionalUserInfo?.isNewUser}"
                    )
                    onAuthSuccess()
                } else {
                    Log.e(AUTH_TAG, "signInWithCredential completed but Firebase.auth.currentUser is null")
                    onAuthError("Firebase सत्र सक्रिय नहीं हो सका। कृपया पुनः प्रयास करें।")
                }
            } else {
                Log.e(AUTH_TAG, "Unexpected credential type returned: ${credential.type}")
                onAuthError("अमान्य लॉगिन क्रेडेंशियल प्राप्त हुआ (${credential.type})।")
            }
        } catch (e: GetCredentialCancellationException) {
            val rawMessage = e.errorMessage?.toString() ?: e.message
            Log.w(
                AUTH_TAG,
                "Google Sign-In cancelled/dismissed | type=${e.type} | msg=$rawMessage | pkg=${context.packageName} | sha1=$sha1",
                e
            )
            if (isConfigOrPlayServicesCancellation(rawMessage)) {
                onAuthError(mapAuthExceptionToUserMessage(e, context.packageName, sha1))
            } else {
                onAuthCancelled()
            }
        } catch (e: FirebaseAuthException) {
            Log.e(
                AUTH_TAG,
                "FirebaseAuth signInWithCredential failed | code=${e.errorCode} | msg=${e.message}",
                e
            )
            onAuthError(mapAuthExceptionToUserMessage(e, context.packageName, sha1))
        } catch (e: GetCredentialException) {
            Log.e(
                AUTH_TAG,
                "CredentialManager failed | class=${e::class.java.simpleName} | type=${e.type} | msg=${e.errorMessage} | pkg=${context.packageName} | sha1=$sha1",
                e
            )
            onAuthError(mapAuthExceptionToUserMessage(e, context.packageName, sha1))
        } catch (e: Exception) {
            Log.e(
                AUTH_TAG,
                "Unexpected Google Sign-In failure | class=${e::class.java.simpleName} | msg=${e.message} | pkg=${context.packageName} | sha1=$sha1",
                e
            )
            onAuthError(mapAuthExceptionToUserMessage(e, context.packageName, sha1))
        }
    }
}

fun signOutFromApp(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(AUTH_TAG, "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                Log.i(AUTH_TAG, "User signed out cleanly from Firebase Auth")
                onSignOutComplete()
            }
        }
    }
}
