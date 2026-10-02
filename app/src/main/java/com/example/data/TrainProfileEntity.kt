package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conductor_profile")
data class ConductorProfileEntity(
  @PrimaryKey val id: Int = 1,
  val goldenTickets: Int = 150, // Starting tickets so kids can try customizing!
  val starsCollected: Int = 3,
  val selectedEngineId: String = "classic_steam",
  val selectedColorId: String = "red",
  val selectedSmokeId: String = "classic_puffs",
  val unlockedEnginesCsv: String = "classic_steam",
  val unlockedColorsCsv: String = "red,blue",
  val unlockedSmokesCsv: String = "classic_puffs",
  val unlockedSecretsCsv: String = "",
  val visitedStationsCsv: String = "",
  val totalSafeStops: Int = 0,
  val safeStreak: Int = 0
)
