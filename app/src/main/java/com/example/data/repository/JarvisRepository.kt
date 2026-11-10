package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CharacterProfile
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.MemoryItem
import com.example.data.model.StudioProject
import com.example.data.model.UserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class JarvisRepository(
    val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    // User Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.document(path).snapshots().map { snapshot ->
            if (snapshot.exists()) {
                snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else {
                null
            }
        }.catch { e ->
            handleFirestoreError(e, OperationType.GET, path)
            if (e is CancellationException) throw e
            emit(null)
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val path = "users/${profile.userId}"
        return try {
            val doc = db.document(path).get().await()
            val existingCreatedAt = doc.getTimestamp("createdAt")
            val toSave = if (existingCreatedAt != null && profile.createdAt == null) {
                profile.copy(createdAt = existingCreatedAt).toMap()
            } else {
                profile.toMap()
            }
            db.document(path).set(toSave).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // Conversations
    fun observeConversations(userId: String): Flow<List<Conversation>> {
        val path = "users/$userId/conversations"
        return db.collection(path)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Conversation::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                handleFirestoreError(e, OperationType.LIST, path)
                if (e is CancellationException) throw e
                emit(emptyList())
            }
    }

    suspend fun createConversation(userId: String, title: String, language: String): Result<String> {
        val path = "users/$userId/conversations"
        return try {
            val docRef = db.collection(path).document()
            val conv = Conversation(
                id = docRef.id,
                userId = userId,
                title = title,
                language = language,
                lastMessage = ""
            )
            docRef.set(conv.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteConversation(userId: String, conversationId: String): Result<Unit> {
        val path = "users/$userId/conversations/$conversationId"
        return try {
            db.document(path).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // Messages
    fun observeMessages(userId: String, conversationId: String): Flow<List<ChatMessage>> {
        val path = "users/$userId/conversations/$conversationId/messages"
        return db.collection(path)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(ChatMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                handleFirestoreError(e, OperationType.LIST, path)
                if (e is CancellationException) throw e
                emit(emptyList())
            }
    }

    suspend fun addMessage(userId: String, conversationId: String, message: ChatMessage): Result<String> {
        val path = "users/$userId/conversations/$conversationId/messages"
        return try {
            val docRef = db.collection(path).document()
            val msgToSave = message.copy(id = docRef.id, userId = userId)
            docRef.set(msgToSave.toMap()).await()

            // Update parent conversation's lastMessage
            val convPath = "users/$userId/conversations/$conversationId"
            db.document(convPath).update(
                mapOf(
                    "lastMessage" to message.text.take(150),
                    "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // Studio Projects
    fun observeProjects(userId: String): Flow<List<StudioProject>> {
        val path = "users/$userId/projects"
        return db.collection(path)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(StudioProject::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                handleFirestoreError(e, OperationType.LIST, path)
                if (e is CancellationException) throw e
                emit(emptyList())
            }
    }

    suspend fun saveProject(userId: String, project: StudioProject): Result<String> {
        val path = "users/$userId/projects"
        return try {
            val docRef = if (project.id.isNotBlank()) {
                db.collection(path).document(project.id)
            } else {
                db.collection(path).document()
            }
            val projToSave = project.copy(id = docRef.id, userId = userId)
            docRef.set(projToSave.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteProject(userId: String, projectId: String): Result<Unit> {
        val path = "users/$userId/projects/$projectId"
        return try {
            db.document(path).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // Memory items
    fun observeMemories(userId: String): Flow<List<MemoryItem>> {
        val path = "users/$userId/memories"
        return db.collection(path)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(MemoryItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                handleFirestoreError(e, OperationType.LIST, path)
                if (e is CancellationException) throw e
                emit(emptyList())
            }
    }

    suspend fun saveMemory(userId: String, memory: MemoryItem): Result<String> {
        val path = "users/$userId/memories"
        return try {
            val docRef = if (memory.id.isNotBlank()) {
                db.collection(path).document(memory.id)
            } else {
                db.collection(path).document()
            }
            val memToSave = memory.copy(id = docRef.id, userId = userId)
            docRef.set(memToSave.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteMemory(userId: String, memoryId: String): Result<Unit> {
        val path = "users/$userId/memories/$memoryId"
        return try {
            db.document(path).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // Character profiles
    fun observeCharacters(userId: String): Flow<List<CharacterProfile>> {
        val path = "users/$userId/characters"
        return db.collection(path)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(CharacterProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                handleFirestoreError(e, OperationType.LIST, path)
                if (e is CancellationException) throw e
                emit(emptyList())
            }
    }

    suspend fun saveCharacter(userId: String, character: CharacterProfile): Result<String> {
        val path = "users/$userId/characters"
        return try {
            val docRef = if (character.id.isNotBlank()) {
                db.collection(path).document(character.id)
            } else {
                db.collection(path).document()
            }
            val charToSave = character.copy(id = docRef.id, userId = userId)
            docRef.set(charToSave.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteCharacter(userId: String, characterId: String): Result<Unit> {
        val path = "users/$userId/characters/$characterId"
        return try {
            db.document(path).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
