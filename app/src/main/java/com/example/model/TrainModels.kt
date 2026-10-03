package com.example.model

import androidx.compose.ui.graphics.Color

enum class WeatherCondition(
  val displayName: String,
  val iconEmoji: String,
  val frictionMultiplier: Float, // Slicker rails = less friction = higher downhill speed, longer stop
  val visibilityAlpha: Float
) {
  CLEAR("Clear Skies", "☀️", 1.0f, 1.0f),
  LIGHT_RAIN("Light Rain", "🌧️", 1.45f, 0.82f),
  GENTLE_SNOW("Gentle Snow", "❄️", 1.25f, 0.78f)
}

enum class SpeedNotch(val value: Int, val label: String, val baseSpeedMph: Float) {
  REVERSE(-1, "REV", -14f),
  STOP(0, "STOP", 0f),
  SPEED_1(1, "1", 16f),
  SPEED_2(2, "2", 32f),
  SPEED_3(3, "3", 52f)
}

enum class TrackSlope(val label: String, val gravityFactor: Float, val icon: String) {
  LEVEL("Level Track", 0.0f, "━"),
  UPHILL_GENTLE("Gentle Climb", -0.35f, "↗"),
  UPHILL_STEEP("Steep Hill Up!", -0.75f, "⬆"),
  DOWNHILL_GENTLE("Gentle Downhill", 0.45f, "↘"),
  DOWNHILL_STEEP("Steep Downhill! Watch Speed!", 0.90f, "⬇")
}

enum class TrackCurvature(val label: String, val curveOffset: Float, val maxSafeSpeed: Float) {
  STRAIGHT("Straight Track", 0.0f, 55f),
  CURVE_GENTLE_LEFT("Gentle Curve Left", -0.3f, 42f),
  CURVE_SHARP_LEFT("Sharp Curve Left! ⚠️", -0.7f, 25f),
  CURVE_GENTLE_RIGHT("Gentle Curve Right", 0.3f, 42f),
  CURVE_SHARP_RIGHT("Sharp Curve Right! ⚠️", 0.7f, 25f)
}

enum class LandscapeWorld(
  val id: String,
  val title: String,
  val subtitle: String,
  val skyColorDay: Color,
  val groundColor: Color,
  val accentColor: Color,
  val iconEmoji: String
) {
  FOREST(
    id = "forest",
    title = "Whispering Pines",
    subtitle = "Evergreen trees, peaceful rivers & cabins",
    skyColorDay = Color(0xFF81D4FA),
    groundColor = Color(0xFF66BB6A),
    accentColor = Color(0xFF2E7D32),
    iconEmoji = "🌲"
  ),
  CANDY(
    id = "candy",
    title = "Sugar Candy Valley",
    subtitle = "Giant lollipops & marshmallow hills",
    skyColorDay = Color(0xFFFFD1DC),
    groundColor = Color(0xFFF48FB1),
    accentColor = Color(0xFFAD1457),
    iconEmoji = "🍭"
  ),
  DINO(
    id = "dino",
    title = "Dino Canyon",
    subtitle = "Ancient fossils, rock arches & volcanos",
    skyColorDay = Color(0xFFFFCC80),
    groundColor = Color(0xFFA1887F),
    accentColor = Color(0xFFE65100),
    iconEmoji = "🦕"
  ),
  COASTAL(
    id = "coastal",
    title = "Sunlit Coastline",
    subtitle = "Ocean waves, jumping dolphins & lighthouse",
    skyColorDay = Color(0xFF80DEEA),
    groundColor = Color(0xFFFFE082),
    accentColor = Color(0xFF00838F),
    iconEmoji = "🐬"
  ),
  SNOWY(
    id = "snowy",
    title = "Alpine Wonderland",
    subtitle = "Sparkling snowpeaks, ice caves & pine trees",
    skyColorDay = Color(0xFFB0BEC5),
    groundColor = Color(0xFFECEFF1),
    accentColor = Color(0xFF1565C0),
    iconEmoji = "⛄"
  )
}

data class TrainEngineDesign(
  val id: String,
  val name: String,
  val description: String,
  val cost: Int,
  val iconEmoji: String
)

data class TrainColorOption(
  val id: String,
  val name: String,
  val color: Color,
  val secondaryColor: Color,
  val cost: Int
)

enum class SmokeType {
  PUFFS,
  BUBBLES,
  HEARTS,
  STARS,
  RAINBOW,
  MUSIC,
  SPARKLES,
  FIRE
}

data class SmokeEffect(
  val id: String,
  val name: String,
  val iconEmoji: String,
  val cost: Int,
  val type: SmokeType = SmokeType.PUFFS,
  val primaryColor: Color = Color.White,
  val secondaryColor: Color = Color(0xFFCFD8DC),
  val description: String = ""
)

data class StationInfo(
  val id: String,
  val name: String,
  val world: LandscapeWorld,
  val trackPosition: Float,
  val passengersWaiting: List<String>, // Emojis of cute animals
  val ticketReward: Int
)

data class SecretDiscovery(
  val id: String,
  val name: String,
  val description: String,
  val world: LandscapeWorld,
  val trackPosition: Float,
  val iconEmoji: String,
  val rewardTickets: Int
)

data class TrackSegment(
  val startPos: Float,
  val endPos: Float,
  val slope: TrackSlope,
  val curvature: TrackCurvature,
  val junctionTargetWorld: LandscapeWorld? = null,
  val junctionChoiceName: String? = null
)

data class SpecialDeliveryMission(
  val id: String,
  val title: String,
  val cargoName: String,
  val cargoEmoji: String,
  val pickupStationId: String,
  val pickupStationName: String,
  val dropoffStationId: String,
  val dropoffStationName: String,
  val timeLimitSec: Int,
  val maxIncidents: Int,
  val rewardTickets: Int,
  val rewardStars: Int,
  val description: String
)

enum class MissionState {
  IDLE,
  PICKUP_READY,
  DELIVERING,
  COMPLETED,
  FAILED
}

enum class RailwaySignType {
  SLOW_DOWN,
  SPEED_UP
}

data class RailwaySign(
  val id: String,
  val world: LandscapeWorld,
  val trackPosition: Float,
  val type: RailwaySignType,
  val iconEmoji: String,
  val title: String,
  val subtitle: String,
  val targetSpeedText: String,
  val targetSpeedMph: Float
)

data class TrainWreckEvent(
  val speedMph: Float,
  val maxSafeSpeedMph: Float,
  val reasonTitle: String,
  val incidentDetail: String,
  val safetyTip: String,
  val world: LandscapeWorld,
  val trackSlope: TrackSlope,
  val trackCurvature: TrackCurvature,
  val timestampMs: Long = System.currentTimeMillis()
)

