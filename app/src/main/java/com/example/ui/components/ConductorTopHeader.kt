package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.model.LandscapeWorld
import com.example.model.WeatherCondition
import com.example.viewmodel.ScreenTab

@Composable
fun ConductorTopHeader(
  currentWorld: LandscapeWorld,
  weather: WeatherCondition,
  goldenTickets: Int,
  starsCollected: Int,
  currentTab: ScreenTab,
  cautionMessage: String?,
  rewardToast: String?,
  onTabSelected: (ScreenTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color(0xFF1B2226),
    tonalElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      // Top Status Bar: World, Weather, Currency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Landscape World & Weather
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color(0xFF263238), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = currentWorld.iconEmoji, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = currentWorld.title,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          // Weather badge (Informative indicator)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF37474F))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${weather.iconEmoji} ${weather.displayName}",
              color = Color(0xFF81D4FA),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Tickets & Stars
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Tickets
          Row(
            modifier = Modifier
              .background(Color(0xFF263238), RoundedCornerShape(10.dp))
              .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🎟️", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "$goldenTickets",
              color = Color(0xFFFFD54F),
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }

          // Stars
          Row(
            modifier = Modifier
              .background(Color(0xFF263238), RoundedCornerShape(10.dp))
              .border(1.dp, Color(0xFF81D4FA), RoundedCornerShape(10.dp))
              .padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "⭐", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "$starsCollected",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }
        }
      }

      // Live Caution Alert Banner
      AnimatedVisibility(visible = cautionMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(Color(0xFFD32F2F), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = cautionMessage ?: "",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }

      // Live Reward Toast Banner
      AnimatedVisibility(visible = rewardToast != null && cautionMessage == null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = rewardToast ?: "",
            color = Color(0xFFFFF9C4),
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }

      // Navigation Bar: Drive, Workshop, Missions, Logbook
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        NavTabChip(
          title = "🚂 CAB",
          isSelected = currentTab == ScreenTab.DRIVE,
          activeColor = Color(0xFFE53935),
          modifier = Modifier.weight(1f),
          testTag = "nav_tab_drive"
        ) { onTabSelected(ScreenTab.DRIVE) }

        NavTabChip(
          title = "🛠️ GARAGE",
          isSelected = currentTab == ScreenTab.WORKSHOP,
          activeColor = Color(0xFF0288D1),
          modifier = Modifier.weight(1f),
          testTag = "nav_tab_workshop"
        ) { onTabSelected(ScreenTab.WORKSHOP) }

        NavTabChip(
          title = "📦 MISSIONS",
          isSelected = currentTab == ScreenTab.MISSIONS,
          activeColor = Color(0xFFFF8F00),
          modifier = Modifier.weight(1f),
          testTag = "nav_tab_missions"
        ) { onTabSelected(ScreenTab.MISSIONS) }

        NavTabChip(
          title = "📖 LOGBOOK",
          isSelected = currentTab == ScreenTab.LOGBOOK,
          activeColor = Color(0xFF7E57C2),
          modifier = Modifier.weight(1f),
          testTag = "nav_tab_logbook"
        ) { onTabSelected(ScreenTab.LOGBOOK) }
      }
    }
  }
}

@Composable
private fun NavTabChip(
  title: String,
  isSelected: Boolean,
  activeColor: Color,
  testTag: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) activeColor else Color(0xFF263238))
      .clickable { onClick() }
      .padding(vertical = 6.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
    )
  }
}
