package com.example.data

import kotlinx.coroutines.flow.Flow

class TrainRepository(private val trainDao: TrainDao) {

  val profile: Flow<ConductorProfileEntity?> = trainDao.getProfile()

  suspend fun getProfileOnce(): ConductorProfileEntity {
    return trainDao.getProfileOnce() ?: ConductorProfileEntity().also {
      trainDao.saveProfile(it)
    }
  }

  suspend fun updateProfile(profile: ConductorProfileEntity) {
    trainDao.saveProfile(profile)
  }

  suspend fun addTicketsAndStars(tickets: Int, stars: Int = 0) {
    val current = getProfileOnce()
    val updated = current.copy(
      goldenTickets = current.goldenTickets + tickets,
      starsCollected = current.starsCollected + stars
    )
    trainDao.saveProfile(updated)
  }

  suspend fun recordStationStop(stationId: String, bonusTickets: Int) {
    val current = getProfileOnce()
    val visited = current.visitedStationsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    visited.add(stationId)
    val updated = current.copy(
      goldenTickets = current.goldenTickets + bonusTickets,
      starsCollected = current.starsCollected + 1,
      totalSafeStops = current.totalSafeStops + 1,
      safeStreak = current.safeStreak + 1,
      visitedStationsCsv = visited.joinToString(",")
    )
    trainDao.saveProfile(updated)
  }

  suspend fun unlockSecret(secretId: String, rewardTickets: Int) {
    val current = getProfileOnce()
    val secrets = current.unlockedSecretsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    if (!secrets.contains(secretId)) {
      secrets.add(secretId)
      val updated = current.copy(
        goldenTickets = current.goldenTickets + rewardTickets,
        starsCollected = current.starsCollected + 1,
        unlockedSecretsCsv = secrets.joinToString(",")
      )
      trainDao.saveProfile(updated)
    }
  }

  suspend fun buyAndEquipEngine(engineId: String, cost: Int) {
    val current = getProfileOnce()
    val engines = current.unlockedEnginesCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    if (engines.contains(engineId) || current.goldenTickets >= cost) {
      val newTickets = if (engines.contains(engineId)) current.goldenTickets else current.goldenTickets - cost
      engines.add(engineId)
      trainDao.saveProfile(
        current.copy(
          goldenTickets = newTickets,
          selectedEngineId = engineId,
          unlockedEnginesCsv = engines.joinToString(",")
        )
      )
    }
  }

  suspend fun buyAndEquipColor(colorId: String, cost: Int) {
    val current = getProfileOnce()
    val colors = current.unlockedColorsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    if (colors.contains(colorId) || current.goldenTickets >= cost) {
      val newTickets = if (colors.contains(colorId)) current.goldenTickets else current.goldenTickets - cost
      colors.add(colorId)
      trainDao.saveProfile(
        current.copy(
          goldenTickets = newTickets,
          selectedColorId = colorId,
          unlockedColorsCsv = colors.joinToString(",")
        )
      )
    }
  }

  suspend fun buyAndEquipSmoke(smokeId: String, cost: Int) {
    val current = getProfileOnce()
    val smokes = current.unlockedSmokesCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    if (smokes.contains(smokeId) || current.goldenTickets >= cost) {
      val newTickets = if (smokes.contains(smokeId)) current.goldenTickets else current.goldenTickets - cost
      smokes.add(smokeId)
      trainDao.saveProfile(
        current.copy(
          goldenTickets = newTickets,
          selectedSmokeId = smokeId,
          unlockedSmokesCsv = smokes.joinToString(",")
        )
      )
    }
  }
}
