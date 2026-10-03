package com.example.model

data class MissionAd(
  val id: String,
  val title: String,
  val tagline: String,
  val description: String,
  val category: String,
  val rating: Float,
  val reviewCount: String,
  val downloads: String,
  val iconEmoji: String,
  val accentColorHex: Long,
  val backgroundColorHex: Long,
  val callToAction: String = "INSTALL FREE",
  val rewardTickets: Int = 5,
  val sponsorName: String = "SteamWorks Kids Studio"
)

object MissionAdCatalog {
  val ads: List<MissionAd> = listOf(
    MissionAd(
      id = "rail_tycoon_3d",
      title = "Rail Tycoon 3D: Mega Tracks",
      tagline = "Build bridges, lay switches & manage 100+ trains!",
      description = "Construct gigantic rail systems through towering mountains and bustling cities. Lay curved tracks and customize locomotives!",
      category = "Featured Simulation",
      rating = 4.9f,
      reviewCount = "142K reviews",
      downloads = "10M+ Downloads",
      iconEmoji = "🚂",
      accentColorHex = 0xFFFFB300,
      backgroundColorHex = 0xFF1A237E,
      callToAction = "PLAY FREE 📲",
      rewardTickets = 5,
      sponsorName = "PlayRail Studios"
    ),
    MissionAd(
      id = "dino_safari_rail",
      title = "Dino Canyon Safari Express",
      tagline = "Drive past T-Rex & deliver dino eggs safely!",
      description = "Experience ancient prehistoric tracks! Spot friendly brachiosaurs and build giant fossil bridges.",
      category = "Top Adventure Game",
      rating = 4.8f,
      reviewCount = "89K reviews",
      downloads = "5M+ Downloads",
      iconEmoji = "🦕",
      accentColorHex = 0xFF00E676,
      backgroundColorHex = 0xFF1B5E20,
      callToAction = "EXPLORE NOW 🌴",
      rewardTickets = 5,
      sponsorName = "Jurassic Rail Games"
    ),
    MissionAd(
      id = "candy_express_rush",
      title = "Sugar Valley: Candy Choo-Choo",
      tagline = "Deliver giant gumdrops & lollipop cargo!",
      description = "Glide through chocolate rivers and peppermint groves in the sweetest railway adventure ever made.",
      category = "Casual & Family",
      rating = 4.9f,
      reviewCount = "210K reviews",
      downloads = "15M+ Downloads",
      iconEmoji = "🍭",
      accentColorHex = 0xFFFF4081,
      backgroundColorHex = 0xFF4A148C,
      callToAction = "GET CANDY TRAIN 🧁",
      rewardTickets = 5,
      sponsorName = "SweetByte Interactive"
    ),
    MissionAd(
      id = "bullet_maglev_speed",
      title = "HyperRail 3000: Supersonic",
      tagline = "Hit 300 MPH on magnetic levitation rails!",
      description = "Futuristic high-speed train racing across neon skyscrapers and glowing tunnels with realistic G-force physics.",
      category = "Action & Racing",
      rating = 4.7f,
      reviewCount = "75K reviews",
      downloads = "3M+ Downloads",
      iconEmoji = "⚡",
      accentColorHex = 0xFF00E5FF,
      backgroundColorHex = 0xFF006064,
      callToAction = "RACE AT 300 MPH 🚀",
      rewardTickets = 5,
      sponsorName = "NeoSpeed Labs"
    ),
    MissionAd(
      id = "winter_bear_express",
      title = "Alpine Bear Snowline",
      tagline = "Cozy passenger runs through winter blizzards!",
      description = "Warm up the boiler and haul steaming hot cocoa to friendly polar bears on the snowy summit peaks.",
      category = "Cozy Games",
      rating = 4.9f,
      reviewCount = "64K reviews",
      downloads = "4M+ Downloads",
      iconEmoji = "🐻",
      accentColorHex = 0xFFFFD54F,
      backgroundColorHex = 0xFF0D47A1,
      callToAction = "JOIN THE CREW ☕",
      rewardTickets = 5,
      sponsorName = "FrostyTrack Games"
    )
  )

  private var adIndex = 0

  fun getNextAd(): MissionAd {
    val ad = ads[adIndex % ads.size]
    adIndex = (adIndex + 1) % ads.size
    return ad
  }
}
