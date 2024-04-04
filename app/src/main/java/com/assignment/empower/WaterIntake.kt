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
    private lateinit var waterinc: Button
    private lateinit var waterprogress: CircularProgressBar
    private lateinit var watercount: TextView
    private var water: Int = 0
    private val sharedPrefName = "water_intake_pref" // SharedPreference name
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_intake)

        waterinc = findViewById(R.id.waterinc)
        waterprogress = findViewById(R.id.waterprogress)
        watercount = findViewById(R.id.watercount)

        val backBtn = findViewById<CardView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finishAfterTransition()
//            finish()
        }
        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        water = sharedPreferences.getInt("water", 0)
        watercount.text = "$water/${10}"
        waterprogress.apply {
            setProgressWithAnimation(water.toFloat())
        }

        waterinc.setOnClickListener {
            if (water < 10) {
                water++
                watercount.text = "$water/${10}"
                waterprogress.apply {
                    setProgressWithAnimation(water.toFloat())
                }

                // Save updated water intake to SharedPreferences
                saveWaterIntakeToSharedPref(water)
            } else {
                Toast.makeText(this, "10 glasses are enough for a day", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle click and long click on progresscard
        waterprogress.setOnClickListener {
            Toast.makeText(this, "Long Press to Reset", Toast.LENGTH_SHORT).show()
        }

        waterprogress.setOnLongClickListener {
            resetWaterIntake()
            showResetToast()
            true // Consume the long click event
        }
    }

    private fun saveWaterIntakeToSharedPref(waterIntake: Int) {
        with (sharedPreferences.edit()) {
            putInt("water", waterIntake) // Key "water" and value waterIntake
            apply() // Apply changes asynchronously
        }
    }

    private fun resetWaterIntake() {
        water = 0
        watercount.text = "$water/${10}"
        waterprogress.setProgressWithAnimation(water.toFloat())
        saveWaterIntakeToSharedPref(water)
    }

    private fun showResetToast() {
        Toast.makeText(this, "Water intake restarted!", Toast.LENGTH_SHORT).show()
    }
}
