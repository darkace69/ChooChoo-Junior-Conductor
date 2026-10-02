package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MissionState
import com.example.model.SpecialDeliveryMission
import com.example.model.TrackWorldData

@Composable
fun SpecialDeliveryMissionsScreen(
  activeMission: SpecialDeliveryMission?,
  missionState: MissionState,
  timeRemainingSec: Int,
  incidentsCount: Int,
  onStartMission: (SpecialDeliveryMission) -> Unit,
  onCancelMission: () -> Unit,
  onBackToDrive: () -> Unit,
  modifier: Modifier = Modifier
) {
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
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "📦 Special Delivery Missions",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "Pick up precious cargo & deliver safely without incidents!",
            color = Color(0xFFB0BEC5),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Active Mission Banner (if active)
      if (activeMission != null) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = when (missionState) {
              MissionState.COMPLETED -> Color(0xFF1B5E20)
              MissionState.FAILED -> Color(0xFFB71C1C)
              else -> Color(0xFF0D47A1)
            }
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = activeMission.cargoEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = activeMission.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                  )
                  Text(
                    text = "Status: ${
                      when (missionState) {
                        MissionState.PICKUP_READY -> "Head to ${activeMission.pickupStationName} to LOAD"
                        MissionState.DELIVERING -> "DELIVERING to ${activeMission.dropoffStationName}"
                        MissionState.COMPLETED -> "MISSION COMPLETED! 🎉"
                        MissionState.FAILED -> "FAILED - Cargo Damaged or Time Expired"
                        else -> "IDLE"
                      }
                    }",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (missionState == MissionState.PICKUP_READY || missionState == MissionState.DELIVERING) {
                Button(
                  onClick = onCancelMission,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Cancel", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time & Incidents Bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "⏱️ Time Left: ${timeRemainingSec}s",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "⚠️ Incidents: $incidentsCount / ${activeMission.maxIncidents} max",
                color = if (incidentsCount > 0) Color(0xFFFF8A80) else Color(0xFF81D4FA),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
      }

      // Mission List Catalog
      Text(
        text = "Select a Special Delivery Mission:",
        color = Color(0xFFFFD54F),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TrackWorldData.allMissions.forEach { mission ->
          val isCurrentMission = activeMission?.id == mission.id

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = if (isCurrentMission) 2.dp else 1.dp,
                color = if (isCurrentMission) Color(0xFFFFD54F) else Color(0xFF37474F),
                shape = RoundedCornerShape(12.dp)
              ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = mission.cargoEmoji, fontSize = 28.sp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = mission.title,
                      color = Color.White,
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Black
                    )
                    Text(
                      text = "Cargo: ${mission.cargoName}",
                      color = Color(0xFF80DEEA),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }

                // Reward Badge
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "+${mission.rewardTickets} 🎟️",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "+${mission.rewardStars} ⭐",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = mission.description,
                color = Color(0xFFECEFF1),
                fontSize = 11.sp
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Route & parameters
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFF1E272C), RoundedCornerShape(8.dp))
                  .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "📍 ${mission.pickupStationName} ➔ 🏁 ${mission.dropoffStationName}",
                  color = Color(0xFFB0BEC5),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "⏱️ ${mission.timeLimitSec}s • Max ${mission.maxIncidents} Incidents",
                  color = Color(0xFFFFCC80),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Start / Select Button
              Button(
                onClick = {
                  onStartMission(mission)
                  onBackToDrive()
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("start_mission_${mission.id}"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isCurrentMission) Color(0xFF0288D1) else Color(0xFF43A047)
                ),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = if (isCurrentMission) "RETURN TO CAB" else "ACCEPT SPECIAL DELIVERY 🚚",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onBackToDrive,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("◀ BACK TO CONDUCTOR CAB", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}
