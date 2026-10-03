package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object TrainAudio {
  private const val sampleRate = 22050
  private val scope = CoroutineScope(Dispatchers.Default)
  private var activeTrack: AudioTrack? = null
  private val trackLock = Any()

  fun playWhistle() {
    scope.launch {
      try {
        val durationSec = 0.8f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 440.0 // A4
        val f2 = 554.37 // C#5
        val f3 = 659.25 // E5

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = when {
            t < 0.08 -> t / 0.08
            t > 0.6 -> (durationSec - t) / 0.2
            else -> 1.0
          }
          val sampleVal = (sin(2.0 * PI * f1 * t) * 0.4 +
              sin(2.0 * PI * f2 * t) * 0.35 +
              sin(2.0 * PI * f3 * t) * 0.25 +
              (Random.nextDouble() - 0.5) * 0.08) * envelope * 28000.0

          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playChug(isFast: Boolean) {
    scope.launch {
      try {
        val durationSec = if (isFast) 0.12f else 0.18f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-t * (if (isFast) 35.0 else 22.0))
          val noise = (Random.nextDouble() - 0.5) * 2.0
          val lowThump = sin(2.0 * PI * (if (isFast) 85.0 else 60.0) * t) * 0.7
          val sampleVal = (noise * 0.6 + lowThump) * envelope * 24000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playBrakeHiss() {
    scope.launch {
      try {
        val durationSec = 0.7f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = (1.0 - t / durationSec) * 0.8
          val noise = (Random.nextDouble() - 0.5) * 2.0
          val sampleVal = noise * envelope * 22000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playBell() {
    scope.launch {
      try {
        val durationSec = 0.6f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        val f = 1174.66 // D6

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-t * 7.0)
          val sampleVal = (sin(2.0 * PI * f * t) * 0.7 + sin(2.0 * PI * f * 2.76 * t) * 0.3) * envelope * 26000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playRewardFanfare() {
    scope.launch {
      try {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
        for (f in notes) {
          val durationSec = 0.14f
          val numSamples = (sampleRate * durationSec).toInt()
          val buffer = ShortArray(numSamples)
          for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = (1.0 - t / durationSec)
            val sampleVal = sin(2.0 * PI * f * t) * envelope * 25000.0
            buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
          }
          playRawPcm(buffer)
          delay((durationSec * 1000).toLong())
        }
      } catch (_: Throwable) {}
    }
  }

  fun playCautionDing() {
    scope.launch {
      try {
        val durationSec = 0.25f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        val f = 880.0

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-t * 12.0)
          val sampleVal = sin(2.0 * PI * f * t) * envelope * 22000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playWiperSqueak() {
    scope.launch {
      try {
        val durationSec = 0.22f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val f = 600.0 + sin(t * 40.0) * 200.0
          val envelope = (1.0 - t / durationSec) * 0.5
          val sampleVal = sin(2.0 * PI * f * t) * envelope * 18000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playAdChime() {
    scope.launch {
      try {
        val durationSec = 0.5f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 523.25 // C5
        val f2 = 659.25 // E5
        val f3 = 783.99 // G5
        val f4 = 1046.50 // C6

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-t * 6.0)
          val sampleVal = (sin(2.0 * PI * f1 * t) * 0.25 +
              sin(2.0 * PI * f2 * t) * 0.25 +
              sin(2.0 * PI * f3 * t) * 0.25 +
              sin(2.0 * PI * f4 * t) * 0.25) * envelope * 24000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playDerailmentScreech() {
    scope.launch {
      try {
        val durationSec = 0.95f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        // Screeching high-pitched dissonant metallic friction and rumble
        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = when {
            t < 0.1 -> t / 0.1
            t > 0.7 -> (durationSec - t) / 0.25
            else -> 1.0
          }
          val f1 = 1800.0 + sin(t * 70.0) * 450.0
          val f2 = 2340.0 + sin(t * 90.0) * 600.0
          val f3 = 920.0 + cos(t * 50.0) * 200.0
          val noise = (Random.nextDouble() - 0.5) * 1.8
          val sampleVal = (sin(2.0 * PI * f1 * t) * 0.35 +
              sin(2.0 * PI * f2 * t) * 0.25 +
              sin(2.0 * PI * f3 * t) * 0.20 +
              noise * 0.45) * envelope * 27000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playCrashWreck() {
    scope.launch {
      try {
        val durationSec = 1.1f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        // Heavy low-frequency metal impact thump + noisy crunch + steam escape
        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val impactEnv = exp(-t * 14.0)
          val steamEnv = if (t > 0.15) exp(-(t - 0.15) * 3.5) else 0.0

          val lowThud = sin(2.0 * PI * (65.0 - t * 25.0) * t) * 0.8
          val midCrunch = sin(2.0 * PI * 180.0 * t) * 0.4
          val metalNoise = (Random.nextDouble() - 0.5) * 2.2
          val steamHiss = (Random.nextDouble() - 0.5) * 1.4

          val sampleVal = ((lowThud + midCrunch + metalNoise * 0.6) * impactEnv +
              steamHiss * steamEnv * 0.7) * 29000.0
          buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  fun playCraneClang() {
    scope.launch {
      try {
        val durationSec = 0.75f
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 880.0
        val f2 = 1320.0
        val f3 = 1760.0

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-t * 6.5)
          val metalRing = (sin(2.0 * PI * f1 * t) * 0.5 +
              sin(2.0 * PI * f2 * t) * 0.35 +
              sin(2.0 * PI * f3 * t) * 0.25) * envelope * 26000.0
          buffer[i] = metalRing.toInt().coerceIn(-32768, 32767).toShort()
        }

        playRawPcm(buffer)
      } catch (_: Throwable) {}
    }
  }

  private fun playRawPcm(buffer: ShortArray) {
    try {
      synchronized(trackLock) {
        try {
          activeTrack?.stop()
          activeTrack?.release()
        } catch (_: Throwable) {}
        activeTrack = null
      }

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      if (track.state != AudioTrack.STATE_INITIALIZED) {
        try { track.release() } catch (_: Throwable) {}
        return
      }

      track.write(buffer, 0, buffer.size)
      track.play()

      synchronized(trackLock) {
        activeTrack = track
      }

      val durationMs = ((buffer.size.toDouble() / sampleRate) * 1000.0).toLong() + 60L
      scope.launch {
        delay(durationMs)
        synchronized(trackLock) {
          if (activeTrack == track) {
            try {
              track.stop()
              track.release()
            } catch (_: Throwable) {}
            activeTrack = null
          }
        }
      }
    } catch (_: Throwable) {}
  }
}
