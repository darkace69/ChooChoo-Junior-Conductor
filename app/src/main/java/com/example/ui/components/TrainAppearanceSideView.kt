package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandscapeWorld
import com.example.model.TrackWorldData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrainAppearanceSideView(
  engineId: String,
  colorId: String,
  smokeId: String,
  currentWorld: LandscapeWorld,
  actualSpeedMph: Float,
  trackPosition: Float,
  cargoEmoji: String?,
  isPaused: Boolean,
  isWrecked: Boolean = false,
  dangerLevel: Float = 0f,
  modifier: Modifier = Modifier
) {
  val colorOption = TrackWorldData.availableColors.firstOrNull { it.id == colorId }
    ?: TrackWorldData.availableColors.first()
  val primaryColor = colorOption.color
  val secondaryColor = colorOption.secondaryColor

  val infiniteTransition = rememberInfiniteTransition(label = "train_side_anim")
  val continuousSpin by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wheel_spin"
  )

  val smokeAnim by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = if (abs(actualSpeedMph) > 0.5f && !isPaused && !isWrecked) 1200 else 3000,
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Restart
    ),
    label = "smoke"
  )

  // Live profile wheel rotation:
  val isReverse = actualSpeedMph < 0f
  val wheelRot = when {
    isPaused || isWrecked -> 0f
    abs(actualSpeedMph) > 0.5f -> {
      val roll = (trackPosition * 65f) % 360f
      if (isReverse) -roll else roll
    }
    else -> continuousSpin
  }

  Box(modifier = modifier.fillMaxSize().background(Color(0xFF1E272C))) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. Scrolling Landscape Backdrop
      val groundY = h * 0.76f
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(currentWorld.skyColorDay.copy(alpha = 0.4f), currentWorld.groundColor),
          startY = 0f,
          endY = groundY
        ),
        size = Size(w, groundY)
      )

      // Scrolling bushes / hills
      val scrollX = (trackPosition * 8f) % 80f
      for (x in -80..w.toInt() + 80 step 80) {
        val cx = x - scrollX
        drawCircle(
          color = currentWorld.accentColor.copy(alpha = 0.35f),
          radius = 30.dp.toPx(),
          center = Offset(cx, groundY + 5.dp.toPx())
        )
      }

      // 2. Track Ballast & Rails
      drawRect(
        color = Color(0xFF4E342E),
        topLeft = Offset(0f, groundY),
        size = Size(w, h - groundY)
      )

      // Scrolling Wooden Sleepers / Ties
      val tieSpacing = 28.dp.toPx()
      val tieOffset = (trackPosition * 14f) % tieSpacing
      var tx = -tieSpacing
      while (tx < w + tieSpacing) {
        val currentTx = tx - tieOffset
        drawRect(
          color = Color(0xFF3E2723),
          topLeft = Offset(currentTx, groundY + 4.dp.toPx()),
          size = Size(8.dp.toPx(), 18.dp.toPx())
        )
        tx += tieSpacing
      }

      // Steel Rail Bar
      drawLine(
        color = Color(0xFFECEFF1),
        start = Offset(0f, groundY + 4.dp.toPx()),
        end = Offset(w, groundY + 4.dp.toPx()),
        strokeWidth = 5.dp.toPx()
      )

      // 3. Render Train (Locomotive + Passenger/Cargo Coach)
      val trainCenterX = w * 0.48f
      val trainBaseY = groundY + 2.dp.toPx()

      // Direction multiplier
      val isReverse = actualSpeedMph < -0.5f

      // Draw Smoke Puffs (originating directly from top of the smokestack)
      val smokestackX = when (engineId) {
        "streamliner" -> trainCenterX + 50.dp.toPx()
        "dino_express" -> trainCenterX + 65.dp.toPx()
        "caterpillar" -> trainCenterX + 65.dp.toPx()
        "golden_royal" -> trainCenterX + 65.dp.toPx()
        else -> trainCenterX + 65.dp.toPx() // Exact horizontal center of steam chimney stack
      }
      val smokestackY = when (engineId) {
        "streamliner" -> trainBaseY - 54.dp.toPx()
        "dino_express" -> trainBaseY - 58.dp.toPx()
        "caterpillar" -> trainBaseY - 60.dp.toPx()
        "golden_royal" -> trainBaseY - 66.dp.toPx()
        else -> trainBaseY - 68.dp.toPx() // Exact top rim of steam chimney stack
      }

      drawSmokePuffs(
        smokestackX = smokestackX,
        smokestackY = smokestackY,
        smokeAnim = if (isPaused) 0.5f else smokeAnim,
        smokeId = smokeId,
        isMoving = abs(actualSpeedMph) > 0.5f
      )

      // Rotate whole train if wrecked
      val trainTilt = if (isWrecked) -24f else 0f
      rotate(degrees = trainTilt, pivot = Offset(trainCenterX, trainBaseY)) {
        // Passenger / Cargo Coach (Coupled behind locomotive)
        val coachX = trainCenterX - 110.dp.toPx()
        val coachY = trainBaseY - 48.dp.toPx()
        drawCoach(
          x = coachX,
          y = coachY,
          primaryColor = primaryColor,
          secondaryColor = secondaryColor,
          cargoEmoji = cargoEmoji,
          wheelRot = wheelRot,
          trainBaseY = trainBaseY
        )

        // Coupler linking coach to locomotive
        drawLine(
          color = Color(0xFF263238),
          start = Offset(coachX + 80.dp.toPx(), trainBaseY - 14.dp.toPx()),
          end = Offset(trainCenterX - 20.dp.toPx(), trainBaseY - 14.dp.toPx()),
          strokeWidth = 4.dp.toPx()
        )

        // Draw Selected Engine Body
        drawEngine(
          engineId = engineId,
          x = trainCenterX - 20.dp.toPx(),
          y = trainBaseY - 56.dp.toPx(),
          primaryColor = primaryColor,
          secondaryColor = secondaryColor,
          wheelRot = wheelRot,
          trainBaseY = trainBaseY
        )
      }

      // Wreck cartoon soot and sparks
      if (isWrecked) {
        drawCircle(
          color = Color(0x99212121),
          radius = 16.dp.toPx(),
          center = Offset(smokestackX - 10.dp.toPx(), smokestackY - 15.dp.toPx())
        )
        drawCircle(
          color = Color(0x66757575),
          radius = 24.dp.toPx(),
          center = Offset(smokestackX - 25.dp.toPx(), smokestackY - 30.dp.toPx())
        )
        drawCircle(
          color = Color(0xFFFFD54F),
          radius = 4.dp.toPx(),
          center = Offset(trainCenterX + 45.dp.toPx(), trainBaseY - 40.dp.toPx())
        )
        drawCircle(
          color = Color(0xFFFF5252),
          radius = 3.dp.toPx(),
          center = Offset(trainCenterX + 60.dp.toPx(), trainBaseY - 25.dp.toPx())
        )
      }

      // 4. Pause Dim Overlay
      if (isPaused) {
        drawRect(
          color = Color(0x66000000),
          size = Size(w, h)
        )
      }
    }

    // Top Label in View
    val topLabel = if (isWrecked) {
      "💥 LOCOMOTIVE DERAILED • EXCESSIVE SPEED"
    } else {
      "🚂 LIVE TRAIN PROFILE • ${TrackWorldData.availableEngines.firstOrNull { it.id == engineId }?.name ?: "Express"}"
    }
    val topColor = if (isWrecked) Color(0xFFFF5252) else Color(0xFFFFD54F)

    Text(
      text = topLabel,
      color = topColor,
      fontSize = 11.sp,
      fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 12.dp, top = 6.dp)
    )

    if (isPaused) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .background(Color(0xD9B71C1C), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "⏸️ GAME PAUSED • TAP RESUME TO CONTINUE",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
      }
    }
  }
}

private fun DrawScope.drawEngine(
  engineId: String,
  x: Float,
  y: Float,
  primaryColor: Color,
  secondaryColor: Color,
  wheelRot: Float,
  trainBaseY: Float
) {
  when (engineId) {
    "streamliner" -> {
      // Aerodynamic bullet shape
      val bodyPath = Path().apply {
        moveTo(x, y + 42.dp.toPx())
        lineTo(x + 90.dp.toPx(), y + 42.dp.toPx())
        quadraticBezierTo(x + 120.dp.toPx(), y + 42.dp.toPx(), x + 120.dp.toPx(), y + 25.dp.toPx())
        quadraticBezierTo(x + 115.dp.toPx(), y + 8.dp.toPx(), x + 85.dp.toPx(), y + 8.dp.toPx())
        lineTo(x, y + 8.dp.toPx())
        close()
      }
      drawPath(bodyPath, primaryColor)
      // Streamline stripe
      drawRect(secondaryColor, topLeft = Offset(x, y + 22.dp.toPx()), size = Size(105.dp.toPx(), 6.dp.toPx()))
      // Driver windshield (smaller, sleek window)
      drawRoundRect(
        Color(0xFFE0F7FA),
        topLeft = Offset(x + 86.dp.toPx(), y + 14.dp.toPx()),
        size = Size(15.dp.toPx(), 8.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
      )
    }
    "dino_express" -> {
      // Dino Head & Spikes
      drawRoundRect(
        primaryColor,
        topLeft = Offset(x, y + 8.dp.toPx()),
        size = Size(90.dp.toPx(), 36.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
      )
      // Dino Snout
      drawRoundRect(
        primaryColor,
        topLeft = Offset(x + 85.dp.toPx(), y + 18.dp.toPx()),
        size = Size(28.dp.toPx(), 26.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
      )
      // Dino Teeth
      for (i in 0..2) {
        val tx = x + 90.dp.toPx() + (i * 8.dp.toPx())
        drawPath(
          Path().apply {
            moveTo(tx, y + 44.dp.toPx())
            lineTo(tx + 4.dp.toPx(), y + 36.dp.toPx())
            lineTo(tx + 8.dp.toPx(), y + 44.dp.toPx())
            close()
          },
          Color.White
        )
      }
      // Spikes along top
      for (i in 0..4) {
        val sx = x + 15.dp.toPx() + (i * 15.dp.toPx())
        drawPath(
          Path().apply {
            moveTo(sx, y + 8.dp.toPx())
            lineTo(sx + 6.dp.toPx(), y - 4.dp.toPx())
            lineTo(sx + 12.dp.toPx(), y + 8.dp.toPx())
            close()
          },
          secondaryColor
        )
      }
      // Dino Eye
      drawCircle(Color.Yellow, radius = 5.dp.toPx(), center = Offset(x + 94.dp.toPx(), y + 24.dp.toPx()))
      drawCircle(Color.Black, radius = 2.5.dp.toPx(), center = Offset(x + 95.dp.toPx(), y + 24.dp.toPx()))
    }
    "caterpillar" -> {
      // Caterpillar smiling segments
      for (i in 0..3) {
        val cx = x + 15.dp.toPx() + (i * 24.dp.toPx())
        drawCircle(
          color = if (i % 2 == 0) primaryColor else secondaryColor,
          radius = 18.dp.toPx(),
          center = Offset(cx, y + 24.dp.toPx())
        )
      }
      // Head with eyes and antenna
      val headX = x + 98.dp.toPx()
      drawCircle(primaryColor, radius = 20.dp.toPx(), center = Offset(headX, y + 22.dp.toPx()))
      drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(headX + 6.dp.toPx(), y + 16.dp.toPx()))
      drawCircle(Color.Black, radius = 2.5.dp.toPx(), center = Offset(headX + 7.dp.toPx(), y + 16.dp.toPx()))
      // Antenna
      drawLine(
        color = secondaryColor,
        start = Offset(headX, y + 4.dp.toPx()),
        end = Offset(headX + 8.dp.toPx(), y - 10.dp.toPx()),
        strokeWidth = 3.dp.toPx()
      )
      drawCircle(Color(0xFFFFD54F), radius = 4.dp.toPx(), center = Offset(headX + 8.dp.toPx(), y - 10.dp.toPx()))
    }
    "golden_royal" -> {
      // Royal locomotive with Crown and gold filigree
      drawRoundRect(
        primaryColor,
        topLeft = Offset(x, y + 10.dp.toPx()),
        size = Size(100.dp.toPx(), 34.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
      )
      // Gold trim border
      drawRoundRect(
        Color(0xFFFFD700),
        topLeft = Offset(x + 2.dp.toPx(), y + 12.dp.toPx()),
        size = Size(96.dp.toPx(), 30.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()),
        style = Stroke(width = 2.5.dp.toPx())
      )
      // Crown on Cab
      drawPath(
        Path().apply {
          moveTo(x + 10.dp.toPx(), y + 10.dp.toPx())
          lineTo(x + 10.dp.toPx(), y - 2.dp.toPx())
          lineTo(x + 16.dp.toPx(), y + 4.dp.toPx())
          lineTo(x + 22.dp.toPx(), y - 6.dp.toPx())
          lineTo(x + 28.dp.toPx(), y + 4.dp.toPx())
          lineTo(x + 34.dp.toPx(), y - 2.dp.toPx())
          lineTo(x + 34.dp.toPx(), y + 10.dp.toPx())
          close()
        },
        Color(0xFFFFD700)
      )
      // Headlight
      drawCircle(Color(0xFFFFD700), radius = 7.dp.toPx(), center = Offset(x + 102.dp.toPx(), y + 26.dp.toPx()))
      drawCircle(Color(0xFFFFF9C4), radius = 4.dp.toPx(), center = Offset(x + 102.dp.toPx(), y + 26.dp.toPx()))
    }
    else -> {
      // Classic Steam Choo-Choo
      // Cab
      drawRect(
        primaryColor,
        topLeft = Offset(x, y + 2.dp.toPx()),
        size = Size(38.dp.toPx(), 42.dp.toPx())
      )
      // Cab Window (smaller, charming locomotive window)
      drawRoundRect(
        Color(0xFFE0F7FA),
        topLeft = Offset(x + 10.dp.toPx(), y + 10.dp.toPx()),
        size = Size(13.dp.toPx(), 11.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5.dp.toPx())
      )
      // Conductor inside waving
      drawCircle(Color(0xFFFFCC80), radius = 3.5.dp.toPx(), center = Offset(x + 16.5.dp.toPx(), y + 15.5.dp.toPx()))
      drawCircle(Color(0xFF0288D1), radius = 2.dp.toPx(), center = Offset(x + 16.5.dp.toPx(), y + 12.5.dp.toPx()))

      // Boiler
      drawRoundRect(
        primaryColor,
        topLeft = Offset(x + 36.dp.toPx(), y + 14.dp.toPx()),
        size = Size(64.dp.toPx(), 30.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
      )
      // Brass boiler rings
      drawRect(secondaryColor, topLeft = Offset(x + 55.dp.toPx(), y + 14.dp.toPx()), size = Size(4.dp.toPx(), 30.dp.toPx()))
      drawRect(secondaryColor, topLeft = Offset(x + 75.dp.toPx(), y + 14.dp.toPx()), size = Size(4.dp.toPx(), 30.dp.toPx()))

      // Smokestack / Chimney
      drawRect(
        Color(0xFF263238),
        topLeft = Offset(x + 78.dp.toPx(), y - 8.dp.toPx()),
        size = Size(14.dp.toPx(), 22.dp.toPx())
      )
      drawRect(
        secondaryColor,
        topLeft = Offset(x + 75.dp.toPx(), y - 10.dp.toPx()),
        size = Size(20.dp.toPx(), 4.dp.toPx())
      )

      // Brass Bell & Dome
      drawCircle(Color(0xFFFFB300), radius = 6.dp.toPx(), center = Offset(x + 50.dp.toPx(), y + 14.dp.toPx()))

      // Golden Headlight
      drawCircle(Color(0xFFFFB300), radius = 6.dp.toPx(), center = Offset(x + 101.dp.toPx(), y + 26.dp.toPx()))
      drawCircle(Color(0xFFFFF9C4), radius = 3.5.dp.toPx(), center = Offset(x + 101.dp.toPx(), y + 26.dp.toPx()))

      // Cowcatcher
      drawPath(
        Path().apply {
          moveTo(x + 95.dp.toPx(), y + 42.dp.toPx())
          lineTo(x + 112.dp.toPx(), y + 42.dp.toPx())
          lineTo(x + 104.dp.toPx(), y + 34.dp.toPx())
          close()
        },
        Color(0xFFFFB300)
      )
    }
  }

  // Wheels & Connecting Piston Rod
  val wheelRadius = 11.dp.toPx()
  val wheelCenters = listOf(
    Offset(x + 18.dp.toPx(), trainBaseY - wheelRadius),
    Offset(x + 48.dp.toPx(), trainBaseY - wheelRadius),
    Offset(x + 82.dp.toPx(), trainBaseY - wheelRadius)
  )

  wheelCenters.forEach { center ->
    drawWheel(center, wheelRadius, wheelRot)
  }

  // Connecting Rod linking wheels
  val rodOffsetAngle = Math.toRadians(wheelRot.toDouble())
  val rodR = wheelRadius * 0.55f
  val rDx = (rodR * cos(rodOffsetAngle)).toFloat()
  val rDy = (rodR * sin(rodOffsetAngle)).toFloat()

  drawLine(
    color = Color(0xFFCFD8DC),
    start = Offset(wheelCenters.first().x + rDx, wheelCenters.first().y + rDy),
    end = Offset(wheelCenters.last().x + rDx, wheelCenters.last().y + rDy),
    strokeWidth = 4.dp.toPx(),
    cap = StrokeCap.Round
  )
}

private fun DrawScope.drawCoach(
  x: Float,
  y: Float,
  primaryColor: Color,
  secondaryColor: Color,
  cargoEmoji: String?,
  wheelRot: Float,
  trainBaseY: Float
) {
  // Coach Body
  drawRoundRect(
    primaryColor,
    topLeft = Offset(x, y + 4.dp.toPx()),
    size = Size(80.dp.toPx(), 38.dp.toPx()),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
  )
  drawRect(
    secondaryColor,
    topLeft = Offset(x, y + 22.dp.toPx()),
    size = Size(80.dp.toPx(), 4.dp.toPx())
  )

  // Coach Windows (smaller, elegant passenger train windows)
  for (i in 0..2) {
    val wx = x + 12.dp.toPx() + (i * 22.dp.toPx())
    drawRoundRect(
      Color(0xFF263238),
      topLeft = Offset(wx - 1.dp.toPx(), y + 9.dp.toPx()),
      size = Size(12.dp.toPx(), 9.dp.toPx()),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5.dp.toPx())
    )
    drawRoundRect(
      Color(0xFFE0F7FA),
      topLeft = Offset(wx, y + 10.dp.toPx()),
      size = Size(10.dp.toPx(), 7.dp.toPx()),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
    )
    // Animal Passengers peeking out
    val animalColors = listOf(Color(0xFF8D6E63), Color(0xFFFFA726), Color(0xFFB0BEC5))
    drawCircle(
      color = animalColors[i % animalColors.size],
      radius = 2.5.dp.toPx(),
      center = Offset(wx + 5.dp.toPx(), y + 14.dp.toPx())
    )
  }

  // Coach Wheels
  val wheelRadius = 9.dp.toPx()
  val c1 = Offset(x + 18.dp.toPx(), trainBaseY - wheelRadius)
  val c2 = Offset(x + 62.dp.toPx(), trainBaseY - wheelRadius)
  drawWheel(c1, wheelRadius, wheelRot)
  drawWheel(c2, wheelRadius, wheelRot)
}

private fun DrawScope.drawWheel(center: Offset, radius: Float, rot: Float) {
  // Wheel Rim & Flange
  drawCircle(Color(0xFF21272B), radius = radius, center = center)
  drawCircle(Color(0xFFCFD8DC), radius = radius * 0.82f, center = center)
  drawCircle(Color(0xFF37474F), radius = radius * 0.65f, center = center)

  // Rotating Spokes, Counterweight & Outer Bolt
  rotate(degrees = rot, pivot = center) {
    // Semi-circle counterweight crescent (classic locomotive driving wheel)
    drawArc(
      color = Color(0xFF21272B),
      startAngle = 135f,
      sweepAngle = 90f,
      useCenter = true,
      topLeft = Offset(center.x - radius * 0.64f, center.y - radius * 0.64f),
      size = Size(radius * 1.28f, radius * 1.28f)
    )

    // Contrasting Spokes
    for (i in 0..5) {
      val angle = (i * 30f)
      val rad = Math.toRadians(angle.toDouble())
      val dx = (radius * 0.62f * cos(rad)).toFloat()
      val dy = (radius * 0.62f * sin(rad)).toFloat()
      drawLine(
        color = Color(0xFF1E272C),
        start = Offset(center.x - dx, center.y - dy),
        end = Offset(center.x + dx, center.y + dy),
        strokeWidth = 2.dp.toPx()
      )
    }

    // Outer wheel rim pin/bolt dot (makes rotation instantly recognizable!)
    val pinRad = Math.toRadians(0.0)
    val pinX = center.x + (radius * 0.52f * cos(pinRad)).toFloat()
    val pinY = center.y + (radius * 0.52f * sin(pinRad)).toFloat()
    drawCircle(Color(0xFFFFD54F), radius = 2.2.dp.toPx(), center = Offset(pinX, pinY))
  }

  // Central Axle Hub
  drawCircle(Color(0xFF263238), radius = radius * 0.35f, center = center)
  drawCircle(Color(0xFFFFB300), radius = radius * 0.20f, center = center)
}

private fun DrawScope.drawSmokePuffs(
  smokestackX: Float,
  smokestackY: Float,
  smokeAnim: Float,
  smokeId: String,
  isMoving: Boolean
) {
  val smokeEffect = TrackWorldData.availableSmokes.firstOrNull { it.id == smokeId }
    ?: TrackWorldData.availableSmokes.first()

  val numPuffs = 5
  for (i in 0 until numPuffs) {
    val progress = ((smokeAnim + (i.toFloat() / numPuffs)) % 1f)
    // Wobble float effect
    val wobbleX = (sin((progress * 12f) + i) * 7.dp.toPx())
    val wobbleY = (cos((progress * 8f) + i) * 4.dp.toPx())

    // Rises up from chimney and drifts backwards with velocity
    val driftDist = if (isMoving) 95.dp.toPx() else 55.dp.toPx()
    val puffX = smokestackX - (progress * driftDist) + wobbleX
    val puffY = smokestackY - (progress * 58.dp.toPx()) + wobbleY
    val puffRadius = (6.dp.toPx() + (progress * 16.dp.toPx()))
    val alpha = (1f - (progress * progress)).coerceIn(0f, 1f)

    when (smokeEffect.type) {
      com.example.model.SmokeType.BUBBLES -> {
        // Translucent iridescent soap bubble with rainbow sheen & specular gleam!
        // 1. Transparent watery interior
        drawCircle(
          color = Color(0x3580DEEA).copy(alpha = alpha * 0.35f),
          radius = puffRadius,
          center = Offset(puffX, puffY)
        )
        // 2. Cyan iridescent outer ring
        drawCircle(
          color = Color(0xFF80DEEA).copy(alpha = alpha * 0.85f),
          radius = puffRadius,
          center = Offset(puffX, puffY),
          style = Stroke(width = 1.8.dp.toPx())
        )
        // 3. Pink/magenta iridescent inner sheen
        drawCircle(
          color = Color(0xFFF48FB1).copy(alpha = alpha * 0.65f),
          radius = puffRadius * 0.90f,
          center = Offset(puffX, puffY),
          style = Stroke(width = 1.2.dp.toPx())
        )
        // 4. White curved crescent highlight on upper-left rim
        drawArc(
          color = Color.White.copy(alpha = alpha * 0.92f),
          startAngle = 195f,
          sweepAngle = 75f,
          useCenter = false,
          topLeft = Offset(puffX - puffRadius * 0.78f, puffY - puffRadius * 0.78f),
          size = Size(puffRadius * 1.56f, puffRadius * 1.56f),
          style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )
        // 5. Specular shine dot on lower-right rim
        drawCircle(
          color = Color.White.copy(alpha = alpha * 0.85f),
          radius = (puffRadius * 0.16f).coerceAtLeast(1.5f),
          center = Offset(puffX + puffRadius * 0.45f, puffY + puffRadius * 0.45f)
        )
      }

      com.example.model.SmokeType.HEARTS -> {
        // Floating love heart
        val heartSize = puffRadius * 1.3f
        val pink = smokeEffect.primaryColor.copy(alpha = alpha * 0.85f)
        // Two lobes
        drawCircle(pink, radius = heartSize * 0.38f, center = Offset(puffX - heartSize * 0.28f, puffY - heartSize * 0.15f))
        drawCircle(pink, radius = heartSize * 0.38f, center = Offset(puffX + heartSize * 0.28f, puffY - heartSize * 0.15f))
        // Bottom tip
        val path = Path().apply {
          moveTo(puffX - heartSize * 0.55f, puffY - heartSize * 0.1f)
          lineTo(puffX + heartSize * 0.55f, puffY - heartSize * 0.1f)
          lineTo(puffX, puffY + heartSize * 0.55f)
          close()
        }
        drawPath(path, color = pink)
        // Gleam dot
        drawCircle(Color.White.copy(alpha = alpha * 0.8f), radius = 1.8.dp.toPx(), center = Offset(puffX - heartSize * 0.25f, puffY - heartSize * 0.22f))
      }

      com.example.model.SmokeType.STARS -> {
        // Sparkling four-point starlight
        val starR = puffRadius * 1.25f
        val starColor = smokeEffect.primaryColor.copy(alpha = alpha * 0.9f)
        val path = Path().apply {
          moveTo(puffX, puffY - starR)
          quadraticTo(puffX, puffY, puffX + starR, puffY)
          quadraticTo(puffX, puffY, puffX, puffY + starR)
          quadraticTo(puffX, puffY, puffX - starR, puffY)
          quadraticTo(puffX, puffY, puffX, puffY - starR)
          close()
        }
        drawPath(path, color = starColor)
        drawCircle(Color.White.copy(alpha = alpha), radius = starR * 0.35f, center = Offset(puffX, puffY))
      }

      com.example.model.SmokeType.MUSIC -> {
        // Musical note
        val noteColor = smokeEffect.primaryColor.copy(alpha = alpha * 0.9f)
        val noteHeadR = puffRadius * 0.42f
        // Tilted notehead
        drawCircle(noteColor, radius = noteHeadR, center = Offset(puffX, puffY + noteHeadR))
        // Note stem
        drawLine(
          color = noteColor,
          start = Offset(puffX + noteHeadR * 0.8f, puffY + noteHeadR),
          end = Offset(puffX + noteHeadR * 0.8f, puffY - noteHeadR * 2.2f),
          strokeWidth = 2.dp.toPx()
        )
        // Note flag
        drawLine(
          color = noteColor,
          start = Offset(puffX + noteHeadR * 0.8f, puffY - noteHeadR * 2.2f),
          end = Offset(puffX + noteHeadR * 2.4f, puffY - noteHeadR * 1.2f),
          strokeWidth = 2.5.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      com.example.model.SmokeType.SPARKLES -> {
        // Magic fairy dust diamond cross
        val sparkR = puffRadius * 1.1f
        val sparkColor = smokeEffect.primaryColor.copy(alpha = alpha * 0.85f)
        drawLine(
          color = sparkColor,
          start = Offset(puffX, puffY - sparkR),
          end = Offset(puffX, puffY + sparkR),
          strokeWidth = 2.dp.toPx()
        )
        drawLine(
          color = sparkColor,
          start = Offset(puffX - sparkR, puffY),
          end = Offset(puffX + sparkR, puffY),
          strokeWidth = 2.dp.toPx()
        )
        drawCircle(Color.White.copy(alpha = alpha * 0.95f), radius = 2.5.dp.toPx(), center = Offset(puffX, puffY))
        drawCircle(smokeEffect.secondaryColor.copy(alpha = alpha * 0.5f), radius = sparkR * 0.6f, center = Offset(puffX, puffY))
      }

      com.example.model.SmokeType.FIRE -> {
        // Flame puffs with floating embers
        val flameColor = if (progress < 0.35f) Color(0xFFFFD600) else if (progress < 0.7f) Color(0xFFFF6D00) else Color(0xFFDD2C00)
        drawCircle(flameColor.copy(alpha = alpha * 0.85f), radius = puffRadius, center = Offset(puffX, puffY))
        drawCircle(Color(0xFFFFF9C4).copy(alpha = alpha * 0.9f), radius = puffRadius * 0.45f, center = Offset(puffX, puffY))
        // Miniature rising ember spark
        val emberX = puffX + ((sin(progress * 25f + i) * 12.dp.toPx()))
        val emberY = puffY - (progress * 15.dp.toPx())
        drawCircle(Color(0xFFFFEA00).copy(alpha = alpha), radius = 1.8.dp.toPx(), center = Offset(emberX, emberY))
      }

      com.example.model.SmokeType.RAINBOW -> {
        // Rainbow mist cycling spectrum colors
        val rainbowColors = listOf(
          Color(0xFFFF1744), Color(0xFFFF9100), Color(0xFFFFEA00),
          Color(0xFF00E676), Color(0xFF00E5FF), Color(0xFFD500F9)
        )
        val color = rainbowColors[(i + (progress * 3).toInt()) % rainbowColors.size]
        drawCircle(color.copy(alpha = alpha * 0.75f), radius = puffRadius, center = Offset(puffX, puffY))
        drawCircle(Color.White.copy(alpha = alpha * 0.5f), radius = puffRadius * 0.5f, center = Offset(puffX - 2.dp.toPx(), puffY - 2.dp.toPx()))
      }

      else -> {
        // PUFFS (Classic or Colored)
        val primary = smokeEffect.primaryColor.copy(alpha = alpha * 0.78f)
        val secondary = smokeEffect.secondaryColor.copy(alpha = alpha * 0.60f)
        // Main puffy body with overlapping cloud lobes
        drawCircle(secondary, radius = puffRadius, center = Offset(puffX, puffY))
        drawCircle(primary, radius = puffRadius * 0.85f, center = Offset(puffX - 2.dp.toPx(), puffY - 2.dp.toPx()))
        drawCircle(Color.White.copy(alpha = alpha * 0.45f), radius = puffRadius * 0.45f, center = Offset(puffX - 4.dp.toPx(), puffY - 4.dp.toPx()))
      }
    }
  }
}
