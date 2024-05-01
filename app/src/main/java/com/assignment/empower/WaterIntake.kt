package com.assignment.empower

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class WaterIntake : AppCompatActivity() {
    private lateinit var waterInc: Button
    private lateinit var waterProgress: CircularProgressBar
    private lateinit var waterCount: TextView
    private lateinit var previousWaterIntake: TextView
    private var water: Int = 0
    private val sharedPrefName = "water_intake_pref"
    private val previousWaterKey = "previousWater" // Key for the previous day's water count
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_intake)

        // Initialize views
        waterInc = findViewById(R.id.waterinc)
        waterProgress = findViewById(R.id.waterprogress)
        waterCount = findViewById(R.id.watercount)
        previousWaterIntake = findViewById(R.id.previousWaterIntake)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        // Retrieve the current water intake and previous day's water intake from shared preferences
        water = sharedPreferences.getInt("water", 0)
        val previousWater = sharedPreferences.getInt(previousWaterKey, 0)

        // Set up the initial state of the views
        waterCount.text = "$water/10"
        waterProgress.setProgressWithAnimation(water.toFloat())
        previousWaterIntake.text = "You drank $previousWater glasses yesterday"

        // Set up back button
        val backBtn = findViewById<CardView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finishAfterTransition()
        }

        // Set up water increment button
        waterInc.setOnClickListener {
            if (water < 10) {
                water++
                waterCount.text = "$water/10"
                waterProgress.setProgressWithAnimation(water.toFloat())
                saveWaterIntakeToSharedPref(water)
            } else {
                Toast.makeText(this, "10 glasses are enough for a day", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle click and long press on the progress bar
        waterProgress.setOnClickListener {
            Toast.makeText(this, "Long Press to Reset", Toast.LENGTH_SHORT).show()
        }

        waterProgress.setOnLongClickListener {
            resetWaterIntake()
            true // Consume the long press event
        }
    }

    private fun saveWaterIntakeToSharedPref(waterIntake: Int) {
        with(sharedPreferences.edit()) {
            putInt("water", waterIntake)
            apply()
        }
    }

    private fun resetWaterIntake() {
        // Save current day's water intake as the previous day's water intake
        with(sharedPreferences.edit()) {
            putInt(previousWaterKey, water) // Save current day's water intake
            apply()
        }

        // Update previous water intake TextView
        previousWaterIntake.text = "You drank $water glasses yesterday"

        // Reset water intake to 0
        water = 0
        waterCount.text = "$water/10"
        waterProgress.setProgressWithAnimation(water.toFloat())

        // Save the updated water intake (0) to shared preferences
        saveWaterIntakeToSharedPref(water)

        // Show reset toast
        Toast.makeText(this, "Water intake restarted!", Toast.LENGTH_SHORT).show()
    }
}
