package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.StudioProject
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class JarvisRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveUserProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser("user_${UUID.randomUUID()}@example.com")
        val repository = JarvisRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            assistantName = "JARVIS",
            ownerName = "Tony Stark"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(profile)
        }
        if (result.isFailure) {
            println("saveUserProfile failure: " + result.exceptionOrNull()?.message)
            result.exceptionOrNull()?.printStackTrace()
        }
        assertTrue("Expected success, but got failure: ${result.exceptionOrNull()?.message}", result.isSuccess)
    }

    @Test
    fun createProject_authenticatedUser_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = JarvisRepository(firestore)

        val dynamicId = UUID.randomUUID().toString()
        val project = StudioProject(
            id = dynamicId,
            userId = uid,
            title = "Neural Link Protocol",
            projectType = "coding"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProject(uid, project)
        }
        assertTrue(result.isSuccess)
    }

    @Test
    fun observeProjects_authenticatedUser_emitsProjects() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = JarvisRepository(firestore)

        val dynamicId = UUID.randomUUID().toString()
        val project = StudioProject(
            id = dynamicId,
            userId = uid,
            title = "Arc Reactor Schematics",
            projectType = "coding"
        )
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProject(uid, project)
        }

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeProjects(uid).first { list -> list.any { it.id == dynamicId } }
        }
        assertTrue(emitted.any { it.id == dynamicId })
    }

    @Test
    fun observeProjects_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()

        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                withTimeout(FLOW_TIMEOUT_MS) {
                    val rawFlow = firestore.collection("users/unauth_uid/projects").snapshots()
                    rawFlow.first()
                }
            }
        }
        val firestoreException = checkNotNull(exception.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $exception"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    companion object {
        const val ALICE_EMAIL = "alice_test@example.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
