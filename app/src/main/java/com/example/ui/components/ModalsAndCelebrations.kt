package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SecretDiscovery
import com.example.viewmodel.StationArrivalCelebration

@Composable
fun StationCelebrationDialog(
  celebration: StationArrivalCelebration,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1B242A)),
      modifier = Modifier
        .fillMaxWidth()
        .border(3.dp, Color(0xFF43A047), RoundedCornerShape(20.dp))
        .padding(4.dp)
        .testTag("station_celebration_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "🚉 ALL ABOARD!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Perfect Safe Stop at ${celebration.station.name}!",
          fontSize = 14.sp,
          color = Color.White,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Passengers Boarding
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          celebration.passengerEmojis.forEach { emoji ->
            Box(
              modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF263238), CircleShape)
                .border(2.dp, Color(0xFF81D4FA), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = emoji, fontSize = 24.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rewards
        Row(
          modifier = Modifier
            .background(Color(0xFF14191C), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = "+${celebration.bonusTickets} 🎟️ Tickets",
            color = Color(0xFFFFD54F),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Text(
            text = "+1 ⭐ Star",
            color = Color(0xFF81D4FA),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("station_continue_button")
        ) {
          Text("CHUG FORWARD! 🚂", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
fun SecretDiscoveredDialog(
  secret: SecretDiscovery,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1B242A)),
      modifier = Modifier
        .fillMaxWidth()
        .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(20.dp))
        .padding(4.dp)
        .testTag("secret_discovered_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "✨ SECRET UNLOCKED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF00838F), CircleShape)
            .border(2.dp, Color(0xFF80DEEA), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = secret.iconEmoji, fontSize = 34.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = secret.name,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = secret.description,
          fontSize = 12.sp,
          color = Color(0xFFECEFF1),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
          modifier = Modifier
            .background(Color(0xFF14191C), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "+${secret.rewardTickets} 🎟️ Golden Tickets Added!",
            color = Color(0xFFFFD54F),
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("secret_continue_button")
        ) {
          Text("GREAT JOB! 🌟", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
fun MissionCelebrationDialog(
  message: String,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1B242A)),
      modifier = Modifier
        .fillMaxWidth()
        .border(3.dp, Color(0xFF0288D1), RoundedCornerShape(20.dp))
        .padding(4.dp)
        .testTag("mission_celebration_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "🎉 MISSION COMPLETE!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = message,
          fontSize = 14.sp,
          color = Color.White,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("COLLECT REWARDS! 🌟", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
fun TrainWreckDialog(
  wreck: com.example.model.TrainWreckEvent,
  onRerail: () -> Unit,
  onTowToStation: () -> Unit
) {
  Dialog(onDismissRequest = { /* Must choose rerail or tow to continue */ }) {
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1719)),
      modifier = Modifier
        .fillMaxWidth()
        .border(3.5.dp, Color(0xFFE53935), RoundedCornerShape(22.dp))
        .padding(4.dp)
        .testTag("train_wreck_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Warning Badge Header
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFFD32F2F).copy(alpha = 0.25f),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE53935)),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🚨", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "EMERGENCY RUNAWAY WRECK",
              color = Color(0xFFFF8A80),
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        // Cartoon Dizzy Locomotive Icon
        Text(
          text = "💥 🚂 💨 🩹",
          fontSize = 36.sp,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
          text = wreck.reasonTitle,
          fontSize = 19.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFFFFD54F),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Speed Telemetry Comparison Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF2B2024),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Recorded Speed", fontSize = 11.sp, color = Color(0xFFB0BEC5))
              Text(
                "${"%.1f".format(wreck.speedMph)} MPH",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF5252)
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(Color(0xFF544449))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Safe Track Limit", fontSize = 11.sp, color = Color(0xFFB0BEC5))
              Text(
                "${"%.0f".format(wreck.maxSafeSpeedMph)} MPH",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF81C784)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Incident Details
        Text(
          text = wreck.incidentDetail,
          fontSize = 13.sp,
          color = Color(0xFFECEFF1),
          textAlign = TextAlign.Center,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Kid-friendly Safety Tip
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF37474F).copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Text("💡", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = wreck.safetyTip,
              fontSize = 12.sp,
              color = Color(0xFFFFF9C4),
              lineHeight = 16.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action 1: Call Breakdown Crane (Primary)
        Button(
          onClick = onRerail,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rerail_crane_button")
        ) {
          Text(
            "🏗️ CALL BREAKDOWN CRANE & RERAIL",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = Color(0xFF212121)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action 2: Tow to Station (Secondary)
        Button(
          onClick = onTowToStation,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tow_station_button")
        ) {
          Text(
            "🚉 EMERGENCY TOW TO STATION",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = Color.White
          )
        }
      }
    }
  }
}

