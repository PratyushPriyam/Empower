package com.assignment.empower

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var workoutList: MutableList<WorkoutModelClass> = mutableListOf()
    val adapter = WorkoutAdapter(workoutList, this)
    lateinit var progressBar: ProgressBar
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    lateinit var circularprogress: CircularProgressBar
    private var maxSteps: Int = 0
    private var currentSteps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.BOTTOM)
        setContentView(R.layout.activity_main)

        circularprogress = findViewById(R.id.circularprogress)
            loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val subButtonAnimationHorizontal = AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_horizontal)
        val subButtonAnimationVertical = AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_vertical)
        val subButtonAnimationDiagonal = AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_diagonal)
        val addWorkoutBtn = findViewById<Button>(R.id.addWorkoutBtn)

            addWorkoutBtn.setOnClickListener {

                if (findViewById<EditText>(R.id.workoutNameEdt).text.trim().isEmpty() ||
                    findViewById<EditText>(R.id.setsEdt).text.trim().isEmpty() ||
                    findViewById<EditText>(R.id.repsEdt).text.trim().isEmpty()) {
                    Toast.makeText(this, "Fill all entries first", Toast.LENGTH_SHORT).show()
                }
                else {
                    val workoutName = findViewById<EditText>(R.id.workoutNameEdt).text.toString()
                    val sets = findViewById<EditText>(R.id.setsEdt).text.toString().toInt()
                    val reps = findViewById<EditText>(R.id.repsEdt).text.toString().toInt()


                    val newWorkout = WorkoutModelClass(workoutName, sets, reps)
                    workoutList.add(newWorkout) // Add new instance

                    adapter.notifyDataSetChanged()
            }
        }

        // Motivation Card Inflating
        val cardContainer = findViewById<LinearLayout>(R.id.scrollLinearLayout)

        val cardList = listOf(
            Card("https://th.bing.com/th?id=OIP.EwbatycHx_915hcNzd7vRgHaE8&w=306&h=204&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2", "The best project you will ever work on is you."),
            Card("https://th.bing.com/th/id/OIP.DDjElVJ077x2dtyhADU5JgHaEK?w=303&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "You didn't come this far only to come this far."),
            Card("https://th.bing.com/th/id/OIP.KplbMQxWpaawZhffX7GAXAHaEg?w=271&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "Nothing will work unless you do."),
            Card("https://th.bing.com/th/id/OIP.6JIOpIbPh-xH5oaKZn8x5gHaEK?w=305&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "You are much stronger than you think.")
        )

        cardList.forEach { card ->
            val inflatedView = LayoutInflater.from(this).inflate(
                R.layout.custom_motivation_card, cardContainer, false
            )
            Picasso.get()
                .load(card.imageUrl)
                .into(inflatedView.findViewById<ImageView>(R.id.card_image))
            inflatedView.findViewById<TextView>(R.id.card_text).text = card.text
            cardContainer.addView(inflatedView)
        }

        // Progress Bar
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        // Floating action button -> Profile Page
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val fabSubBtn1 = findViewById<FloatingActionButton>(R.id.personFab)
        val fabSubBtn2 = findViewById<FloatingActionButton>(R.id.exploreFab)
        val fabSubBtn3 = findViewById<FloatingActionButton>(R.id.historyFab)
        var isRotated = false

        floatingActionButton.setOnClickListener {
            val anim = RotateAnimation(
                0f,
                45f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.duration = 200
            anim.fillAfter = true
            anim.interpolator = LinearInterpolator()

            val reverseAnim = RotateAnimation(
                45f, // Start from 45 degrees (reversed)
                0f, // Rotate to 0 degrees
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            reverseAnim.duration = 200
            reverseAnim.fillAfter = true
            reverseAnim.interpolator = LinearInterpolator()

            fabSubBtn1.setOnClickListener {
                val profileIntent = Intent(this, Profile::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(profileIntent, options.toBundle())
            }

            fabSubBtn3.setOnClickListener {
                val historyIntent = Intent(this, History::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(historyIntent, options.toBundle())
            }

            fabSubBtn2.setOnClickListener {
                val exploreIntent = Intent(this, Explore::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(exploreIntent, options.toBundle())
            }

            if (isRotated) {
                floatingActionButton.startAnimation(reverseAnim) // Use reverseAnim for reversed rotation
                isRotated = false
                isRotated = false
                fabSubBtn1.visibility = View.GONE
                fabSubBtn2.visibility = View.GONE
                fabSubBtn3.visibility = View.GONE
                fabSubBtn1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_vertical_back))
                fabSubBtn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_diagonal_back))
                fabSubBtn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sub_button_animation_horizantal_back))
            } else {
                floatingActionButton.startAnimation(anim)
                fabSubBtn1.visibility = View.VISIBLE
                fabSubBtn2.visibility = View.VISIBLE
                fabSubBtn3.visibility = View.VISIBLE
                fabSubBtn1.startAnimation(subButtonAnimationVertical)
                fabSubBtn2.startAnimation(subButtonAnimationDiagonal)
                fabSubBtn3.startAnimation(subButtonAnimationHorizontal)
                isRotated = true
            }

        }


        // Recycler View
        val recyclerView = findViewById<RecyclerView>(R.id.recView) // Replace with your RecyclerView's ID

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this) // Set a layout manager (e.g., LinearLayoutManager)

        // Add data to firebase
        val addToFirebaseBtn = findViewById<AppCompatButton>(R.id.addToFirebaseBtn)
        addToFirebaseBtn.setOnClickListener{

            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            saveWorkoutListToFirebase(workoutList)

        }

        // When the app starts, current day's data to be displayed
        val database = FirebaseDatabase.getInstance().reference
        val appRef = database.child("empower")

        // Retrieve user ID and current date
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: return // Handle case where user is not logged in
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Display past workouts for the current date
        appRef.child(userId).child(currentDate).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    workoutList.clear()
                    val savedWorkouts = snapshot.children.map { it.getValue(WorkoutModelClass::class.java)!! }
                    workoutList.addAll(savedWorkouts)
                    progressBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }else {
                    // Handle empty snapshot (no data for the current date)
                    progressBar.visibility = View.GONE
                    val inflater: View = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.customToastMainLayoutId))
                    val toast = Toast(applicationContext)
                    toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = inflater
                    toast.show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })

    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }
    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }
    fun resetSteps() {
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        tv_stepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        tv_stepsTaken.setOnLongClickListener {
            previousTotalSteps = totalSteps
            tv_stepsTaken.text = 0.toString()
            saveData()
            circularprogress.apply {
                setProgressWithAnimation(0F)
            }
            true
        }
    }


    private fun saveWorkoutListToFirebase(workoutList: List<WorkoutModelClass>) {
        // Retrieve user ID (replace with your actual method)
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: return // Handle case where user is not logged in

        val database = FirebaseDatabase.getInstance().reference
        val appRef = database.child("empower")
        val userRef = appRef.child(userId)

        // Convert workout list to list of maps
        val workoutMaps = workoutList.map { workout ->
            hashMapOf(
                "name" to workout.name,
                "sets" to workout.sets,
                "reps" to workout.reps
            )
        }

        // Get current date in YYYY-MM-DD format
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Save the entire workout list to the "workouts" node
        userRef.child(currentDate).setValue(workoutMaps).addOnSuccessListener {
            val inflater: View = layoutInflater.inflate(R.layout.data_saved_custom_toast, findViewById(R.id.customToastMainLayoutIdDatSaved))
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = inflater
            toast.show()
        }.addOnFailureListener {
            val inflater: View = layoutInflater.inflate(R.layout.something_went_wrong_custom_toast, findViewById(R.id.customToastMainLayoutIdWrong))
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = inflater
            toast.show()
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        if (running) {
            totalSteps = event!!.values[0]
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            tv_stepsTaken.text = ("$currentSteps")
            circularprogress.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this implementation
    }
}