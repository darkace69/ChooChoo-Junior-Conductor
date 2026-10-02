package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainDao {
  @Query("SELECT * FROM conductor_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<ConductorProfileEntity?>

  @Query("SELECT * FROM conductor_profile WHERE id = 1 LIMIT 1")
  suspend fun getProfileOnce(): ConductorProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProfile(profile: ConductorProfileEntity)
}
