package com.example.model

import androidx.compose.ui.graphics.Color

object TrackWorldData {

  val availableEngines = listOf(
    TrainEngineDesign(
      id = "classic_steam",
      name = "Choo-Choo Classic",
      description = "Beloved steam locomotive with glowing firebox and brass bell",
      cost = 0,
      iconEmoji = "🚂"
    ),
    TrainEngineDesign(
      id = "streamliner",
      name = "Silver Bullet",
      description = "Ultra sleek bullet train with aerodynamic chrome nose",
      cost = 100,
      iconEmoji = "🚄"
    ),
    TrainEngineDesign(
      id = "dino_express",
      name = "Dino Roarer",
      description = "Prehistoric powerhouse engine with dino spikes & glowing eyes",
      cost = 180,
      iconEmoji = "🦖"
    ),
    TrainEngineDesign(
      id = "caterpillar",
      name = "Caterpillar Express",
      description = "Cheerful cartoon crawler with bouncy antennae and smiles",
      cost = 240,
      iconEmoji = "🐛"
    ),
    TrainEngineDesign(
      id = "golden_royal",
      name = "Royal Monarch",
      description = "Majestic gold-trimmed locomotive fit for a King or Queen Conductor",
      cost = 320,
      iconEmoji = "👑"
    )
  )

  val availableColors = listOf(
    TrainColorOption("red", "Cherry Red", Color(0xFFE53935), Color(0xFFC62828), 0),
    TrainColorOption("blue", "Ocean Azure", Color(0xFF0288D1), Color(0xFF01579B), 40),
    TrainColorOption("yellow", "Sunburst Yellow", Color(0xFFFFB300), Color(0xFFFFA000), 60),
    TrainColorOption("green", "Emerald Forest", Color(0xFF43A047), Color(0xFF2E7D32), 75),
    TrainColorOption("pink", "Bubblegum Pop", Color(0xFFEC407A), Color(0xFFC2185B), 90),
    TrainColorOption("purple", "Cosmic Violet", Color(0xFF7E57C2), Color(0xFF512DA8), 120),
    TrainColorOption("rainbow", "Rainbow Sparkle", Color(0xFFFF7043), Color(0xFF26C6DA), 160)
  )

  val availableSmokes = listOf(
    SmokeEffect("classic_puffs", "Cloud Puffs", "☁️", 0),
    SmokeEffect("heart_puffs", "Loving Hearts", "💖", 60),
    SmokeEffect("star_puffs", "Sparkling Stars", "⭐", 90),
    SmokeEffect("rainbow_puffs", "Rainbow Mist", "🌈", 140)
  )

  val allSecrets = listOf(
    SecretDiscovery(
      id = "secret_forest_owl",
      name = "Singing Barn Owl",
      description = "A friendly golden owl perched atop a hollow oak tree chanting conductor tunes!",
      world = LandscapeWorld.FOREST,
      trackPosition = 75f,
      iconEmoji = "🦉",
      rewardTickets = 30
    ),
    SecretDiscovery(
      id = "secret_forest_treehouse",
      name = "Secret Forest Treehouse",
      description = "A magical woodland treehouse with rope swings and waving woodland critters!",
      world = LandscapeWorld.FOREST,
      trackPosition = 230f,
      iconEmoji = "🏡",
      rewardTickets = 45
    ),
    SecretDiscovery(
      id = "secret_candy_bear",
      name = "Giant Gummy Bear",
      description = "A friendly towering gummy bear taking a nap in a bowl of sprinkles!",
      world = LandscapeWorld.CANDY,
      trackPosition = 90f,
      iconEmoji = "🧸",
      rewardTickets = 35
    ),
    SecretDiscovery(
      id = "secret_candy_windmill",
      name = "Lollipop Windmill",
      description = "A spinning windmill made entirely of swirling peppermint lollipops!",
      world = LandscapeWorld.CANDY,
      trackPosition = 240f,
      iconEmoji = "🍭",
      rewardTickets = 45
    ),
    SecretDiscovery(
      id = "secret_dino_egg",
      name = "Hatching Dinosaur Egg",
      description = "A glowing speckle egg cracking open to reveal a baby Triceratops!",
      world = LandscapeWorld.DINO,
      trackPosition = 85f,
      iconEmoji = "🥚",
      rewardTickets = 40
    ),
    SecretDiscovery(
      id = "secret_dino_crystal",
      name = "Glowing Amber Crystal",
      description = "A giant ancient amber geode radiating warm prehistoric light!",
      world = LandscapeWorld.DINO,
      trackPosition = 220f,
      iconEmoji = "💎",
      rewardTickets = 50
    ),
    SecretDiscovery(
      id = "secret_coastal_dolphin",
      name = "Jumping Dolphin Family",
      description = "Three playful dolphins leaping over ocean waves right beside the rail!",
      world = LandscapeWorld.COASTAL,
      trackPosition = 110f,
      iconEmoji = "🐬",
      rewardTickets = 35
    ),
    SecretDiscovery(
      id = "secret_coastal_chest",
      name = "Sunken Pirate Chest",
      description = "An ancient treasure chest filled with gold dubloons washed ashore!",
      world = LandscapeWorld.COASTAL,
      trackPosition = 250f,
      iconEmoji = "🪙",
      rewardTickets = 50
    ),
    SecretDiscovery(
      id = "secret_snowy_yeti",
      name = "Hot-Cocoa Yeti",
      description = "A gentle giant yeti enjoying a steamy mug of hot chocolate with marshmallows!",
      world = LandscapeWorld.SNOWY,
      trackPosition = 95f,
      iconEmoji = "☕",
      rewardTickets = 40
    ),
    SecretDiscovery(
      id = "secret_snowy_igloo",
      name = "Sparkling Ice Palace",
      description = "An ice castle illuminated by dancing Aurora Borealis polar lights!",
      world = LandscapeWorld.SNOWY,
      trackPosition = 260f,
      iconEmoji = "🏰",
      rewardTickets = 55
    )
  )

  val allStations = listOf(
    StationInfo(
      id = "station_forest_pine",
      name = "Pinecone Valley Depot",
      world = LandscapeWorld.FOREST,
      trackPosition = 160f,
      passengersWaiting = listOf("🐻", "🐰", "🦔"),
      ticketReward = 40
    ),
    StationInfo(
      id = "station_candy_crest",
      name = "Marshmallow Central",
      world = LandscapeWorld.CANDY,
      trackPosition = 170f,
      passengersWaiting = listOf("🐱", "🐶", "🐼"),
      ticketReward = 45
    ),
    StationInfo(
      id = "station_dino_depot",
      name = "Fossil Ridge Station",
      world = LandscapeWorld.DINO,
      trackPosition = 165f,
      passengersWaiting = listOf("🦕", "🦖", "🦊"),
      ticketReward = 50
    ),
    StationInfo(
      id = "station_coastal_bay",
      name = "Seagull Cove Pier",
      world = LandscapeWorld.COASTAL,
      trackPosition = 180f,
      passengersWaiting = listOf("🦁", "🐵", "🐧"),
      ticketReward = 45
    ),
    StationInfo(
      id = "station_snowy_summit",
      name = "Frosty Peak Terminal",
      world = LandscapeWorld.SNOWY,
      trackPosition = 190f,
      passengersWaiting = listOf("⛄", "🐻‍❄️", "🐧"),
      ticketReward = 50
    )
  )

  val allMissions = listOf(
    SpecialDeliveryMission(
      id = "mission_ice_cream",
      title = "Ice Cream Express",
      cargoName = "Giant Ice Cream Sundae",
      cargoEmoji = "🍦",
      pickupStationId = "station_forest_pine",
      pickupStationName = "Pinecone Valley Depot",
      dropoffStationId = "station_candy_crest",
      dropoffStationName = "Marshmallow Central",
      timeLimitSec = 90,
      maxIncidents = 2,
      rewardTickets = 120,
      rewardStars = 2,
      description = "Deliver delicious ice cream to Candy Valley before it melts! Keep speed smooth on curves."
    ),
    SpecialDeliveryMission(
      id = "mission_dino_egg",
      title = "Fragile Dino Egg",
      cargoName = "Ancient Dino Egg",
      cargoEmoji = "🥚",
      pickupStationId = "station_dino_depot",
      pickupStationName = "Fossil Ridge Station",
      dropoffStationId = "station_forest_pine",
      dropoffStationName = "Pinecone Valley Depot",
      timeLimitSec = 100,
      maxIncidents = 1,
      rewardTickets = 150,
      rewardStars = 3,
      description = "Carefully escort a rare dinosaur egg to the forest sanctuary! Zero reckless speeding allowed!"
    ),
    SpecialDeliveryMission(
      id = "mission_royal_cake",
      title = "Royal Birthday Cake",
      cargoName = "5-Tier Strawberry Cake",
      cargoEmoji = "🎂",
      pickupStationId = "station_candy_crest",
      pickupStationName = "Marshmallow Central",
      dropoffStationId = "station_snowy_summit",
      dropoffStationName = "Frosty Peak Terminal",
      timeLimitSec = 110,
      maxIncidents = 2,
      rewardTickets = 180,
      rewardStars = 3,
      description = "Rush the majestic birthday cake to the mountain peak without tipping the frosting over!"
    ),
    SpecialDeliveryMission(
      id = "mission_pirate_gold",
      title = "Pirate Sunken Chest",
      cargoName = "Golden Pirate Dubloons",
      cargoEmoji = "🪙",
      pickupStationId = "station_coastal_bay",
      pickupStationName = "Seagull Cove Pier",
      dropoffStationId = "station_dino_depot",
      dropoffStationName = "Fossil Ridge Station",
      timeLimitSec = 120,
      maxIncidents = 2,
      rewardTickets = 200,
      rewardStars = 4,
      description = "Transport the historic gold chest safely past steep hills and tight coastal bends!"
    )
  )

  val allRailwaySigns = listOf(
    // FOREST SIGNS
    RailwaySign("sign_f_speed_1", LandscapeWorld.FOREST, 30f, RailwaySignType.SPEED_UP, "🚀", "SPEED UP", "Clear Straight Tracks", "RESUME 35 MPH", 35f),
    RailwaySign("sign_f_slow_station", LandscapeWorld.FOREST, 135f, RailwaySignType.SLOW_DOWN, "🛑", "SLOW DOWN", "Pinecone Depot Ahead", "STOP ZONE 10 MPH", 10f),
    RailwaySign("sign_f_slow_curve", LandscapeWorld.FOREST, 172f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Sharp Curve Left Ahead", "MAX 20 MPH", 20f),
    RailwaySign("sign_f_slow_downhill", LandscapeWorld.FOREST, 212f, RailwaySignType.SLOW_DOWN, "⬇️", "SLOW DOWN", "Steep Downhill Ahead", "SLOW DOWN 25 MPH", 25f),
    RailwaySign("sign_f_speed_2", LandscapeWorld.FOREST, 320f, RailwaySignType.SPEED_UP, "🟢", "SPEED UP", "Open Forest Valley Stretch", "FULL STEAM 45 MPH", 45f),

    // CANDY SIGNS
    RailwaySign("sign_c_speed_1", LandscapeWorld.CANDY, 20f, RailwaySignType.SPEED_UP, "🟢", "SPEED UP", "Sugar Strip Runway", "RESUME 35 MPH", 35f),
    RailwaySign("sign_c_slow_drop", LandscapeWorld.CANDY, 102f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Steep Gummy Descent Ahead", "MAX 20 MPH", 20f),
    RailwaySign("sign_c_slow_station", LandscapeWorld.CANDY, 145f, RailwaySignType.SLOW_DOWN, "🛑", "SLOW DOWN", "Marshmallow Central Ahead", "PREPARE TO STOP", 10f),
    RailwaySign("sign_c_speed_2", LandscapeWorld.CANDY, 192f, RailwaySignType.SPEED_UP, "🚀", "SPEED UP", "Peppermint Straightaway", "ACCELERATE 40 MPH", 40f),
    RailwaySign("sign_c_slow_curve", LandscapeWorld.CANDY, 232f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Licorice Bend Sharp Turn", "MAX 20 MPH", 20f),

    // DINO SIGNS
    RailwaySign("sign_d_slow_curve", LandscapeWorld.DINO, 42f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Rocky Ridge Sharp Curve", "MAX 20 MPH", 20f),
    RailwaySign("sign_d_slow_drop", LandscapeWorld.DINO, 92f, RailwaySignType.SLOW_DOWN, "⬇️", "SLOW DOWN", "Canyon Gorge Drop Ahead", "SLOW DOWN 25 MPH", 25f),
    RailwaySign("sign_d_slow_station", LandscapeWorld.DINO, 140f, RailwaySignType.SLOW_DOWN, "🛑", "SLOW DOWN", "Fossil Ridge Station Ahead", "PREPARE TO STOP", 10f),
    RailwaySign("sign_d_slow_turn", LandscapeWorld.DINO, 178f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "T-Rex Cavern Sharp Turn", "MAX 20 MPH", 20f),
    RailwaySign("sign_d_speed", LandscapeWorld.DINO, 285f, RailwaySignType.SPEED_UP, "🟢", "SPEED UP", "Flat Canyon Clearing Ahead", "FULL STEAM 45 MPH", 45f),

    // COASTAL SIGNS
    RailwaySign("sign_co_speed_1", LandscapeWorld.COASTAL, 25f, RailwaySignType.SPEED_UP, "🟢", "SPEED UP", "Ocean Boardwalk Straight", "CRUISE 35 MPH", 35f),
    RailwaySign("sign_co_slow_cliff", LandscapeWorld.COASTAL, 96f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Cliffside Sharp Bend", "MAX 20 MPH", 20f),
    RailwaySign("sign_co_slow_station", LandscapeWorld.COASTAL, 155f, RailwaySignType.SLOW_DOWN, "🛑", "SLOW DOWN", "Seagull Cove Pier Ahead", "PREPARE TO STOP", 10f),
    RailwaySign("sign_co_slow_drop", LandscapeWorld.COASTAL, 252f, RailwaySignType.SLOW_DOWN, "⬇️", "SLOW DOWN", "Pier Drop Ahead", "SLOW DOWN 25 MPH", 25f),
    RailwaySign("sign_co_speed_2", LandscapeWorld.COASTAL, 345f, RailwaySignType.SPEED_UP, "🚀", "SPEED UP", "Coastal Highway Ahead", "FULL STEAM 45 MPH", 45f),

    // SNOWY SIGNS
    RailwaySign("sign_s_slow_curve", LandscapeWorld.SNOWY, 40f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Sharp Glacier Bend Ahead", "MAX 20 MPH", 20f),
    RailwaySign("sign_s_slow_ice", LandscapeWorld.SNOWY, 92f, RailwaySignType.SLOW_DOWN, "❄️", "SLOW DOWN", "Slick Ice Slide Ahead", "SLOW DOWN 20 MPH", 20f),
    RailwaySign("sign_s_slow_station", LandscapeWorld.SNOWY, 165f, RailwaySignType.SLOW_DOWN, "🛑", "SLOW DOWN", "Frosty Peak Terminal Ahead", "PREPARE TO STOP", 10f),
    RailwaySign("sign_s_slow_turn", LandscapeWorld.SNOWY, 205f, RailwaySignType.SLOW_DOWN, "⚠️", "SLOW DOWN", "Avalanche Curve Right Ahead", "MAX 20 MPH", 20f),
    RailwaySign("sign_s_speed", LandscapeWorld.SNOWY, 315f, RailwaySignType.SPEED_UP, "🟢", "SPEED UP", "Snowfield Straightaway Ahead", "FULL STEAM 40 MPH", 40f)
  )

  /**
   * Track sections for a given world loop (track loops around from 0 to 360 units).
   * Generates varied slopes (uphills, steep downhills) and curves (gentle and sharp).
   */
  fun getTrackSegmentsForWorld(world: LandscapeWorld): List<TrackSegment> {
    return when (world) {
      LandscapeWorld.FOREST -> listOf(
        TrackSegment(0f, 50f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT),
        TrackSegment(50f, 90f, TrackSlope.UPHILL_GENTLE, TrackCurvature.CURVE_GENTLE_RIGHT),
        TrackSegment(90f, 130f, TrackSlope.DOWNHILL_GENTLE, TrackCurvature.STRAIGHT),
        TrackSegment(130f, 180f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT), // Station is at 160
        TrackSegment(180f, 220f, TrackSlope.UPHILL_STEEP, TrackCurvature.CURVE_SHARP_LEFT),
        TrackSegment(220f, 270f, TrackSlope.DOWNHILL_STEEP, TrackCurvature.CURVE_GENTLE_RIGHT), // steep downhill challenge!
        TrackSegment(270f, 320f, TrackSlope.LEVEL, TrackCurvature.CURVE_SHARP_RIGHT, junctionTargetWorld = LandscapeWorld.CANDY, junctionChoiceName = "Candy Valley Switch"),
        TrackSegment(320f, 360f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT)
      )
      LandscapeWorld.CANDY -> listOf(
        TrackSegment(0f, 60f, TrackSlope.LEVEL, TrackCurvature.CURVE_GENTLE_LEFT),
        TrackSegment(60f, 110f, TrackSlope.UPHILL_STEEP, TrackCurvature.STRAIGHT),
        TrackSegment(110f, 150f, TrackSlope.DOWNHILL_STEEP, TrackCurvature.CURVE_SHARP_LEFT), // fast sugary descent!
        TrackSegment(150f, 190f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT), // Station at 170
        TrackSegment(190f, 240f, TrackSlope.UPHILL_GENTLE, TrackCurvature.CURVE_GENTLE_RIGHT),
        TrackSegment(240f, 290f, TrackSlope.DOWNHILL_GENTLE, TrackCurvature.CURVE_SHARP_RIGHT),
        TrackSegment(290f, 340f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT, junctionTargetWorld = LandscapeWorld.DINO, junctionChoiceName = "Dino Canyon Switch"),
        TrackSegment(340f, 360f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT)
      )
      LandscapeWorld.DINO -> listOf(
        TrackSegment(0f, 50f, TrackSlope.UPHILL_GENTLE, TrackCurvature.STRAIGHT),
        TrackSegment(50f, 100f, TrackSlope.UPHILL_STEEP, TrackCurvature.CURVE_SHARP_RIGHT), // Rocky climb!
        TrackSegment(100f, 145f, TrackSlope.DOWNHILL_STEEP, TrackCurvature.CURVE_GENTLE_LEFT), // Gorge drop!
        TrackSegment(145f, 185f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT), // Station at 165
        TrackSegment(185f, 230f, TrackSlope.LEVEL, TrackCurvature.CURVE_SHARP_LEFT),
        TrackSegment(230f, 280f, TrackSlope.DOWNHILL_GENTLE, TrackCurvature.CURVE_GENTLE_RIGHT),
        TrackSegment(280f, 330f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT, junctionTargetWorld = LandscapeWorld.COASTAL, junctionChoiceName = "Coastline Switch"),
        TrackSegment(330f, 360f, TrackSlope.LEVEL, TrackCurvature.CURVE_GENTLE_LEFT)
      )
      LandscapeWorld.COASTAL -> listOf(
        TrackSegment(0f, 60f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT),
        TrackSegment(60f, 105f, TrackSlope.UPHILL_GENTLE, TrackCurvature.CURVE_GENTLE_RIGHT),
        TrackSegment(105f, 155f, TrackSlope.DOWNHILL_GENTLE, TrackCurvature.CURVE_SHARP_RIGHT), // Cliffside curve!
        TrackSegment(155f, 205f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT), // Station at 180
        TrackSegment(205f, 260f, TrackSlope.UPHILL_STEEP, TrackCurvature.STRAIGHT), // Pier ascent
        TrackSegment(260f, 305f, TrackSlope.DOWNHILL_STEEP, TrackCurvature.CURVE_GENTLE_LEFT),
        TrackSegment(305f, 345f, TrackSlope.LEVEL, TrackCurvature.CURVE_SHARP_LEFT, junctionTargetWorld = LandscapeWorld.SNOWY, junctionChoiceName = "Alpine Summit Switch"),
        TrackSegment(345f, 360f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT)
      )
      LandscapeWorld.SNOWY -> listOf(
        TrackSegment(0f, 50f, TrackSlope.UPHILL_STEEP, TrackCurvature.CURVE_GENTLE_LEFT), // Glacier climb!
        TrackSegment(50f, 100f, TrackSlope.UPHILL_STEEP, TrackCurvature.CURVE_SHARP_LEFT),
        TrackSegment(100f, 160f, TrackSlope.DOWNHILL_STEEP, TrackCurvature.STRAIGHT), // Slick snow slide!
        TrackSegment(160f, 210f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT), // Station at 190
        TrackSegment(210f, 265f, TrackSlope.LEVEL, TrackCurvature.CURVE_SHARP_RIGHT),
        TrackSegment(265f, 310f, TrackSlope.DOWNHILL_GENTLE, TrackCurvature.CURVE_GENTLE_RIGHT),
        TrackSegment(310f, 350f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT, junctionTargetWorld = LandscapeWorld.FOREST, junctionChoiceName = "Forest Valley Switch"),
        TrackSegment(350f, 360f, TrackSlope.LEVEL, TrackCurvature.STRAIGHT)
      )
    }
  }

  fun getSegmentAt(world: LandscapeWorld, pos: Float): TrackSegment {
    val normPos = ((pos % 360f) + 360f) % 360f
    val segments = getTrackSegmentsForWorld(world)
    return segments.firstOrNull { normPos >= it.startPos && normPos < it.endPos }
      ?: segments.first()
  }
}
