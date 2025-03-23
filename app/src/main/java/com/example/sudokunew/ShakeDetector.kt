package com.example.sudokunew

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import kotlin.math.abs

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {
    companion object {
        private const val TAG = "com.example.sudokunew.ShakeDetector"
        private const val SHAKE_THRESHOLD = 5.5f // Lower threshold for easier detection. Maybe adjust this value...
        private const val MIN_TIME_BETWEEN_SHAKES = 4000L // 4 second cooldown
    }

    private var lastShakeTime: Long = 0
    private var lastAccel = floatArrayOf(0f, 0f, 0f)
    private var isFirstReading = true

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        // Get current accelerometer values
        val currentAccel = event.values.clone()

        if (isFirstReading) {
            isFirstReading = false
            lastAccel = currentAccel
            return
        }

        // Check if enough time has passed since last shake
        if (currentTime - lastShakeTime < MIN_TIME_BETWEEN_SHAKES) {
            return
        }

        // Calculate acceleration difference from last reading
        val deltaX = abs(lastAccel[0] - currentAccel[0])
        val deltaY = abs(lastAccel[1] - currentAccel[1])
        val deltaZ = abs(lastAccel[2] - currentAccel[2])

        Log.d(TAG, "Delta acceleration - X: $deltaX, Y: $deltaY, Z: $deltaZ")

        // Check if acceleration exceeds threshold
        if ((deltaX > SHAKE_THRESHOLD && deltaY > SHAKE_THRESHOLD) ||
            (deltaX > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD) ||
            (deltaY > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD)) {

            Log.d(TAG, "Shake detected!")
            lastShakeTime = currentTime
            onShake()
        }

        lastAccel = currentAccel
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}