package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TrainAudio
import com.example.data.AppDatabase
import com.example.data.ConductorProfileEntity
import com.example.data.TrainRepository
import com.example.model.LandscapeWorld
import com.example.model.SecretDiscovery
import com.example.model.SpeedNotch
import com.example.model.StationInfo
import com.example.model.TrackCurvature
import com.example.model.TrackSegment
import com.example.model.TrackSlope
import com.example.model.TrackWorldData
import com.example.model.WeatherCondition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

enum class ScreenTab {
  DRIVE,
  WORKSHOP,
  MISSIONS,
  LOGBOOK
}

data class StationArrivalCelebration(
  val station: StationInfo,
  val bonusTickets: Int,
  val passengerEmojis: List<String>
)

class TrainGameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TrainRepository
  val profile: StateFlow<ConductorProfileEntity?>

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  // Active Screen Tab
  private val _currentTab = MutableStateFlow(ScreenTab.DRIVE)
  val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

  // Conductor Drive State
  private val _speedNotch = MutableStateFlow(SpeedNotch.STOP)
  val speedNotch: StateFlow<SpeedNotch> = _speedNotch.asStateFlow()

  private val _actualSpeedMph = MutableStateFlow(0f)
  val actualSpeedMph: StateFlow<Float> = _actualSpeedMph.asStateFlow()

  // Train Wreck / Derailment State
  private val _isWrecked = MutableStateFlow(false)
  val isWrecked: StateFlow<Boolean> = _isWrecked.asStateFlow()

  private val _currentWreck = MutableStateFlow<com.example.model.TrainWreckEvent?>(null)
  val currentWreck: StateFlow<com.example.model.TrainWreckEvent?> = _currentWreck.asStateFlow()

  private val _dangerLevel = MutableStateFlow(0f)
  val dangerLevel: StateFlow<Float> = _dangerLevel.asStateFlow()

  private var overspeedDangerAccumulator = 0f
  private var hadHighDangerCloseCall = false

  private val _trackPosition = MutableStateFlow(10f)
  val trackPosition: StateFlow<Float> = _trackPosition.asStateFlow()

  private val _currentWorld = MutableStateFlow(LandscapeWorld.FOREST)
  val currentWorld: StateFlow<LandscapeWorld> = _currentWorld.asStateFlow()

  private val _weather = MutableStateFlow(WeatherCondition.CLEAR)
  val weather: StateFlow<WeatherCondition> = _weather.asStateFlow()

  private val _isWipersOn = MutableStateFlow(false)
  val isWipersOn: StateFlow<Boolean> = _isWipersOn.asStateFlow()

  private val _isHeadlightOn = MutableStateFlow(true)
  val isHeadlightOn: StateFlow<Boolean> = _isHeadlightOn.asStateFlow()

  // Track status
  private val _currentSlope = MutableStateFlow(TrackSlope.LEVEL)
  val currentSlope: StateFlow<TrackSlope> = _currentSlope.asStateFlow()

  private val _currentCurvature = MutableStateFlow(TrackCurvature.STRAIGHT)
  val currentCurvature: StateFlow<TrackCurvature> = _currentCurvature.asStateFlow()

  // Live Alerts & Banners
  private val _cautionMessage = MutableStateFlow<String?>(null)
  val cautionMessage: StateFlow<String?> = _cautionMessage.asStateFlow()

  private val _rewardToast = MutableStateFlow<String?>(null)
  val rewardToast: StateFlow<String?> = _rewardToast.asStateFlow()

  // Station and Junction state
  private val _stationCelebration = MutableStateFlow<StationArrivalCelebration?>(null)
  val stationCelebration: StateFlow<StationArrivalCelebration?> = _stationCelebration.asStateFlow()

  private val _approachingStation = MutableStateFlow<StationInfo?>(null)
  val approachingStation: StateFlow<StationInfo?> = _approachingStation.asStateFlow()

  private val _approachingJunction = MutableStateFlow<TrackSegment?>(null)
  val approachingJunction: StateFlow<TrackSegment?> = _approachingJunction.asStateFlow()

  private val _selectedSwitchTurn = MutableStateFlow(false)
  val selectedSwitchTurn: StateFlow<Boolean> = _selectedSwitchTurn.asStateFlow()

  private val _nearbySecret = MutableStateFlow<SecretDiscovery?>(null)
  val nearbySecret: StateFlow<SecretDiscovery?> = _nearbySecret.asStateFlow()

  private val _discoveredSecretModal = MutableStateFlow<SecretDiscovery?>(null)
  val discoveredSecretModal: StateFlow<SecretDiscovery?> = _discoveredSecretModal.asStateFlow()

  // PAUSE FEATURE
  private val _isPaused = MutableStateFlow(false)
  val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

  // SPECIAL DELIVERY MISSIONS FEATURE
  private val _activeMission = MutableStateFlow<com.example.model.SpecialDeliveryMission?>(null)
  val activeMission: StateFlow<com.example.model.SpecialDeliveryMission?> = _activeMission.asStateFlow()

  private val _missionState = MutableStateFlow(com.example.model.MissionState.IDLE)
  val missionState: StateFlow<com.example.model.MissionState> = _missionState.asStateFlow()

  private val _missionTimeRemainingSec = MutableStateFlow(0)
  val missionTimeRemainingSec: StateFlow<Int> = _missionTimeRemainingSec.asStateFlow()

  private val _missionIncidents = MutableStateFlow(0)
  val missionIncidents: StateFlow<Int> = _missionIncidents.asStateFlow()

  private val _missionCelebrationMessage = MutableStateFlow<String?>(null)
  val missionCelebrationMessage: StateFlow<String?> = _missionCelebrationMessage.asStateFlow()

  // ADS BETWEEN MISSIONS
  private val _currentInterstitialAd = MutableStateFlow<com.example.model.MissionAd?>(null)
  val currentInterstitialAd: StateFlow<com.example.model.MissionAd?> = _currentInterstitialAd.asStateFlow()
  private var pendingAdAction: (() -> Unit)? = null

  // RAILWAY SPEED SIGNS (SLOW DOWN & SPEED UP)
  private val _activeRailwaySign = MutableStateFlow<com.example.model.RailwaySign?>(null)
  val activeRailwaySign: StateFlow<com.example.model.RailwaySign?> = _activeRailwaySign.asStateFlow()

  private val _approachingSigns = MutableStateFlow<List<com.example.model.RailwaySign>>(emptyList())
  val approachingSigns: StateFlow<List<com.example.model.RailwaySign>> = _approachingSigns.asStateFlow()

  // Sound chug timer
  private var lastChugTime = 0L
  private var safeCurveEvaluated = false
  private var stationHandledForStop = false
  private var currentSlickness = 1.0f
  private var lastSignChimedId: String? = null
  private var lastSignRewardedId: String? = null

  init {
    val database = AppDatabase.getDatabase(application)
    repository = TrainRepository(database.trainDao())
    profile = repository.profile.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = ConductorProfileEntity()
    )

    // Ensure database profile row exists
    viewModelScope.launch {
      try {
        repository.getProfileOnce()
      } catch (_: Throwable) {}
    }

    // Start train simulation loop
    startPhysicsLoop()
    startWeatherCycle()
  }

  fun setTab(tab: ScreenTab) {
    _currentTab.value = tab
    vibrate(20)
  }

  fun setSpeedNotch(notch: SpeedNotch) {
    if (_isWrecked.value) {
      if (notch != SpeedNotch.STOP) {
        showRewardToast("⚠️ Train is derailed! Call the rescue crane or tow to station to rerail.")
        TrainAudio.playCautionDing()
        vibrate(25)
      }
      return
    }
    if (_speedNotch.value != notch) {
      _speedNotch.value = notch
      if (notch == SpeedNotch.STOP) {
        TrainAudio.playBrakeHiss()
        vibrate(40)
      } else {
        vibrate(25)
      }
    }
  }

  fun pullWhistle() {
    TrainAudio.playWhistle()
    vibrate(60)
  }

  fun toggleWipers() {
    _isWipersOn.value = !_isWipersOn.value
    TrainAudio.playWiperSqueak()
    vibrate(15)
  }

  fun toggleHeadlight() {
    _isHeadlightOn.value = !_isHeadlightOn.value
    vibrate(15)
  }

  private fun cycleWeather() {
    val nextWeather = when (_weather.value) {
      WeatherCondition.CLEAR -> WeatherCondition.LIGHT_RAIN
      WeatherCondition.LIGHT_RAIN -> WeatherCondition.GENTLE_SNOW
      WeatherCondition.GENTLE_SNOW -> WeatherCondition.CLEAR
    }
    setWeather(nextWeather)
  }

  private fun setWeather(cond: WeatherCondition) {
    if (_weather.value != cond) {
      _weather.value = cond
      val message = when (cond) {
        WeatherCondition.CLEAR -> "🌤️ Clouds parting... Warm sun breaking through!"
        WeatherCondition.LIGHT_RAIN -> "🌧️ Soft rain rolling in... Tracks becoming slicker!"
        WeatherCondition.GENTLE_SNOW -> "❄️ Gentle snowflakes drifting down... Winter wonderland!"
      }
      showRewardToast(message)
      vibrate(20)
    }
  }

  fun toggleSwitchTrack() {
    _selectedSwitchTurn.value = !_selectedSwitchTurn.value
    TrainAudio.playBell()
    vibrate(40)
  }

  fun dismissStationCelebration() {
    _stationCelebration.value = null
  }

  fun dismissSecretModal() {
    _discoveredSecretModal.value = null
  }

  fun tapSecret(secret: SecretDiscovery) {
    viewModelScope.launch {
      repository.unlockSecret(secret.id, secret.rewardTickets)
      _discoveredSecretModal.value = secret
      TrainAudio.playRewardFanfare()
      vibrate(60)
    }
  }

  // PAUSE / RESUME TOGGLE
  fun togglePause() {
    _isPaused.value = !_isPaused.value
    if (_isPaused.value) {
      TrainAudio.playBrakeHiss()
    } else {
      TrainAudio.playBell()
    }
    vibrate(25)
  }

  // SPECIAL DELIVERY MISSIONS & ADS BETWEEN MISSIONS
  fun showAdBetweenMissions(onFinish: (() -> Unit)? = null) {
    pendingAdAction = onFinish
    _currentInterstitialAd.value = com.example.model.MissionAdCatalog.getNextAd()
    TrainAudio.playAdChime()
    vibrate(25)
  }

  fun dismissInterstitialAd(rewardBonus: Boolean = true) {
    val ad = _currentInterstitialAd.value
    _currentInterstitialAd.value = null
    if (rewardBonus && ad != null) {
      viewModelScope.launch {
        repository.addTicketsAndStars(ad.rewardTickets, 0)
        showRewardToast("🎁 +${ad.rewardTickets} Golden Tickets from ${ad.title}!")
        TrainAudio.playRewardFanfare()
        vibrate(40)
      }
    }
    pendingAdAction?.invoke()
    pendingAdAction = null
  }

  fun watchSponsorAd() {
    showAdBetweenMissions()
  }

  fun startMission(mission: com.example.model.SpecialDeliveryMission) {
    val wasCompleted = _missionState.value == com.example.model.MissionState.COMPLETED
    if (wasCompleted) {
      // Show interstitial ad between missions when embarking on a new delivery!
      showAdBetweenMissions {
        launchMissionInternal(mission)
      }
    } else {
      launchMissionInternal(mission)
    }
  }

  private fun launchMissionInternal(mission: com.example.model.SpecialDeliveryMission) {
    _activeMission.value = mission
    _missionState.value = com.example.model.MissionState.PICKUP_READY
    _missionTimeRemainingSec.value = mission.timeLimitSec
    _missionIncidents.value = 0
    showRewardToast("📋 Mission Started: Head to ${mission.pickupStationName} to load ${mission.cargoName}!")
    TrainAudio.playBell()
    vibrate(30)

    // Launch mission countdown timer
    viewModelScope.launch {
      while (_activeMission.value?.id == mission.id && _missionState.value != com.example.model.MissionState.COMPLETED && _missionState.value != com.example.model.MissionState.FAILED) {
        delay(1000)
        if (!_isPaused.value) { // Pause pauses mission timer too!
          if (_missionTimeRemainingSec.value > 0) {
            _missionTimeRemainingSec.value -= 1
          } else {
            _missionState.value = com.example.model.MissionState.FAILED
            showRewardToast("⏰ Time is up! Special Delivery failed. Try again!")
            TrainAudio.playCautionDing()
            vibrate(50)
            break
          }
        }
      }
    }
  }

  fun cancelMission() {
    _activeMission.value = null
    _missionState.value = com.example.model.MissionState.IDLE
    _missionIncidents.value = 0
    showRewardToast("Mission cancelled.")
    vibrate(20)
  }

  fun dismissMissionCelebration() {
    _missionCelebrationMessage.value = null
    // Automatically trigger interstitial ad between missions when collecting rewards!
    showAdBetweenMissions()
  }

  fun buyAndEquipEngine(engineId: String, cost: Int) {
    viewModelScope.launch {
      repository.buyAndEquipEngine(engineId, cost)
      TrainAudio.playWhistle()
      vibrate(30)
    }
  }

  fun buyAndEquipColor(colorId: String, cost: Int) {
    viewModelScope.launch {
      repository.buyAndEquipColor(colorId, cost)
      TrainAudio.playBell()
      vibrate(30)
    }
  }

  fun buyAndEquipSmoke(smokeId: String, cost: Int) {
    viewModelScope.launch {
      repository.buyAndEquipSmoke(smokeId, cost)
      TrainAudio.playBell()
      vibrate(30)
    }
  }

  private fun startPhysicsLoop() {
    viewModelScope.launch {
      var lastTime = System.currentTimeMillis()
      while (true) {
        val now = System.currentTimeMillis()
        val dt = ((now - lastTime) / 1000f).coerceIn(0.01f, 0.1f)
        lastTime = now

        try {
          updateTrainPhysics(dt)
        } catch (_: Throwable) {}
        delay(33) // ~30 FPS smooth physics update
      }
    }
  }

  private fun updateTrainPhysics(dt: Float) {
    if (_isPaused.value || _isWrecked.value) {
      return // Paused or Wrecked!
    }

    val world = _currentWorld.value
    val pos = _trackPosition.value
    val segment = TrackWorldData.getSegmentAt(world, pos)
    val weatherCond = _weather.value
    val notch = _speedNotch.value

    _currentSlope.value = segment.slope
    _currentCurvature.value = segment.curvature

    // Calculate target speed from throttle
    var targetSpeed = notch.baseSpeedMph

    // Gravity influence on slope
    val gravityFactor = segment.slope.gravityFactor
    // Smooth gradual transition of track slickness
    val targetSlickness = weatherCond.frictionMultiplier
    val slicknessDiff = targetSlickness - currentSlickness
    currentSlickness += (slicknessDiff * dt * 0.4f).coerceIn(-abs(slicknessDiff), abs(slicknessDiff))
    val slickness = currentSlickness

    if (notch == SpeedNotch.STOP) {
      // Braking deceleration (transitions gradually with slickness)
      val brakeRate = 28f / slickness
      if (_actualSpeedMph.value > 0) {
        _actualSpeedMph.value = (_actualSpeedMph.value - brakeRate * dt).coerceAtLeast(0f)
      } else if (_actualSpeedMph.value < 0) {
        _actualSpeedMph.value = (_actualSpeedMph.value + brakeRate * dt).coerceAtMost(0f)
      }
    } else {
      // When moving forward, downhills add extra acceleration!
      if (notch.value > 0) {
        if (gravityFactor > 0) {
          // Downhill boost (slicker tracks = higher boost!)
          targetSpeed += (gravityFactor * 14f * slickness)
        } else if (gravityFactor < 0) {
          // Uphill drag
          targetSpeed += (gravityFactor * 10f)
          if (targetSpeed < 6f) targetSpeed = 6f
        }
      }

      // Smooth acceleration toward target speed
      val accelRate = if (targetSpeed > _actualSpeedMph.value) 14f else 20f
      val step = accelRate * dt
      if (_actualSpeedMph.value < targetSpeed) {
        _actualSpeedMph.value = (_actualSpeedMph.value + step).coerceAtMost(targetSpeed)
      } else {
        _actualSpeedMph.value = (_actualSpeedMph.value - step).coerceAtLeast(targetSpeed)
      }
    }

    // Move along track
    val speed = _actualSpeedMph.value
    val newPos = pos + (speed * dt * 0.45f)
    _trackPosition.value = ((newPos % 360f) + 360f) % 360f

    // Sound engine trigger
    val absSpeed = abs(speed)
    if (absSpeed > 2f) {
      val chugInterval = when {
        absSpeed > 35f -> 220L
        absSpeed > 18f -> 350L
        else -> 550L
      }
      if (System.currentTimeMillis() - lastChugTime > chugInterval) {
        TrainAudio.playChug(isFast = absSpeed > 25f)
        lastChugTime = System.currentTimeMillis()
      }
    }

    // Curve and Downhill Speed Safety Check
    val maxSafe = segment.curvature.maxSafeSpeed
    val isCurved = segment.curvature != TrackCurvature.STRAIGHT
    val isSteepDownhill = segment.slope == TrackSlope.DOWNHILL_STEEP

    // Critical speed threshold for wreck / derailment
    val criticalThreshold = when {
      segment.curvature == TrackCurvature.CURVE_SHARP_LEFT || segment.curvature == TrackCurvature.CURVE_SHARP_RIGHT -> 36f
      segment.curvature == TrackCurvature.CURVE_GENTLE_LEFT || segment.curvature == TrackCurvature.CURVE_GENTLE_RIGHT -> 48f
      isSteepDownhill -> 52f
      else -> 58f
    }

    if (absSpeed >= criticalThreshold) {
      safeCurveEvaluated = false
      val excess = absSpeed - criticalThreshold
      val accumulationRate = 0.9f + (excess / 5f)
      overspeedDangerAccumulator += dt * accumulationRate
      val dLevel = (overspeedDangerAccumulator / 1.0f).coerceIn(0f, 1f)
      _dangerLevel.value = dLevel

      if (dLevel > 0.35f) {
        hadHighDangerCloseCall = true
      }

      val secondsLeft = ((1f - dLevel) * 1.5f).coerceAtLeast(0.1f)
      _cautionMessage.value = "🚨 SPEED TOO HIGH (${"%.0f".format(absSpeed)} MPH)! DERAILMENT IN ${"%.1f".format(secondsLeft)}s!"

      if (overspeedDangerAccumulator >= 1.0f) {
        triggerWreck(
          speedMph = absSpeed,
          maxSafe = maxSafe,
          segment = segment,
          world = world
        )
        return
      }
    } else {
      if (overspeedDangerAccumulator > 0f) {
        overspeedDangerAccumulator = (overspeedDangerAccumulator - dt * 2.2f).coerceAtLeast(0f)
        _dangerLevel.value = (overspeedDangerAccumulator / 1.0f).coerceIn(0f, 1f)
        if (overspeedDangerAccumulator == 0f && hadHighDangerCloseCall) {
          hadHighDangerCloseCall = false
          showRewardToast("😅 PHEW! Emergency braking prevented a derailment! Good save!")
          TrainAudio.playWhistle()
        }
      }

      if (absSpeed > maxSafe || (isSteepDownhill && absSpeed > 36f)) {
        safeCurveEvaluated = false
        if (_cautionMessage.value == null || _cautionMessage.value?.startsWith("🚨") == true) {
          _cautionMessage.value = if (isSteepDownhill) {
            "⚠️ Steep Downhill! Slow down for safety!"
          } else {
            "⚠️ Sharp Curve Ahead! Slow down!"
          }
          TrainAudio.playCautionDing()
          vibrate(30)
        }

        // Track incident for active special delivery mission
        val currentMission = _activeMission.value
        if (currentMission != null && _missionState.value == com.example.model.MissionState.DELIVERING) {
          val newIncidents = _missionIncidents.value + 1
          _missionIncidents.value = newIncidents
          if (newIncidents > currentMission.maxIncidents) {
            _missionState.value = com.example.model.MissionState.FAILED
            showRewardToast("💔 Cargo damaged from reckless speed! Mission failed.")
          } else {
            showRewardToast("⚠️ Cargo Shaken! Incident $newIncidents / ${currentMission.maxIncidents}")
          }
        }
      } else {
        // Safe navigation reward
        if ((isCurved || isSteepDownhill) && absSpeed > 10f && !safeCurveEvaluated) {
          safeCurveEvaluated = true
          showRewardToast("🌟 Master Conductor! Smooth & Safe Speed! +10 Tickets")
          viewModelScope.launch {
            repository.addTicketsAndStars(tickets = 10, stars = 0)
          }
        }
        _cautionMessage.value = null
      }
    }

    // Railway Signs Detection (SLOW DOWN & SPEED UP)
    val signsInWorld = TrackWorldData.allRailwaySigns.filter { it.world == world }
    val normPos = _trackPosition.value

    // Signs approaching ahead (within 45 units)
    val signsAhead = signsInWorld.filter { sign ->
      val forwardDist = (sign.trackPosition - normPos + 360f) % 360f
      forwardDist in 0f..45f
    }
    _approachingSigns.value = signsAhead

    // Active immediate sign (within 35 units)
    val immediateSign = signsAhead.firstOrNull { sign ->
      val forwardDist = (sign.trackPosition - normPos + 360f) % 360f
      forwardDist in 0f..35f
    }
    _activeRailwaySign.value = immediateSign

    if (immediateSign != null) {
      val forwardDist = (immediateSign.trackPosition - normPos + 360f) % 360f
      // Chime when first approaching
      if (forwardDist < 28f && lastSignChimedId != immediateSign.id) {
        lastSignChimedId = immediateSign.id
        TrainAudio.playCautionDing()
      }

      // Check obedience when passing the sign
      if (forwardDist < 8f && lastSignRewardedId != immediateSign.id) {
        val obeyed = when (immediateSign.type) {
          com.example.model.RailwaySignType.SLOW_DOWN -> absSpeed <= (immediateSign.targetSpeedMph + 4f)
          com.example.model.RailwaySignType.SPEED_UP -> absSpeed >= 20f || _speedNotch.value.value >= 2
        }
        if (obeyed) {
          lastSignRewardedId = immediateSign.id
          showRewardToast("🌟 Great Conductor! Obeyed ${immediateSign.title} sign! +5 Tickets")
          viewModelScope.launch {
            repository.addTicketsAndStars(tickets = 5, stars = 0)
          }
        }
      }
    }

    // Check station proximity & stop zone
    val station = TrackWorldData.allStations.firstOrNull { it.world == world }
    if (station != null) {
      val distToStation = abs(_trackPosition.value - station.trackPosition)
      if (distToStation < 24f) {
        _approachingStation.value = station
        if (distToStation < 6f && absSpeed < 1.0f) {
          // Perfectly stopped inside station platform!
          if (!stationHandledForStop) {
            stationHandledForStop = true
            handleStationArrival(station)
          }
        }
      } else {
        _approachingStation.value = null
        if (distToStation > 15f) {
          stationHandledForStop = false
        }
      }
    }

    // Check junction switches
    if (segment.junctionTargetWorld != null) {
      _approachingJunction.value = segment
      // When train reaches boundary of segment (e.g. pos near endPos), if switched, transition world!
      if (abs(_trackPosition.value - segment.endPos) < 4f && _selectedSwitchTurn.value) {
        _currentWorld.value = segment.junctionTargetWorld
        _selectedSwitchTurn.value = false
        showRewardToast("🛤️ Switched to ${segment.junctionTargetWorld.title}!")
        TrainAudio.playBell()
      }
    } else {
      _approachingJunction.value = null
    }

    // Check nearby secrets along route
    val nearby = TrackWorldData.allSecrets.firstOrNull {
      it.world == world && abs(_trackPosition.value - it.trackPosition) < 8f
    }
    _nearbySecret.value = nearby
  }

  private fun handleStationArrival(station: StationInfo) {
    viewModelScope.launch {
      TrainAudio.playBell()
      TrainAudio.playRewardFanfare()
      vibrate(80)
      repository.recordStationStop(station.id, station.ticketReward)
      _stationCelebration.value = StationArrivalCelebration(
        station = station,
        bonusTickets = station.ticketReward,
        passengerEmojis = station.passengersWaiting
      )

      // Handle Special Delivery mission events
      val currentMission = _activeMission.value
      if (currentMission != null) {
        if (_missionState.value == com.example.model.MissionState.PICKUP_READY && station.id == currentMission.pickupStationId) {
          _missionState.value = com.example.model.MissionState.DELIVERING
          showRewardToast("📦 Loaded ${currentMission.cargoEmoji} ${currentMission.cargoName}! Deliver to ${currentMission.dropoffStationName}!")
        } else if (_missionState.value == com.example.model.MissionState.DELIVERING && station.id == currentMission.dropoffStationId) {
          _missionState.value = com.example.model.MissionState.COMPLETED
          repository.addTicketsAndStars(currentMission.rewardTickets, currentMission.rewardStars)
          _missionCelebrationMessage.value = "🎉 Special Delivery Complete! Delivered ${currentMission.cargoName}!\n+${currentMission.rewardTickets} Golden Tickets & +${currentMission.rewardStars} Stars! ⭐"
        }
      }
    }
  }

  private fun showRewardToast(message: String) {
    _rewardToast.value = message
    viewModelScope.launch {
      delay(3000)
      if (_rewardToast.value == message) {
        _rewardToast.value = null
      }
    }
  }

  private fun startWeatherCycle() {
    viewModelScope.launch {
      while (true) {
        delay(240000) // auto cycle every 4 minutes (much less frequent)
        cycleWeather()
      }
    }
  }

  private fun vibrate(ms: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(ms)
      }
    } catch (_: Throwable) {}
  }

  private fun vibrateCrash() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val timings = longArrayOf(0, 160, 90, 260, 90, 420)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(longArrayOf(0, 160, 90, 260, 90, 420), -1)
      }
    } catch (_: Throwable) {}
  }

  fun triggerWreck(
    speedMph: Float,
    maxSafe: Float,
    segment: TrackSegment,
    world: LandscapeWorld
  ) {
    if (_isWrecked.value) return
    _isWrecked.value = true
    _actualSpeedMph.value = 0f
    _speedNotch.value = SpeedNotch.STOP
    overspeedDangerAccumulator = 0f
    _dangerLevel.value = 0f
    hadHighDangerCloseCall = false
    _cautionMessage.value = "💥 TRAIN DERAILMENT! Locomotive jumped the rails!"

    val isCurve = segment.curvature != TrackCurvature.STRAIGHT
    val isDownhill = segment.slope == TrackSlope.DOWNHILL_STEEP

    val (title, detail, tip) = when {
      segment.curvature == TrackCurvature.CURVE_SHARP_LEFT || segment.curvature == TrackCurvature.CURVE_SHARP_RIGHT -> {
        Triple(
          "💥 SHARP CURVE DERAILMENT!",
          "Entering ${segment.curvature.label} at ${"%.1f".format(speedMph)} MPH exceeded the track's ${"%.0f".format(maxSafe)} MPH limit! Centrifugal inertia snapped the wheel flange contact and threw the train off the tracks!",
          "Keep your speed in Notch 1 (16 MPH) or apply the STOP brake before hitting sharp turns!"
        )
      }
      isDownhill -> {
        Triple(
          "💥 RUNAWAY DOWNHILL WRECK!",
          "Gravity accelerated your locomotive to a runaway speed of ${"%.1f".format(speedMph)} MPH down ${segment.slope.label}! The train could not hold the grade and rolled off the roadbed!",
          "On steep downhills, switch to STOP or REV to gently brake and control your train's descent!"
        )
      }
      isCurve -> {
        Triple(
          "💥 EXCESSIVE CURVE SPEED WRECK!",
          "Cruising at ${"%.1f".format(speedMph)} MPH around ${segment.curvature.label} overcame rail friction, sliding the locomotive into the scenic embankment!",
          "Check curve warning signs early and ease up on the throttle!"
        )
      }
      else -> {
        Triple(
          "💥 RUNAWAY OVERSPEED WRECK!",
          "Speed exceeded the track structural limit of 55 MPH at ${"%.1f".format(speedMph)} MPH! The locomotive jumped the steel rails!",
          "Always monitor your speedometer and maintain safe cruising speed!"
        )
      }
    }

    val wreckEvent = com.example.model.TrainWreckEvent(
      speedMph = speedMph,
      maxSafeSpeedMph = maxSafe,
      reasonTitle = title,
      incidentDetail = detail,
      safetyTip = tip,
      world = world,
      trackSlope = segment.slope,
      trackCurvature = segment.curvature
    )
    _currentWreck.value = wreckEvent

    // Audio effects
    TrainAudio.playDerailmentScreech()
    viewModelScope.launch {
      delay(380)
      TrainAudio.playCrashWreck()
    }

    // Heavy crash haptic rumble
    vibrateCrash()

    // Handle mission failure if active
    val currentMission = _activeMission.value
    if (currentMission != null && _missionState.value == com.example.model.MissionState.DELIVERING) {
      _missionState.value = com.example.model.MissionState.FAILED
      _missionIncidents.value += 1
      showRewardToast("💥 Cargo damaged in train derailment! Special Delivery failed.")
    } else {
      showRewardToast("🚨 TRAIN DERAILMENT! Speed was too high!")
    }
  }

  fun rerailTrain() {
    _isWrecked.value = false
    _currentWreck.value = null
    _dangerLevel.value = 0f
    overspeedDangerAccumulator = 0f
    hadHighDangerCloseCall = false
    _actualSpeedMph.value = 0f
    _speedNotch.value = SpeedNotch.STOP
    _cautionMessage.value = null

    TrainAudio.playCraneClang()
    viewModelScope.launch {
      delay(300)
      TrainAudio.playWhistle()
    }
    vibrate(40)
    showRewardToast("🏗️ Breakdown Crane rerailed your locomotive! Safe to proceed!")
  }

  fun towToNearestStation() {
    val world = _currentWorld.value
    val station = TrackWorldData.allStations.firstOrNull { it.world == world }
    if (station != null) {
      _trackPosition.value = station.trackPosition
    }
    _isWrecked.value = false
    _currentWreck.value = null
    _dangerLevel.value = 0f
    overspeedDangerAccumulator = 0f
    hadHighDangerCloseCall = false
    _actualSpeedMph.value = 0f
    _speedNotch.value = SpeedNotch.STOP
    _cautionMessage.value = null

    TrainAudio.playCraneClang()
    viewModelScope.launch {
      delay(250)
      TrainAudio.playBell()
    }
    vibrate(40)
    val stName = station?.name ?: "Station"
    showRewardToast("🚉 Towed safely to $stName platform! Safety inspection complete!")
  }

  fun simulateSpeedWreck() {
    val world = _currentWorld.value
    val pos = _trackPosition.value
    val segment = TrackWorldData.getSegmentAt(world, pos)
    val maxSafe = segment.curvature.maxSafeSpeed
    val simulatedSpeed = (maxSafe + 18f).coerceAtLeast(46f)
    triggerWreck(
      speedMph = simulatedSpeed,
      maxSafe = maxSafe,
      segment = segment,
      world = world
    )
  }
}
