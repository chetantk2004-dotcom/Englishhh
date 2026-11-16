package com.example.auth

import android.app.Activity
import android.content.ContextWrapper
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.firebase.FirebaseNetworkException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GoogleAuthManagerTest {

    @Test
    fun `isConfigOrPlayServicesCancellation detects developer console and sha errors`() {
        assertTrue(isConfigOrPlayServicesCancellation("[28444] Developer console is not set up correctly."))
        assertTrue(isConfigOrPlayServicesCancellation("Caller not authorized for SHA-1"))
        assertFalse(isConfigOrPlayServicesCancellation(null))
        assertFalse(isConfigOrPlayServicesCancellation("User closed sheet"))
    }

    @Test
    fun `mapAuthExceptionToUserMessage includes sha1 for credential config errors`() {
        val sha1 = "2A:9C:05:72:CC:72:81:5C:AC:48:C2:D2:E3:40:48:7E:63:A5:53:8E"
        val pkg = "com.aistudio.englishseekhen.kxmpqz"

        val cancelConfigMsg = mapAuthExceptionToUserMessage(
            GetCredentialCancellationException("[28444] Developer console is not set up correctly"),
            pkg,
            sha1
        )
        assertTrue(cancelConfigMsg.contains(sha1))
        assertTrue(cancelConfigMsg.contains(pkg))

        val noCredMsg = mapAuthExceptionToUserMessage(
            NoCredentialException("No credentials available"),
            pkg,
            sha1
        )
        assertTrue(noCredMsg.contains(sha1))

        val netMsg = mapAuthExceptionToUserMessage(
            FirebaseNetworkException("Network error"),
            pkg,
            sha1
        )
        assertTrue(netMsg.contains("इंटरनेट"))
    }

    @Test
    fun `findActivity unwraps ContextWrapper and returns null for application context`() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val wrapped = ContextWrapper(ContextWrapper(activity))
        assertEquals(activity, findActivity(wrapped))
        assertNull(findActivity(RuntimeEnvironment.getApplication()))
    }
}
