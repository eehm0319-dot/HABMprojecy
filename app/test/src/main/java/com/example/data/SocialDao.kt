package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // --- Posts ---
    @Query("SELECT * FROM social_posts ORDER BY scheduledTime ASC")
    fun getAllPosts(): Flow<List<SocialPost>>

    @Query("SELECT * FROM social_posts WHERE id = :id")
    suspend fun getPostById(id: Int): SocialPost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SocialPost): Long

    @Update
    suspend fun updatePost(post: SocialPost)

    @Delete
    suspend fun deletePost(post: SocialPost)

    @Query("DELETE FROM social_posts WHERE id = :id")
    suspend fun deletePostById(id: Int)

    // --- Profiles ---
    @Query("SELECT * FROM social_profiles")
    fun getAllProfiles(): Flow<List<SocialProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<SocialProfile>)

    @Update
    suspend fun updateProfile(profile: SocialProfile)

    // --- Assets ---
    @Query("SELECT * FROM asset_items ORDER BY createdTime DESC")
    fun getAllAssets(): Flow<List<AssetItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetItem): Long

    @Delete
    suspend fun deleteAsset(asset: AssetItem)
}
