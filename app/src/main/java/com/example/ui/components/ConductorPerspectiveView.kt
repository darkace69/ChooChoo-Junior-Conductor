package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandscapeWorld
import com.example.model.SecretDiscovery
import com.example.model.StationInfo
import com.example.model.TrackCurvature
import com.example.model.TrackSlope
import com.example.model.WeatherCondition
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConductorPerspectiveView(
  currentWorld: LandscapeWorld,
  weather: WeatherCondition,
  isWipersOn: Boolean,
  isHeadlightOn: Boolean,
  speedMph: Float,
  trackPosition: Float,
  currentSlope: TrackSlope,
  currentCurvature: TrackCurvature,
  approachingStation: StationInfo?,
  nearbySecret: SecretDiscovery?,
  onTapSecret: (SecretDiscovery) -> Unit,
  approachingSigns: List<com.example.model.RailwaySign> = emptyList(),
  isWrecked: Boolean = false,
  dangerLevel: Float = 0f,
  onRerailTrain: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "scenery_animation")
  val animProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "progress"
  )

  val wobblePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(160, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "danger_wobble"
  )
  val dangerTilt = if (dangerLevel > 0.05f) {
    (sin(Math.toRadians(wobblePhase.toDouble())).toFloat() * dangerLevel * 12f)
  } else 0f
  val wreckTilt = if (isWrecked) -20f else dangerTilt

  val wiperAngle by infiniteTransition.animateFloat(
    initialValue = -50f,
    targetValue = 50f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wiper"
  )

  // Gradual weather animation specs (3.5 seconds smooth cinematic transition)
  val targetSkyTop = when (weather) {
    WeatherCondition.CLEAR -> currentWorld.skyColorDay
    WeatherCondition.LIGHT_RAIN -> Color(0xFF607D8B)
    WeatherCondition.GENTLE_SNOW -> Color(0xFF90A4AE)
  }
  val targetSkyBottom = when (weather) {
    WeatherCondition.CLEAR -> Color(0xFFFFF9C4)
    WeatherCondition.LIGHT_RAIN -> Color(0xFFB0BEC5)
    WeatherCondition.GENTLE_SNOW -> Color(0xFFECEFF1)
  }

  val animatedSkyTop by animateColorAsState(
    targetValue = targetSkyTop,
    animationSpec = tween(durationMillis = 3500, easing = LinearEasing),
    label = "sky_top"
  )
  val animatedSkyBottom by animateColorAsState(
    targetValue = targetSkyBottom,
    animationSpec = tween(durationMillis = 3500, easing = LinearEasing),
    label = "sky_bottom"
  )

  val sunAlpha by animateFloatAsState(
    targetValue = if (weather == WeatherCondition.CLEAR) 1.0f else 0.0f,
    animationSpec = tween(durationMillis = 3200),
    label = "sun_alpha"
  )
  val rainAlpha by animateFloatAsState(
    targetValue = if (weather == WeatherCondition.LIGHT_RAIN) 1.0f else 0.0f,
    animationSpec = tween(durationMillis = 3500),
    label = "rain_alpha"
  )
  val snowAlpha by animateFloatAsState(
    targetValue = if (weather == WeatherCondition.GENTLE_SNOW) 1.0f else 0.0f,
    animationSpec = tween(durationMillis = 3500),
    label = "snow_alpha"
  )

  // Pre-seed pseudo random drops and flakes
  val randomPoints = remember {
    List(40) {
      Offset(Random.nextFloat(), Random.nextFloat())
    }
  }

  Box(
    modifier = modifier
      .background(Color.Black)
      .pointerInput(nearbySecret) {
        detectTapGestures { offset ->
          // If tapped near secret position on screen
          if (nearbySecret != null) {
            onTapSecret(nearbySecret)
          }
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      rotate(degrees = wreckTilt, pivot = Offset(w * 0.5f, h * 0.55f)) {
        // 1. Horizon & Sky
        val horizonY = when (currentSlope) {
          TrackSlope.UPHILL_STEEP -> h * 0.58f
          TrackSlope.UPHILL_GENTLE -> h * 0.52f
          TrackSlope.LEVEL -> h * 0.46f
          TrackSlope.DOWNHILL_GENTLE -> h * 0.40f
          TrackSlope.DOWNHILL_STEEP -> h * 0.34f
        }

        val curveOffsetPx = currentCurvature.curveOffset * (w * 0.28f)
        val vanishingX = (w * 0.5f) + curveOffsetPx

      // Gradual Sky Gradient
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(animatedSkyTop, animatedSkyBottom),
          startY = 0f,
          endY = horizonY
        ),
        size = Size(w, horizonY)
      )

      // Celestial / Weather backdrops (fades gradually with sunAlpha)
      if (sunAlpha > 0.02f) {
        // Cheerful Sun
        drawCircle(
          color = Color(0xFFFFD54F).copy(alpha = sunAlpha),
          radius = 28.dp.toPx(),
          center = Offset(w * 0.82f, horizonY * 0.35f)
        )
        drawCircle(
          color = Color(0xFFFFF9C4).copy(alpha = sunAlpha),
          radius = 20.dp.toPx(),
          center = Offset(w * 0.82f, horizonY * 0.35f)
        )

        // Distant Rainbow
        val rainbowPath = Path().apply {
          moveTo(w * 0.1f, horizonY)
          quadraticBezierTo(w * 0.4f, horizonY * 0.15f, w * 0.7f, horizonY)
        }
        drawPath(
          path = rainbowPath,
          color = Color(0x33FF4081).copy(alpha = 0.2f * sunAlpha),
          style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
      }

      // Distant Mountains / Hills
      drawMountains(w, horizonY, currentWorld)

      // 2. Ground Terrain
      drawRect(
        color = currentWorld.groundColor,
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
      )

      // Ground path / ballast
      val ballastPath = Path().apply {
        moveTo(vanishingX - (w * 0.06f), horizonY)
        lineTo(vanishingX + (w * 0.06f), horizonY)
        lineTo(w * 0.85f, h)
        lineTo(w * 0.15f, h)
        close()
      }
      drawPath(
        path = ballastPath,
        color = Color(0xFF5D4037)
      )

      // 3. Railroad Tracks (Ties and Rails)
      val numTies = 10
      val motionOffset = (trackPosition % 1f)
      for (i in numTies downTo 1) {
        val t = ((i.toFloat() - motionOffset) / numTies.toFloat()).coerceIn(0.05f, 1.0f)
        val tieY = horizonY + (h - horizonY) * (t * t)
        val tieHalfWidth = (w * 0.06f) + (w * 0.35f) * (t * t)
        val tieCenterX = vanishingX + (w * 0.5f - vanishingX) * t

        // Wooden Tie
        drawLine(
          color = Color(0xFF3E2723),
          start = Offset(tieCenterX - tieHalfWidth, tieY),
          end = Offset(tieCenterX + tieHalfWidth, tieY),
          strokeWidth = (2.dp.toPx() + 6.dp.toPx() * t),
          cap = StrokeCap.Round
        )
      }

      // Steel Rails (Left & Right)
      val leftRailStart = Offset(vanishingX - (w * 0.035f), horizonY)
      val leftRailEnd = Offset(w * 0.22f, h)
      val rightRailStart = Offset(vanishingX + (w * 0.035f), horizonY)
      val rightRailEnd = Offset(w * 0.78f, h)

      drawLine(
        color = Color(0xFFCFD8DC),
        start = leftRailStart,
        end = leftRailEnd,
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
      )
      drawLine(
        color = Color(0xFFCFD8DC),
        start = rightRailStart,
        end = rightRailEnd,
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Rail Highlights (Glint)
      drawLine(
        color = Color.White,
        start = leftRailStart,
        end = leftRailEnd,
        strokeWidth = 2.dp.toPx()
      )
      drawLine(
        color = Color.White,
        start = rightRailStart,
        end = rightRailEnd,
        strokeWidth = 2.dp.toPx()
      )

      // 4. Headlight Cone
      if (isHeadlightOn) {
        val lightPath = Path().apply {
          moveTo(w * 0.5f, h)
          lineTo(vanishingX - (w * 0.22f), horizonY)
          lineTo(vanishingX + (w * 0.22f), horizonY)
          close()
        }
        drawPath(
          path = lightPath,
          brush = Brush.verticalGradient(
            colors = listOf(Color(0x55FFF59D), Color(0x11FFF59D)),
            startY = horizonY,
            endY = h
          )
        )
      }

      // 5. Approaching Station Boarding Zone Marker
      if (approachingStation != null) {
        val dist = (approachingStation.trackPosition - (trackPosition % 360f))
        if (dist in -10f..40f) {
          val t = (1f - (dist / 40f)).coerceIn(0.1f, 0.95f)
          val stationY = horizonY + (h - horizonY) * (t * t)
          val platformX = vanishingX + (w * 0.28f * t)

          // Green Target Stop Zone across the rails
          val zoneHalfWidth = (w * 0.08f) + (w * 0.28f) * (t * t)
          val zoneCenterX = vanishingX + (w * 0.5f - vanishingX) * t
          drawRect(
            color = Color(0x6643A047),
            topLeft = Offset(zoneCenterX - zoneHalfWidth, stationY - 14.dp.toPx()),
            size = Size(zoneHalfWidth * 2f, 28.dp.toPx())
          )

          // Station Platform & Canopy on side
          drawRoundRect(
            color = Color(0xFFE0E0E0),
            topLeft = Offset(platformX, stationY - 32.dp.toPx() * t),
            size = Size(w * 0.22f * t, 40.dp.toPx() * t),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
          )
          // Station Signboard
          drawRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(platformX + 4.dp.toPx(), stationY - 44.dp.toPx() * t),
            size = Size(w * 0.20f * t, 12.dp.toPx() * t)
          )
        }
      }

      // 5b. Approaching Trackside Railway Signs (SLOW DOWN and SPEED UP boards)
      approachingSigns.forEach { sign ->
        val dist = (sign.trackPosition - (trackPosition % 360f) + 360f) % 360f
        if (dist in 1.5f..40f) {
          val t = (1f - (dist / 40f)).coerceIn(0.08f, 0.95f)
          val signY = horizonY + (h - horizonY) * (t * t)
          val signX = vanishingX + (w * 0.26f * t)

          val postWidth = (3.dp.toPx() + 4.dp.toPx() * t)
          val postHeight = (18.dp.toPx() + 38.dp.toPx() * t)
          val boardSize = (14.dp.toPx() + 26.dp.toPx() * t)
          val boardCenterY = signY - postHeight

          // Metal / Wooden Signpost
          drawRect(
            color = Color(0xFF37474F),
            topLeft = Offset(signX - postWidth * 0.5f, signY - postHeight),
            size = Size(postWidth, postHeight)
          )

          // Sign Board
          if (sign.type == com.example.model.RailwaySignType.SLOW_DOWN) {
            // Diamond Warning Board (SLOW DOWN)
            val diamondHalf = boardSize * 0.55f
            val diamondPath = Path().apply {
              moveTo(signX, boardCenterY - diamondHalf)
              lineTo(signX + diamondHalf, boardCenterY)
              lineTo(signX, boardCenterY + diamondHalf)
              lineTo(signX - diamondHalf, boardCenterY)
              close()
            }
            drawPath(diamondPath, Color(0xFF212121), style = Stroke(width = 3.dp.toPx() * t))
            drawPath(diamondPath, Color(0xFFFFD54F))

            // Chevron Arrow pointing DOWN (SLOW DOWN)
            drawLine(
              color = Color(0xFF212121),
              start = Offset(signX - diamondHalf * 0.45f, boardCenterY - diamondHalf * 0.2f),
              end = Offset(signX, boardCenterY + diamondHalf * 0.35f),
              strokeWidth = 2.5.dp.toPx() * t,
              cap = StrokeCap.Round
            )
            drawLine(
              color = Color(0xFF212121),
              start = Offset(signX, boardCenterY + diamondHalf * 0.35f),
              end = Offset(signX + diamondHalf * 0.45f, boardCenterY - diamondHalf * 0.2f),
              strokeWidth = 2.5.dp.toPx() * t,
              cap = StrokeCap.Round
            )
          } else {
            // Emerald Circle Board (SPEED UP)
            val circleRadius = boardSize * 0.5f
            drawCircle(Color.White, radius = circleRadius + 2.dp.toPx() * t, center = Offset(signX, boardCenterY))
            drawCircle(Color(0xFF2E7D32), radius = circleRadius, center = Offset(signX, boardCenterY))

            // Arrow pointing UP (SPEED UP)
            drawLine(
              color = Color.White,
              start = Offset(signX, boardCenterY + circleRadius * 0.45f),
              end = Offset(signX, boardCenterY - circleRadius * 0.45f),
              strokeWidth = 2.5.dp.toPx() * t,
              cap = StrokeCap.Round
            )
            drawLine(
              color = Color.White,
              start = Offset(signX - circleRadius * 0.35f, boardCenterY - circleRadius * 0.05f),
              end = Offset(signX, boardCenterY - circleRadius * 0.45f),
              strokeWidth = 2.5.dp.toPx() * t,
              cap = StrokeCap.Round
            )
            drawLine(
              color = Color.White,
              start = Offset(signX + circleRadius * 0.35f, boardCenterY - circleRadius * 0.05f),
              end = Offset(signX, boardCenterY - circleRadius * 0.45f),
              strokeWidth = 2.5.dp.toPx() * t,
              cap = StrokeCap.Round
            )
          }
        }
      }

      // 6. Dynamic Weather Particles (Smooth gradual fade in and out)
      if (rainAlpha > 0.02f) {
        // Rain streaks
        randomPoints.forEachIndexed { idx, pt ->
          val rx = (pt.x * w + animProgress * 300f + idx * 25f) % w
          val ry = (pt.y * h + animProgress * 800f + idx * 45f) % h
          drawLine(
            color = Color(0xFFB0BEC5).copy(alpha = 0.55f * rainAlpha),
            start = Offset(rx, ry),
            end = Offset(rx - 8f, ry + 22f),
            strokeWidth = 2.dp.toPx()
          )
        }
        // Windshield droplets
        if (!isWipersOn) {
          randomPoints.take(15).forEach { pt ->
            val dx = pt.x * w
            val dy = pt.y * h
            drawCircle(
              color = Color(0xFFCFD8DC).copy(alpha = 0.65f * rainAlpha),
              radius = 3.5.dp.toPx(),
              center = Offset(dx, dy)
            )
          }
        }
      }

      if (snowAlpha > 0.02f) {
        // Falling Snowflakes
        randomPoints.forEachIndexed { idx, pt ->
          val sx = (pt.x * w + sin((animProgress * 6f + idx).toDouble()).toFloat() * 20f) % w
          val sy = (pt.y * h + animProgress * 400f + idx * 30f) % h
          drawCircle(
            color = Color.White.copy(alpha = 0.85f * snowAlpha),
            radius = (2.dp.toPx() + (idx % 3).dp.toPx()),
            center = Offset(sx, sy)
          )
        }
        // Frost corners on windshield
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF).copy(alpha = 0.4f * snowAlpha), Color.Transparent),
            center = Offset(0f, 0f),
            radius = w * 0.25f
          ),
          radius = w * 0.25f,
          center = Offset(0f, 0f)
        )
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF).copy(alpha = 0.4f * snowAlpha), Color.Transparent),
            center = Offset(w, 0f),
            radius = w * 0.25f
          ),
          radius = w * 0.25f,
          center = Offset(w, 0f)
        )
      }

      // 7. Windshield Frame (Locomotive Cab Interior Border)
      val frameThickness = 14.dp.toPx()
      // Outer cab rim
      drawRect(
        color = Color(0xFF263238),
        size = Size(w, frameThickness)
      )
      drawRect(
        color = Color(0xFF263238),
        topLeft = Offset(0f, h - frameThickness),
        size = Size(w, frameThickness)
      )
      drawRect(
        color = Color(0xFF263238),
        size = Size(frameThickness, h)
      )
      drawRect(
        color = Color(0xFF263238),
        topLeft = Offset(w - frameThickness, 0f),
        size = Size(frameThickness, h)
      )

      // Center divider bar
      drawRect(
        color = Color(0xFF37474F),
        topLeft = Offset(w * 0.5f - 4.dp.toPx(), 0f),
        size = Size(8.dp.toPx(), h)
      )

      // Brass rivet accents
      val brassColor = Color(0xFFFFC107)
      for (i in 1..4) {
        val rivetY = (h / 5f) * i
        drawCircle(color = brassColor, radius = 3.dp.toPx(), center = Offset(frameThickness * 0.5f, rivetY))
        drawCircle(color = brassColor, radius = 3.dp.toPx(), center = Offset(w - frameThickness * 0.5f, rivetY))
      }

      // 8. Windshield Wipers
      if (isWipersOn) {
        val wiperPivotLeft = Offset(w * 0.28f, h - frameThickness)
        val wiperPivotRight = Offset(w * 0.72f, h - frameThickness)
        val wiperLength = h * 0.65f

        val rad = Math.toRadians((wiperAngle - 90).toDouble())
        val endLeft = Offset(
          wiperPivotLeft.x + (wiperLength * Math.cos(rad)).toFloat(),
          wiperPivotLeft.y + (wiperLength * Math.sin(rad)).toFloat()
        )
        val endRight = Offset(
          wiperPivotRight.x + (wiperLength * Math.cos(rad)).toFloat(),
          wiperPivotRight.y + (wiperLength * Math.sin(rad)).toFloat()
        )

        drawLine(
          color = Color(0xFF1E1E1E),
          start = wiperPivotLeft,
          end = endLeft,
          strokeWidth = 4.dp.toPx(),
          cap = StrokeCap.Round
        )
        drawLine(
          color = Color(0xFF1E1E1E),
          start = wiperPivotRight,
          end = endRight,
          strokeWidth = 4.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      } // End rotate(wreckTilt)

      // Danger Pulsing Red Edge Glow
      if (dangerLevel > 0.05f && !isWrecked) {
        drawRect(
          color = Color(0xFFE53935).copy(alpha = (dangerLevel * 0.55f).coerceIn(0f, 0.7f)),
          style = Stroke(width = 8.dp.toPx())
        )
      }
    }


    // Nearby Secret Interactive Callout
    if (nearbySecret != null) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(bottom = 30.dp)
          .background(Color(0xE6FFF8E1), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
          .clickable { onTapSecret(nearbySecret) }
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Text(
          text = "✨ Tap Secret: ${nearbySecret.iconEmoji} ${nearbySecret.name}!",
          color = Color(0xFFE65100),
          fontSize = 13.sp,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
      }
    }

    // Approaching Station Label
    if (approachingStation != null && !isWrecked) {
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 18.dp)
          .background(Color(0xE6C62828), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "🚉 Approaching ${approachingStation.name} - Press STOP inside Green Zone!",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
      }
    }

    // WRECK ON-SCREEN OVERLAY
    if (isWrecked) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x44B71C1C)),
        contentAlignment = Alignment.Center
      ) {
        androidx.compose.foundation.layout.Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .background(Color(0xEE1E1719), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFFF5252), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
          Text(
            text = "💥 DERAILMENT!",
            fontSize = 17.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            color = Color(0xFFFFD54F)
          )
          androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 2.dp))
          Text(
            text = "Speed exceeded curve/slope limit!",
            fontSize = 11.sp,
            color = Color(0xFFFFCDD2)
          )
          if (onRerailTrain != null) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 6.dp))
            androidx.compose.material3.Button(
              onClick = onRerailTrain,
              colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
              shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
            ) {
              Text(
                "🏗️ RERAIL LOCOMOTIVE",
                color = Color(0xFF212121),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }
  }
}

private fun DrawScope.drawMountains(w: Float, horizonY: Float, world: LandscapeWorld) {
  val mountainColor = when (world) {
    LandscapeWorld.FOREST -> Color(0xFF2E7D32)
    LandscapeWorld.CANDY -> Color(0xFFEC407A)
    LandscapeWorld.DINO -> Color(0xFFBF360C)
    LandscapeWorld.COASTAL -> Color(0xFF00838F)
    LandscapeWorld.SNOWY -> Color(0xFF78909C)
  }

  val path = Path().apply {
    moveTo(0f, horizonY)
    lineTo(w * 0.15f, horizonY - 45.dp.toPx())
    lineTo(w * 0.35f, horizonY - 20.dp.toPx())
    lineTo(w * 0.55f, horizonY - 60.dp.toPx())
    lineTo(w * 0.75f, horizonY - 30.dp.toPx())
    lineTo(w * 0.90f, horizonY - 50.dp.toPx())
    lineTo(w, horizonY)
    close()
  }
  drawPath(path = path, color = mountainColor.copy(alpha = 0.55f))
}
