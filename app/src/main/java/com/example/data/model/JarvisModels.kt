package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val assistantName: String = "JARVIS",
    val ownerName: String = "Sir",
    val language: String = "English",
    val voiceSpeed: Double = 1.0,
    val voicePitch: Double = 1.0,
    val wakeWordEnabled: Boolean = false,
    val themeMode: String = "dark",
    val avatarUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "assistantName" to assistantName,
            "ownerName" to ownerName,
            "language" to language,
            "voiceSpeed" to voiceSpeed,
            "voicePitch" to voicePitch,
            "wakeWordEnabled" to wakeWordEnabled,
            "themeMode" to themeMode,
            "avatarUrl" to avatarUrl,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
data class Conversation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val language: String = "English",
    val lastMessage: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "title" to title,
            "language" to language,
            "lastMessage" to lastMessage,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val sender: String = "user", // "user" or "jarvis"
    val text: String = "",
    val codeLanguage: String = "",
    val hasImage: Boolean = false,
    val imageUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "sender" to sender,
            "text" to text,
            "codeLanguage" to codeLanguage,
            "hasImage" to hasImage,
            "imageUrl" to imageUrl,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
data class StudioProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val projectType: String = "coding", // "coding", "website", "app", "anime", "drawing", "3d"
    val description: String = "",
    val language: String = "Kotlin",
    val content: String = "",
    val status: String = "completed",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "title" to title,
            "projectType" to projectType,
            "description" to description,
            "language" to language,
            "content" to content,
            "status" to status,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
data class MemoryItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val category: String = "General",
    val key: String = "",
    val value: String = "",
    val isPrivate: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "category" to category,
            "key" to key,
            "value" to value,
            "isPrivate" to isPrivate,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
data class CharacterProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val ageCategory: String = "Adult",
    val personality: String = "",
    val powers: String = "",
    val costume: String = "",
    val hairstyle: String = "",
    val accessories: String = "",
    val weapons: String = "",
    val bio: String = "",
    val referenceNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "ageCategory" to ageCategory,
            "personality" to personality,
            "powers" to powers,
            "costume" to costume,
            "hairstyle" to hairstyle,
            "accessories" to accessories,
            "weapons" to weapons,
            "bio" to bio,
            "referenceNotes" to referenceNotes,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}
