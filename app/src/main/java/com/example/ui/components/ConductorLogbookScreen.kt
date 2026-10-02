package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConductorProfileEntity
import com.example.model.TrackWorldData

@Composable
fun ConductorLogbookScreen(
  profile: ConductorProfileEntity,
  onBackToDrive: () -> Unit,
  modifier: Modifier = Modifier
) {
  val unlockedSecrets = remember(profile.unlockedSecretsCsv) {
    profile.unlockedSecretsCsv.split(",").filter { it.isNotBlank() }.toSet()
  }
  val visitedStations = remember(profile.visitedStationsCsv) {
    profile.visitedStationsCsv.split(",").filter { it.isNotBlank() }.toSet()
  }

  val rankTitle = when {
    profile.totalSafeStops >= 15 -> "🌟 Legendary Grand Conductor"
    profile.totalSafeStops >= 8 -> "🎖️ Master Track Engineer"
    profile.totalSafeStops >= 3 -> "🚂 Station Master"
    else -> "🧢 Junior Trainee Conductor"
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
      // Header
      Text(
        text = "📖 Conductor Logbook & Secrets",
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black
      )
      Text(
        text = "Your official train conductor credentials & discoveries!",
        color = Color(0xFFB0BEC5),
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Official Conductor License Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2830)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .background(Color(0xFF0288D1), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🧑‍✈️", fontSize = 26.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "OFFICIAL CONDUCTOR LICENSE",
                  color = Color(0xFFFFD54F),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = rankTitle,
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(text = "⭐ x ${profile.starsCollected}", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Stats row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF14191C), RoundedCornerShape(10.dp))
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${profile.totalSafeStops}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
              Text(text = "Safe Stops", color = Color(0xFF90A4AE), fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${unlockedSecrets.size} / ${TrackWorldData.allSecrets.size}", color = Color(0xFF80DEEA), fontSize = 16.sp, fontWeight = FontWeight.Black)
              Text(text = "Secrets Found", color = Color(0xFF90A4AE), fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${profile.goldenTickets}", color = Color(0xFFFFD54F), fontSize = 16.sp, fontWeight = FontWeight.Black)
              Text(text = "Golden Tickets", color = Color(0xFF90A4AE), fontSize = 10.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Visited Stations Stamps
      Text(
        text = "🚉 Station Passport Stamps",
        color = Color(0xFFFFD54F),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TrackWorldData.allStations.forEach { station ->
          val isVisited = visitedStations.contains(station.id)
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isVisited) Color(0xFF1B5E20) else Color(0xFF263238))
              .border(1.dp, if (isVisited) Color(0xFF66BB6A) else Color(0xFF37474F), RoundedCornerShape(10.dp))
              .padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (isVisited) "✅" else "🔒",
                fontSize = 14.sp
              )
              Text(
                text = station.name.split(" ").first(),
                color = if (isVisited) Color.White else Color(0xFF90A4AE),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Hidden Secrets Scrapbook
      Text(
        text = "✨ Hidden Scenic Secrets along the Route (${unlockedSecrets.size}/${TrackWorldData.allSecrets.size})",
        color = Color(0xFFFFD54F),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Drive forward or backward along scenic tracks and tap secrets to discover them!",
        color = Color(0xFFB0BEC5),
        fontSize = 10.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TrackWorldData.allSecrets.forEach { secret ->
          val isFound = unlockedSecrets.contains(secret.id)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = 1.dp,
                color = if (isFound) Color(0xFF00ACC1) else Color(0xFF37474F),
                shape = RoundedCornerShape(10.dp)
              ),
            colors = CardDefaults.cardColors(
              containerColor = if (isFound) Color(0xFF213038) else Color(0xFF1C242A)
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(if (isFound) Color(0xFF00838F) else Color(0xFF263238), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = if (isFound) secret.iconEmoji else "❓", fontSize = 20.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isFound) secret.name else "Mysterious Secret (${secret.world.title})",
                  color = if (isFound) Color.White else Color(0xFFB0BEC5),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (isFound) secret.description else "Explore ${secret.world.title} at mile ${(secret.trackPosition).toInt()} to discover!",
                  color = Color(0xFF90A4AE),
                  fontSize = 10.sp
                )
              }
              if (isFound) {
                Text(
                  text = "+${secret.rewardTickets} 🎟️",
                  color = Color(0xFFFFD54F),
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onBackToDrive,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("◀ BACK TO CAB & TRACKS", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}
