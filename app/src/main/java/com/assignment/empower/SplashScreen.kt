package com.assignment.empower

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity(), SensorEventListener {

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
//    Sensors
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var tempSensor: Sensor? = null
    private var isSensorRegistered = false

    lateinit var brightnesstv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar = findViewById(R.id.progressBar2) // Assuming "progressBar2" is the progress bar ID
        imageView = findViewById(R.id.imageView)
        imageView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.blink))
        brightnesstv = findViewById(R.id.brightnesstv)

        setUpLightSensor()
        setUpTempSensor()
        // Simulate progress bar filling in 3 seconds
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            var progress = 0

            override fun run() {
                if (progress < 100) {
                    progress += 10
                    progressBar.progress = progress
                    handler.postDelayed(this, 500) // Update every 300 milliseconds
                } else {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    val currentUser = firebaseAuth.currentUser

                    if (currentUser == null) {
                        startActivity(Intent(this@SplashScreen, Login::class.java))
                    } else {
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    }
                    finish() // Finish the splash screen after navigation
                }
            }
        }
        handler.post(runnable)
    }
    private fun setUpLightSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    private fun setUpTempSensor() {
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0]
            showToastBasedOnLight(lightValue)
        }
    }

    private fun getBrightnessLevel(lightValue: Float): String {
        return when (lightValue.toInt()) {
            0 -> "Dark"
            in 1..10 -> "Dark"
            in 11..50 -> "Normal"
            in 51..5000 -> "Normal"
            in 5001..25000 -> "Bright"
            else -> "Bright"
        }
    }

    private fun showToastBasedOnLight(lightValue: Float) {
        val brightnessLevel = getBrightnessLevel(lightValue)
        val message = when (brightnessLevel) {
            "Bright" -> "It's too bright outside! Increase phone brightness for better viewing."
            "Dark" -> "It's dark outside! Reduce phone brightness to avoid eye strain."
            else -> "Just the right amount of light" // No toast for other levels
        }
        if (message.isNotEmpty()) {
            brightnesstv.text = message
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // (Optional) handle sensor accuracy changes
    }

    override fun onResume() {
        super.onResume()
        if (!isSensorRegistered) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
            isSensorRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorRegistered) {
            sensorManager.unregisterListener(this)
            isSensorRegistered = false
        }
    }
}
