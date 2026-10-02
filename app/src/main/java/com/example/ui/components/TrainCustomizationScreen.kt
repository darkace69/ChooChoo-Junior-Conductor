package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConductorProfileEntity
import com.example.model.LandscapeWorld
import com.example.model.TrackWorldData

@Composable
fun TrainCustomizationScreen(
  profile: ConductorProfileEntity,
  onBuyAndEquipEngine: (String, Int) -> Unit,
  onBuyAndEquipColor: (String, Int) -> Unit,
  onBuyAndEquipSmoke: (String, Int) -> Unit,
  onBackToDrive: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Engines, 1: Colors, 2: Smoke

  val infiniteTransition = rememberInfiniteTransition(label = "workshop_turntable")
  val previewTrackPos by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(10000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "preview_pos"
  )

  val unlockedEngines = remember(profile.unlockedEnginesCsv) {
    profile.unlockedEnginesCsv.split(",").filter { it.isNotBlank() }.toSet()
  }
  val unlockedColors = remember(profile.unlockedColorsCsv) {
    profile.unlockedColorsCsv.split(",").filter { it.isNotBlank() }.toSet()
  }
  val unlockedSmokes = remember(profile.unlockedSmokesCsv) {
    profile.unlockedSmokesCsv.split(",").filter { it.isNotBlank() }.toSet()
  }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = Color(0xFF192024)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(14.dp)
    ) {
      // Header: Workshop Title + Tickets & Stars
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "🛠️ Train Workshop",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "Upgrade your train's appearance & smoke!",
            color = Color(0xFFB0BEC5),
            fontSize = 11.sp
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Tickets badge
          Row(
            modifier = Modifier
              .background(Color(0xFF263238), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🎟️", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${profile.goldenTickets}",
              color = Color(0xFFFFD54F),
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
          }

          // Stars badge
          Row(
            modifier = Modifier
              .background(Color(0xFF263238), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFF81D4FA), RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "⭐", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${profile.starsCollected}",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Turntable Live Preview
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14191C)),
        shape = RoundedCornerShape(14.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          TrainAppearanceSideView(
            engineId = profile.selectedEngineId,
            colorId = profile.selectedColorId,
            smokeId = profile.selectedSmokeId,
            currentWorld = LandscapeWorld.FOREST,
            actualSpeedMph = 14f,
            trackPosition = previewTrackPos,
            cargoEmoji = null,
            isPaused = false,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Tabs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          label = { Text("🚂 Engine Shapes", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF0288D1),
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("tab_engines")
        )
        FilterChip(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          label = { Text("🎨 Paint Colors", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFE53935),
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("tab_colors")
        )
        FilterChip(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          label = { Text("💨 Smoke Styles", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF7E57C2),
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("tab_smoke")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Content for selected category
      when (selectedTab) {
        0 -> {
          // Engine Models
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TrackWorldData.availableEngines.forEach { engine ->
              val isEquipped = profile.selectedEngineId == engine.id
              val isUnlocked = unlockedEngines.contains(engine.id)
              val canAfford = profile.goldenTickets >= engine.cost

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .border(
                    width = if (isEquipped) 2.dp else 1.dp,
                    color = if (isEquipped) Color(0xFFFFD54F) else Color(0xFF37474F),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    if (isUnlocked || canAfford) {
                      onBuyAndEquipEngine(engine.id, engine.cost)
                    }
                  },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = engine.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = engine.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = engine.description,
                        color = Color(0xFFB0BEC5),
                        fontSize = 10.sp
                      )
                    }
                  }

                  // Action Badge
                  Box(
                    modifier = Modifier
                      .background(
                        when {
                          isEquipped -> Color(0xFF43A047)
                          isUnlocked -> Color(0xFF0288D1)
                          canAfford -> Color(0xFFFFB300)
                          else -> Color(0xFF455A64)
                        },
                        RoundedCornerShape(8.dp)
                      )
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = when {
                        isEquipped -> "EQUIPPED"
                        isUnlocked -> "USE"
                        else -> "🎟️ ${engine.cost}"
                      },
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }
        1 -> {
          // Paint Colors
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TrackWorldData.availableColors.forEach { colorOpt ->
              val isEquipped = profile.selectedColorId == colorOpt.id
              val isUnlocked = unlockedColors.contains(colorOpt.id)
              val canAfford = profile.goldenTickets >= colorOpt.cost

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .border(
                    width = if (isEquipped) 2.dp else 1.dp,
                    color = if (isEquipped) Color(0xFFFFD54F) else Color(0xFF37474F),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    if (isUnlocked || canAfford) {
                      onBuyAndEquipColor(colorOpt.id, colorOpt.cost)
                    }
                  },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .background(colorOpt.color, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                      text = colorOpt.name,
                      color = Color.White,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Box(
                    modifier = Modifier
                      .background(
                        when {
                          isEquipped -> Color(0xFF43A047)
                          isUnlocked -> Color(0xFF0288D1)
                          canAfford -> Color(0xFFFFB300)
                          else -> Color(0xFF455A64)
                        },
                        RoundedCornerShape(8.dp)
                      )
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = when {
                        isEquipped -> "EQUIPPED"
                        isUnlocked -> "USE"
                        else -> "🎟️ ${colorOpt.cost}"
                      },
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }
        2 -> {
          // Smoke Styles
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TrackWorldData.availableSmokes.forEach { smoke ->
              val isEquipped = profile.selectedSmokeId == smoke.id
              val isUnlocked = unlockedSmokes.contains(smoke.id)
              val canAfford = profile.goldenTickets >= smoke.cost

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .border(
                    width = if (isEquipped) 2.dp else 1.dp,
                    color = if (isEquipped) Color(0xFFFFD54F) else Color(0xFF37474F),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    if (isUnlocked || canAfford) {
                      onBuyAndEquipSmoke(smoke.id, smoke.cost)
                    }
                  },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = smoke.iconEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = smoke.name,
                      color = Color.White,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Box(
                    modifier = Modifier
                      .background(
                        when {
                          isEquipped -> Color(0xFF43A047)
                          isUnlocked -> Color(0xFF0288D1)
                          canAfford -> Color(0xFFFFB300)
                          else -> Color(0xFF455A64)
                        },
                        RoundedCornerShape(8.dp)
                      )
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = when {
                        isEquipped -> "EQUIPPED"
                        isUnlocked -> "USE"
                        else -> "🎟️ ${smoke.cost}"
                      },
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Button: Back to Driving Cab
      Button(
        onClick = onBackToDrive,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("back_to_drive_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "🚂 TEST DRIVE ON TRACKS!",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
      }
    }
  }
}
