package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "social_profiles")
data class SocialProfile(
    @PrimaryKey val id: String, // e.g., "instagram", "twitter", "facebook", "linkedin", "tiktok"
    val username: String,
    val displayName: String,
    val profilePicHexColor: String, // Hex shade for a modern stylized avatar
    val isConnected: Boolean = true,
    val followerCount: Int = 0
) : Serializable

@Entity(tableName = "social_posts")
data class SocialPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val scheduledTime: Long, // timestamp miliseconds
    val platforms: String, // comma separated e.g., "instagram,twitter"
    val mediaUri: String? = null, // placeholder names or custom shapes
    val status: String, // "DRAFT", "REVIEW", "APPROVED", "PUBLISHED"
    val isEvergreen: Boolean = false,
    val clicks: Int = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0
) : Serializable

@Entity(tableName = "asset_items")
data class AssetItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val mediaPath: String, // visual or string details
    val mimeType: String, // e.g., "image/jpeg", "text/plain"
    val tags: String, // comma separated
    val type: String, // "IMAGE", "VIDEO", "TEXT_TEMPLATE"
    val createdTime: Long = System.currentTimeMillis()
) : Serializable
