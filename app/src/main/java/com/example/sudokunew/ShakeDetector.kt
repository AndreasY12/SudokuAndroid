package com.example.sudokunew

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.pow
import kotlin.math.sqrt

//Shake Detector Class from https://github.com/AlShevelev
//https://gist.github.com/AlShevelev/afae84d0d9245eb6a8044f20bffa63f5

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {
    private companion object {
        const val SHAKE_THRESHOLD_GRAVITY = 2.7f          // In "Gs" (one Earth gravity unit)

        const val SHAKE_SLOP_TIME = 500                   // In [ms]
        const val SHAKE_COUNT_RESET_TIME = 3000
    }

    private var shakeTimestamp: Long = 0
    private var shakesCount: Int = 0

   // private var lastShakeTime: Long = 0
    //private var lastAccel = floatArrayOf(0f, 0f, 0f)
    //private var isFirstReading = true

    override fun onSensorChanged(event: SensorEvent) {
        val gX = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = event.values[2] / SensorManager.GRAVITY_EARTH

        // gForce will be close to 1 when there is no movement.
        val gForce = sqrt(gX.pow(2) + gY.pow(2) + gZ.pow(2))

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {

            val now = System.currentTimeMillis()

            // ignore shake events too close to each other (500ms)
            if (shakeTimestamp + SHAKE_SLOP_TIME > now) {
                return
            }

            // reset the shake count after 3 seconds of no shakes
            if (shakeTimestamp + SHAKE_COUNT_RESET_TIME < now) {
                shakesCount = 0
            }

            shakeTimestamp = now
            shakesCount++
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}