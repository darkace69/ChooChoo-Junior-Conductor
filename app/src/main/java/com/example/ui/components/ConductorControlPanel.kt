package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RailwaySign
import com.example.model.RailwaySignType
import com.example.model.SpeedNotch
import com.example.model.TrackCurvature
import com.example.model.TrackSegment
import com.example.model.TrackSlope
import com.example.model.WeatherCondition
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ConductorControlPanel(
  speedNotch: SpeedNotch,
  actualSpeedMph: Float,
  currentSlope: TrackSlope,
  currentCurvature: TrackCurvature,
  weather: WeatherCondition,
  isWipersOn: Boolean,
  isHeadlightOn: Boolean,
  isPaused: Boolean,
  approachingJunction: TrackSegment?,
  selectedSwitchTurn: Boolean,
  onSetSpeedNotch: (SpeedNotch) -> Unit,
  onPullWhistle: () -> Unit,
  onTogglePause: () -> Unit,
  onToggleWipers: () -> Unit,
  onToggleHeadlight: () -> Unit,
  onToggleSwitchTrack: () -> Unit,
  activeRailwaySign: RailwaySign? = null,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color(0xFF21272B),
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {

      // Active Railway Advisory Sign Banner (SLOW DOWN & SPEED UP)
      AnimatedVisibility(visible = activeRailwaySign != null) {
        val sign = activeRailwaySign
        if (sign != null) {
          val isSlowDown = sign.type == RailwaySignType.SLOW_DOWN
          val bannerBg = if (isSlowDown) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
          val bannerBorder = if (isSlowDown) Color(0xFFFF9800) else Color(0xFF43A047)
          val textColor = if (isSlowDown) Color(0xFFE65100) else Color(0xFF1B5E20)

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(bannerBg)
              .border(2.dp, bannerBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
              .testTag("railway_sign_banner"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (isSlowDown) "⬇️ ${sign.iconEmoji}" else "⬆️ ${sign.iconEmoji}",
                fontSize = 18.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "${sign.title} • ${sign.targetSpeedText}",
                  color = textColor,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = sign.subtitle,
                  color = Color(0xFF37474F),
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bannerBorder)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = if (isSlowDown) "SLOW DOWN" else "FULL STEAM",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // Junction switch prompt bar (if near switch)
      AnimatedVisibility(visible = approachingJunction != null) {
        val targetName = approachingJunction?.junctionChoiceName ?: "Alternate Track"
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .background(
              color = if (selectedSwitchTurn) Color(0xFF2E7D32) else Color(0xFF455A64),
              shape = RoundedCornerShape(10.dp)
            )
            .clickable { onToggleSwitchTrack() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (selectedSwitchTurn) "🟢 Track Switched to: $targetName" else "🛤️ Track Junction: Tap to Switch to $targetName",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = if (selectedSwitchTurn) "SWITCHED 🔀" else "SWITCH LEVER ⚙️",
            color = Color(0xFFFFD54F),
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
        }
      }

      // Middle Row: Speedometer gauge & Status indicators & Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Speedometer Dial Card
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color(0xFF141A1E), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          // Circular Speed Indicator
          val speedColor = when {
            abs(actualSpeedMph) > currentCurvature.maxSafeSpeed -> Color(0xFFE53935)
            abs(actualSpeedMph) > 34f -> Color(0xFFFFB300)
            abs(actualSpeedMph) > 0.5f -> Color(0xFF43A047)
            else -> Color(0xFF90A4AE)
          }

          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFF21272B))
              .border(2.dp, speedColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${abs(actualSpeedMph).toInt()}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "MPH",
                color = Color(0xFFB0BEC5),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Slope and Curvature badges
          Column {
            // Slope indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = currentSlope.icon, fontSize = 11.sp, color = Color(0xFFFFD54F))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = currentSlope.label,
                color = Color(0xFFCFD8DC),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
              )
            }
            // Curve indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (currentCurvature != TrackCurvature.STRAIGHT) "🔄" else "━",
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = currentCurvature.label,
                color = if (currentCurvature == TrackCurvature.CURVE_SHARP_LEFT || currentCurvature == TrackCurvature.CURVE_SHARP_RIGHT) {
                  Color(0xFFFFB300)
                } else {
                  Color(0xFF90A4AE)
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Action Buttons Row: PAUSE/RESUME, WHISTLE, WIPER, LIGHT
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // PAUSE / RESUME TOGGLE BUTTON
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isPaused) Color(0xFF43A047) else Color(0xFF37474F))
              .clickable { onTogglePause() }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("pause_resume_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = if (isPaused) "Resume" else "Pause",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = if (isPaused) "RESUME" else "PAUSE",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // WHISTLE BUTTON
          val whistleInteraction = remember { MutableInteractionSource() }
          val isWhistlePressed by whistleInteraction.collectIsPressedAsState()
          val whistleScale by animateFloatAsState(
            targetValue = if (isWhistlePressed) 0.88f else 1.0f,
            animationSpec = tween(80),
            label = "whistle_press"
          )

          Box(
            modifier = Modifier
              .scale(whistleScale)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                )
              )
              .clickable(
                interactionSource = whistleInteraction,
                indication = null
              ) { onPullWhistle() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("whistle_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🔔", fontSize = 16.sp)
              Text(
                text = "TOOT!",
                color = Color(0xFF3E2723),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // WIPER TOGGLE BUTTON
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isWipersOn) Color(0xFF00ACC1) else Color(0xFF37474F))
              .clickable { onToggleWipers() }
              .padding(horizontal = 7.dp, vertical = 6.dp)
              .testTag("wiper_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🚿", fontSize = 14.sp)
              Text(
                text = if (isWipersOn) "ON" else "WIPER",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // HEADLIGHT BUTTON
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isHeadlightOn) Color(0xFFFBC02D) else Color(0xFF37474F))
              .clickable { onToggleHeadlight() }
              .padding(horizontal = 7.dp, vertical = 6.dp)
              .testTag("headlight_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Headlight",
                tint = if (isHeadlightOn) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (isHeadlightOn) "ON" else "LIGHT",
                color = if (isHeadlightOn) Color.Black else Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Bottom: Interactive Locomotive Throttle Lever (Replaces buttons with physical sliding lever)
      ConductorThrottleLever(
        currentNotch = speedNotch,
        onSetSpeedNotch = onSetSpeedNotch,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
fun ConductorThrottleLever(
  currentNotch: SpeedNotch,
  onSetSpeedNotch: (SpeedNotch) -> Unit,
  modifier: Modifier = Modifier
) {
  val notches = remember {
    listOf(
      SpeedNotch.REVERSE,
      SpeedNotch.STOP,
      SpeedNotch.SPEED_1,
      SpeedNotch.SPEED_2,
      SpeedNotch.SPEED_3
    )
  }

  val currentIndex = notches.indexOf(currentNotch).coerceIn(0, notches.size - 1)

  // Animated fraction for lever handle along track (0.0f to 1.0f)
  val targetFraction = currentIndex.toFloat() / (notches.size - 1)
  val animatedFraction by animateFloatAsState(
    targetValue = targetFraction,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
    label = "lever_position"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF14191C))
      .border(1.5.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag("throttle_lever_container")
  ) {
    // Header label: "THROTTLE LEVER" + Current Status
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🕹️", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "THROTTLE LEVER",
          color = Color(0xFFCFD8DC),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }

      val notchLabel = when (currentNotch) {
        SpeedNotch.REVERSE -> "◀ REVERSE (-14 MPH)"
        SpeedNotch.STOP -> "🛑 BRAKE (0 MPH)"
        SpeedNotch.SPEED_1 -> "🐢 NOTCH 1 (16 MPH)"
        SpeedNotch.SPEED_2 -> "🚂 NOTCH 2 (32 MPH)"
        SpeedNotch.SPEED_3 -> "⚡ NOTCH 3 (52 MPH)"
      }
      val notchColor = when (currentNotch) {
        SpeedNotch.REVERSE -> Color(0xFFFF9800)
        SpeedNotch.STOP -> Color(0xFFEF5350)
        SpeedNotch.SPEED_1 -> Color(0xFF66BB6A)
        SpeedNotch.SPEED_2 -> Color(0xFF29B6F6)
        SpeedNotch.SPEED_3 -> Color(0xFFAB47BC)
      }

      Text(
        text = notchLabel,
        color = notchColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Interactive Lever Track & Handle
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
            val targetIdx = (fraction * (notches.size - 1)).roundToInt().coerceIn(0, notches.size - 1)
            onSetSpeedNotch(notches[targetIdx])
          }
        }
        .pointerInput(Unit) {
          detectHorizontalDragGestures { change, _ ->
            change.consume()
            val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
            val targetIdx = (fraction * (notches.size - 1)).roundToInt().coerceIn(0, notches.size - 1)
            if (targetIdx != currentIndex) {
              onSetSpeedNotch(notches[targetIdx])
            }
          }
        }
    ) {
      val trackWidthPx = constraints.maxWidth.toFloat()
      val trackHeightPx = constraints.maxHeight.toFloat()

      Canvas(modifier = Modifier.fillMaxSize()) {
        val slotY = trackHeightPx * 0.45f
        val slotHeight = 10.dp.toPx()
        val marginX = 22.dp.toPx()
        val usableWidth = trackWidthPx - marginX * 2

        // Metallic Throttle Slot Background
        drawRoundRect(
          color = Color(0xFF0D1113),
          topLeft = Offset(marginX - 6.dp.toPx(), slotY - slotHeight * 0.5f),
          size = Size(usableWidth + 12.dp.toPx(), slotHeight),
          cornerRadius = CornerRadius(5.dp.toPx())
        )
        drawRoundRect(
          color = Color(0xFF455A64),
          topLeft = Offset(marginX - 6.dp.toPx(), slotY - slotHeight * 0.5f),
          size = Size(usableWidth + 12.dp.toPx(), slotHeight),
          cornerRadius = CornerRadius(5.dp.toPx()),
          style = Stroke(width = 1.5.dp.toPx())
        )

        // Detent Tick Marks & Notches
        notches.forEachIndexed { index, notch ->
          val notchX = marginX + (index.toFloat() / (notches.size - 1)) * usableWidth
          val isSelected = index == currentIndex
          val tickColor = if (isSelected) Color(0xFFFFD54F) else Color(0xFF607D8B)

          // Vertical notch detent line
          drawLine(
            color = tickColor,
            start = Offset(notchX, slotY - 12.dp.toPx()),
            end = Offset(notchX, slotY + 12.dp.toPx()),
            strokeWidth = if (isSelected) 3.dp.toPx() else 1.5.dp.toPx()
          )
        }

        // Throttle Lever Arm & Grip Knob
        val knobCenterX = marginX + animatedFraction * usableWidth
        val knobCenterY = slotY

        // Chrome Lever Arm
        drawLine(
          color = Color(0xFFECEFF1),
          start = Offset(knobCenterX, knobCenterY - 16.dp.toPx()),
          end = Offset(knobCenterX, knobCenterY + 10.dp.toPx()),
          strokeWidth = 5.dp.toPx(),
          cap = StrokeCap.Round
        )
        drawLine(
          color = Color(0xFF78909C),
          start = Offset(knobCenterX - 1.dp.toPx(), knobCenterY - 16.dp.toPx()),
          end = Offset(knobCenterX - 1.dp.toPx(), knobCenterY + 10.dp.toPx()),
          strokeWidth = 1.5.dp.toPx()
        )

        // Large Locomotive Grip Ball / Knob (Red & Brass)
        val knobRadius = 13.dp.toPx()
        val knobColor = when (currentNotch) {
          SpeedNotch.STOP -> Color(0xFFD32F2F)
          SpeedNotch.REVERSE -> Color(0xFFF57C00)
          else -> Color(0xFF1976D2)
        }
        // Shadow
        drawCircle(
          color = Color(0x66000000),
          radius = knobRadius + 2.dp.toPx(),
          center = Offset(knobCenterX, knobCenterY - 4.dp.toPx())
        )
        // Main Knob Body
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF8A80), knobColor, Color(0xFF212121)),
            center = Offset(knobCenterX - knobRadius * 0.3f, knobCenterY - 4.dp.toPx() - knobRadius * 0.3f),
            radius = knobRadius
          ),
          radius = knobRadius,
          center = Offset(knobCenterX, knobCenterY - 4.dp.toPx())
        )
        // Brass Hub / Retaining Bolt
        drawCircle(
          color = Color(0xFFFFD54F),
          radius = 3.5.dp.toPx(),
          center = Offset(knobCenterX, knobCenterY - 4.dp.toPx())
        )
        drawCircle(
          color = Color(0xFF3E2723),
          radius = 1.8.dp.toPx(),
          center = Offset(knobCenterX, knobCenterY - 4.dp.toPx())
        )
      }

      // Detent Labels Row (REV, STOP, 1, 2, 3)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp)
          .align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        notches.forEachIndexed { idx, notch ->
          val isSelected = idx == currentIndex
          val labelText = when (notch) {
            SpeedNotch.REVERSE -> "REV"
            SpeedNotch.STOP -> "STOP"
            SpeedNotch.SPEED_1 -> "1 🐢"
            SpeedNotch.SPEED_2 -> "2 🚂"
            SpeedNotch.SPEED_3 -> "3 ⚡"
          }
          val labelColor = if (isSelected) Color(0xFFFFD54F) else Color(0xFF90A4AE)
          val fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold

          Text(
            text = labelText,
            color = labelColor,
            fontSize = 9.sp,
            fontWeight = fontWeight,
            modifier = Modifier
              .clickable { onSetSpeedNotch(notch) }
              .testTag("lever_notch_${notch.name.lowercase()}")
          )
        }
      }
    }
  }
}
