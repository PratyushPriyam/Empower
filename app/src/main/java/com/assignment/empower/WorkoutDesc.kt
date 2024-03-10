package com.assignment.empower

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale  // Import for Locale

class WorkoutDesc : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var descriptionText: TextView
    private lateinit var speakFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        window.enterTransition = Slide(Gravity.LEFT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_desc)

        val imageUrl = intent.getStringExtra("workoutImgUrl")
        val name = intent.getStringExtra("workoutName")
        val description = intent.getStringExtra("workoutDescription")

        val imageView = findViewById<ImageView>(R.id.descImg)
        descriptionText = findViewById<TextView>(R.id.descTxt)
        val descriptionName = findViewById<TextView>(R.id.descName)
        speakFab = findViewById(R.id.speakFab)
        val backBtn = findViewById<AppCompatButton>(R.id.backBtn)

        backBtn.setOnClickListener {
            finishAfterTransition()
        }

        Glide.with(this).load(imageUrl).into(imageView)
        descriptionText.text = description
        descriptionName.text = name

        // Initialize TextToSpeech engine
        textToSpeech = TextToSpeech(this, this) // 'this' refers to the current activity

        // Set click listener for the image
        speakFab.setOnClickListener {
            speakDescription()
        }
    }

    // Handle TextToSpeech initialization
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language (optional, adjust based on your needs)
            val locale = Locale.US
            textToSpeech.setLanguage(locale)
        }
    }

    // Function to speak the description text
    private fun speakDescription() {
        val descriptionToSpeak = descriptionText.text.toString()
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop() // Stop any ongoing speech before speaking new description
        }
        textToSpeech.speak(descriptionToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Release resources when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
    }
}
