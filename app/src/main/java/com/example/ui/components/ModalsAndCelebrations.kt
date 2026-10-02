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
