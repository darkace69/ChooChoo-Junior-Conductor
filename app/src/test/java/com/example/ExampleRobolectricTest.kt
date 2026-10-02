package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ChooChoo Junior Conductor", appName)
  }

  @Test
  fun `verify railway signs exist for all landscape worlds`() {
    val allSigns = com.example.model.TrackWorldData.allRailwaySigns
    org.junit.Assert.assertTrue("Should have multiple railway signs", allSigns.size >= 15)
    val slowSigns = allSigns.filter { it.type == com.example.model.RailwaySignType.SLOW_DOWN }
    val speedSigns = allSigns.filter { it.type == com.example.model.RailwaySignType.SPEED_UP }
    org.junit.Assert.assertTrue("Should have slow down signs", slowSigns.isNotEmpty())
    org.junit.Assert.assertTrue("Should have speed up signs", speedSigns.isNotEmpty())
  }

  @Test
  fun `verify speed notches order and reverse stop progression`() {
    val notches = listOf(
      com.example.model.SpeedNotch.REVERSE,
      com.example.model.SpeedNotch.STOP,
      com.example.model.SpeedNotch.SPEED_1,
      com.example.model.SpeedNotch.SPEED_2,
      com.example.model.SpeedNotch.SPEED_3
    )
    org.junit.Assert.assertEquals(5, notches.size)
    org.junit.Assert.assertEquals(0f, com.example.model.SpeedNotch.STOP.baseSpeedMph, 0.01f)
    org.junit.Assert.assertTrue(com.example.model.SpeedNotch.REVERSE.baseSpeedMph < 0f)
    org.junit.Assert.assertTrue(com.example.model.SpeedNotch.SPEED_3.baseSpeedMph > com.example.model.SpeedNotch.SPEED_1.baseSpeedMph)
  }
}
