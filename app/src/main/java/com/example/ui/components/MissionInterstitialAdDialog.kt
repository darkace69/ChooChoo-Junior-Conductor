package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MissionAd
import kotlinx.coroutines.delay

@Composable
fun MissionInterstitialAdDialog(
  ad: MissionAd,
  onDismiss: () -> Unit,
  onClaimReward: () -> Unit
) {
  var secondsRemaining by remember { mutableIntStateOf(5) }
  var canSkip by remember { mutableStateOf(false) }
  var hasInstalled by remember { mutableStateOf(false) }
  val progress = remember { Animatable(0f) }
  val pulseScale = remember { Animatable(1f) }

  // Countdown timer for ad compliance & duration
  LaunchedEffect(ad.id) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
    )
  }

  LaunchedEffect(ad.id) {
    while (secondsRemaining > 0) {
      delay(1000)
      secondsRemaining -= 1
    }
    canSkip = true
  }

  // Gentle pulse for ad icon
  LaunchedEffect(Unit) {
    pulseScale.animateTo(
      targetValue = 1.08f,
      animationSpec = infiniteRepeatable(
        animation = tween(800, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      )
    )
  }

  Dialog(
    onDismissRequest = {
      if (canSkip) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .testTag("mission_ad_dialog"),
      color = Color(0xFF0F1418)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // TOP BAR: Ad Badge + Countdown / Skip Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // "Ad" badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(Color(0xFF263238), RoundedCornerShape(6.dp))
              .border(1.dp, Color(0xFF546E7A), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(Color(0xFFFFD54F), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Ad • Sponsored",
              color = Color(0xFFFFD54F),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Skip Timer / Skip Button
          if (!canSkip) {
            Box(
              modifier = Modifier
                .background(Color(0x99000000), RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF455A64), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Reward in ${secondsRemaining}s ⏳",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          } else {
            Button(
              onClick = {
                onClaimReward()
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("skip_ad_button")
            ) {
              Text(
                text = "CLAIM & SKIP ⏭️",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        // Animated Timer Line
        LinearProgressIndicator(
          progress = { progress.value },
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = Color(ad.accentColorHex),
          trackColor = Color(0xFF263238)
        )

        // MAIN AD CREATIVE CARD
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2830)),
          border = androidx.compose.foundation.BorderStroke(2.dp, Color(ad.accentColorHex).copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color(ad.backgroundColorHex).copy(alpha = 0.85f),
                    Color(0xFF12191F)
                  )
                )
              )
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
          ) {
            // Category & Sponsor
            Text(
              text = "${ad.category.uppercase()} • ${ad.sponsorName}",
              color = Color(ad.accentColorHex),
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )

            // Hero Animated Icon Box
            Box(
              modifier = Modifier
                .size(110.dp)
                .scale(pulseScale.value)
                .background(Color(0xFF161F26), CircleShape)
                .border(3.dp, Color(ad.accentColorHex), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = ad.iconEmoji, fontSize = 54.sp)
            }

            // Title & Tagline
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = ad.title,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = ad.tagline,
                color = Color(0xFFFFD54F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }

            // Rating & Downloads Pill
            Row(
              modifier = Modifier
                .background(Color(0x55000000), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐ ${ad.rating}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "(${ad.reviewCount})", color = Color(0xFFB0BEC5), fontSize = 10.sp)
              }
              Box(modifier = Modifier.size(4.dp).background(Color(0xFF78909C), CircleShape))
              Text(text = "📥 ${ad.downloads}", color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            // Description Body
            Text(
              text = ad.description,
              color = Color(0xFFCFD8DC),
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              lineHeight = 16.sp,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Reward callout
            Box(
              modifier = Modifier
                .background(Color(0xFF263238), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = "🎁 Watching ad awards +${ad.rewardTickets} 🎟️ Golden Tickets!",
                color = Color(0xFFFFD54F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // BOTTOM ACTION ROW: Call to Action + Skip
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (!hasInstalled) {
            Button(
              onClick = {
                hasInstalled = true
                onClaimReward()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("install_ad_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(ad.accentColorHex)),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = ad.callToAction,
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              )
            }
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF2E7D32), RoundedCornerShape(14.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "✅ SPONSOR VISITED! +${ad.rewardTickets} TICKETS CLAIMED!",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Sub-footer
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "🛡️ Safe for Kids • Family Approved",
              color = Color(0xFF78909C),
              fontSize = 10.sp
            )
            if (canSkip) {
              Text(
                text = "Tap SKIP anytime",
                color = Color(0xFF81D4FA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDismiss() }
              )
            }
          }
        }
      }
    }
  }
}
