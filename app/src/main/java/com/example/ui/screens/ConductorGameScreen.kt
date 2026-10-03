package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ConductorProfileEntity
import com.example.model.MissionState
import com.example.ui.components.ConductorControlPanel
import com.example.ui.components.ConductorLogbookScreen
import com.example.ui.components.ConductorPerspectiveView
import com.example.ui.components.ConductorTopHeader
import com.example.ui.components.MissionCelebrationDialog
import com.example.ui.components.MissionInterstitialAdDialog
import com.example.ui.components.SecretDiscoveredDialog
import com.example.ui.components.SpecialDeliveryMissionsScreen
import com.example.ui.components.StationCelebrationDialog
import com.example.ui.components.TrainAppearanceSideView
import com.example.ui.components.TrainCustomizationScreen
import com.example.ui.components.TrainWreckDialog
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.TrainGameViewModel

@Composable
fun ConductorGameScreen(
  viewModel: TrainGameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val safeProfile = profile ?: ConductorProfileEntity()

  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val speedNotch by viewModel.speedNotch.collectAsStateWithLifecycle()
  val actualSpeedMph by viewModel.actualSpeedMph.collectAsStateWithLifecycle()
  val trackPosition by viewModel.trackPosition.collectAsStateWithLifecycle()
  val currentWorld by viewModel.currentWorld.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val isWipersOn by viewModel.isWipersOn.collectAsStateWithLifecycle()
  val isHeadlightOn by viewModel.isHeadlightOn.collectAsStateWithLifecycle()
  val currentSlope by viewModel.currentSlope.collectAsStateWithLifecycle()
  val currentCurvature by viewModel.currentCurvature.collectAsStateWithLifecycle()
  val cautionMessage by viewModel.cautionMessage.collectAsStateWithLifecycle()
  val rewardToast by viewModel.rewardToast.collectAsStateWithLifecycle()
  val stationCelebration by viewModel.stationCelebration.collectAsStateWithLifecycle()
  val approachingStation by viewModel.approachingStation.collectAsStateWithLifecycle()
  val approachingJunction by viewModel.approachingJunction.collectAsStateWithLifecycle()
  val selectedSwitchTurn by viewModel.selectedSwitchTurn.collectAsStateWithLifecycle()
  val nearbySecret by viewModel.nearbySecret.collectAsStateWithLifecycle()
  val discoveredSecretModal by viewModel.discoveredSecretModal.collectAsStateWithLifecycle()

  // Train Wreck / Derailment state
  val isWrecked by viewModel.isWrecked.collectAsStateWithLifecycle()
  val currentWreck by viewModel.currentWreck.collectAsStateWithLifecycle()
  val dangerLevel by viewModel.dangerLevel.collectAsStateWithLifecycle()

  // Pause state
  val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()

  // Special Delivery Missions state
  val activeMission by viewModel.activeMission.collectAsStateWithLifecycle()
  val missionState by viewModel.missionState.collectAsStateWithLifecycle()
  val missionTimeRemainingSec by viewModel.missionTimeRemainingSec.collectAsStateWithLifecycle()
  val missionIncidents by viewModel.missionIncidents.collectAsStateWithLifecycle()
  val missionCelebrationMessage by viewModel.missionCelebrationMessage.collectAsStateWithLifecycle()
  val currentInterstitialAd by viewModel.currentInterstitialAd.collectAsStateWithLifecycle()

  // Railway Signs state (SLOW DOWN & SPEED UP)
  val activeRailwaySign by viewModel.activeRailwaySign.collectAsStateWithLifecycle()
  val approachingSigns by viewModel.approachingSigns.collectAsStateWithLifecycle()

  // Handle Android back button
  BackHandler(enabled = currentTab != ScreenTab.DRIVE) {
    viewModel.setTab(ScreenTab.DRIVE)
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    containerColor = Color(0xFF14191C),
    topBar = {
      ConductorTopHeader(
        currentWorld = currentWorld,
        weather = weather,
        goldenTickets = safeProfile.goldenTickets,
        starsCollected = safeProfile.starsCollected,
        currentTab = currentTab,
        cautionMessage = cautionMessage,
        rewardToast = rewardToast,
        onTabSelected = { viewModel.setTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        ScreenTab.DRIVE -> {
          // SPLIT SCREEN ARCHITECTURE
          Column(modifier = Modifier.fillMaxSize()) {

            // TOP SPLIT: Conductor Forward Cab Perspective (Looking out windshield)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1.22f)
            ) {
              ConductorPerspectiveView(
                currentWorld = currentWorld,
                weather = weather,
                isWipersOn = isWipersOn,
                isHeadlightOn = isHeadlightOn,
                speedMph = actualSpeedMph,
                trackPosition = trackPosition,
                currentSlope = currentSlope,
                currentCurvature = currentCurvature,
                approachingStation = approachingStation,
                nearbySecret = nearbySecret,
                onTapSecret = { viewModel.tapSecret(it) },
                approachingSigns = approachingSigns,
                isWrecked = isWrecked,
                dangerLevel = dangerLevel,
                onRerailTrain = { viewModel.rerailTrain() },
                modifier = Modifier.fillMaxSize()
              )
            }

            // MIDDLE DOCK: Conductor Controls (Speed notches, Whistle, Pause, etc.)
            ConductorControlPanel(
              speedNotch = speedNotch,
              actualSpeedMph = actualSpeedMph,
              currentSlope = currentSlope,
              currentCurvature = currentCurvature,
              weather = weather,
              isWipersOn = isWipersOn,
              isHeadlightOn = isHeadlightOn,
              isPaused = isPaused,
              approachingJunction = approachingJunction,
              selectedSwitchTurn = selectedSwitchTurn,
              onSetSpeedNotch = { viewModel.setSpeedNotch(it) },
              onPullWhistle = { viewModel.pullWhistle() },
              onTogglePause = { viewModel.togglePause() },
              onToggleWipers = { viewModel.toggleWipers() },
              onToggleHeadlight = { viewModel.toggleHeadlight() },
              onToggleSwitchTrack = { viewModel.toggleSwitchTrack() },
              activeRailwaySign = activeRailwaySign,
              isWrecked = isWrecked,
              onRerail = { viewModel.rerailTrain() },
              onSimulateWreck = { viewModel.simulateSpeedWreck() }
            )

            // BOTTOM SPLIT: Dynamic Live View of what the train looks like!
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(0.78f)
            ) {
              TrainAppearanceSideView(
                engineId = safeProfile.selectedEngineId,
                colorId = safeProfile.selectedColorId,
                smokeId = safeProfile.selectedSmokeId,
                currentWorld = currentWorld,
                actualSpeedMph = actualSpeedMph,
                trackPosition = trackPosition,
                cargoEmoji = if (missionState == MissionState.DELIVERING) activeMission?.cargoEmoji else null,
                isPaused = isPaused,
                isWrecked = isWrecked,
                dangerLevel = dangerLevel,
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
        ScreenTab.WORKSHOP -> {
          TrainCustomizationScreen(
            profile = safeProfile,
            onBuyAndEquipEngine = { id, cost -> viewModel.buyAndEquipEngine(id, cost) },
            onBuyAndEquipColor = { id, cost -> viewModel.buyAndEquipColor(id, cost) },
            onBuyAndEquipSmoke = { id, cost -> viewModel.buyAndEquipSmoke(id, cost) },
            onBackToDrive = { viewModel.setTab(ScreenTab.DRIVE) }
          )
        }
        ScreenTab.MISSIONS -> {
          SpecialDeliveryMissionsScreen(
            activeMission = activeMission,
            missionState = missionState,
            timeRemainingSec = missionTimeRemainingSec,
            incidentsCount = missionIncidents,
            onStartMission = { viewModel.startMission(it) },
            onCancelMission = { viewModel.cancelMission() },
            onBackToDrive = { viewModel.setTab(ScreenTab.DRIVE) },
            onWatchSponsorAd = { viewModel.watchSponsorAd() }
          )
        }
        ScreenTab.LOGBOOK -> {
          ConductorLogbookScreen(
            profile = safeProfile,
            onBackToDrive = { viewModel.setTab(ScreenTab.DRIVE) }
          )
        }
      }

      // Dialogs
      stationCelebration?.let { celebration ->
        StationCelebrationDialog(
          celebration = celebration,
          onDismiss = { viewModel.dismissStationCelebration() }
        )
      }

      discoveredSecretModal?.let { secret ->
        SecretDiscoveredDialog(
          secret = secret,
          onDismiss = { viewModel.dismissSecretModal() }
        )
      }

      missionCelebrationMessage?.let { msg ->
        MissionCelebrationDialog(
          message = msg,
          onDismiss = { viewModel.dismissMissionCelebration() }
        )
      }

      // Interstitial Ad Dialog between missions
      currentInterstitialAd?.let { ad ->
        MissionInterstitialAdDialog(
          ad = ad,
          onDismiss = { viewModel.dismissInterstitialAd(false) },
          onClaimReward = { viewModel.dismissInterstitialAd(true) }
        )
      }

      // Train Wreck / Derailment Dialog
      currentWreck?.let { wreck ->
        TrainWreckDialog(
          wreck = wreck,
          onRerail = { viewModel.rerailTrain() },
          onTowToStation = { viewModel.towToNearestStation() }
        )
      }
    }
  }
}

