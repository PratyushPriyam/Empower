package com.assignment.empower

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class History : AppCompatActivity() {

    private var workoutList: MutableList<WorkoutModelClass> = mutableListOf()
    private lateinit var adapter: WorkoutAdapter
    private lateinit var datesHorizontalRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.RIGHT)
        setContentView(R.layout.activity_history)

        val backBtn = findViewById<CardView>(R.id.backBtn)
        backBtn.setOnClickListener { finishAfterTransition() }

        val recyclerView = findViewById<RecyclerView>(R.id.gridView)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val fetchButton = findViewById<Button>(R.id.fetchButton) // Assuming ID for button

        // Initialize dates recycler view
        datesHorizontalRV = findViewById(R.id.datesHorizontalRV) // Assuming ID for dates recycler view
        datesHorizontalRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fetchDates() // Call new function to fetch dates

        adapter = WorkoutAdapter(workoutList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val calendar = Calendar.getInstance()

        // Implement date picker dialog on click of date EditText
        val datePicker = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = getDateString(year, monthOfYear, dayOfMonth)
                dateEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateEditText.setOnClickListener { datePicker.show() }

        fetchButton.setOnClickListener {
            val selectedDate = dateEditText.text.toString()
            fetchWorkoutData(selectedDate)
        }
    }
    private fun fetchWorkoutData(selectedDate: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: return // Handle case where user is not logged in

        val database = FirebaseDatabase.getInstance().reference
        val appRef = database.child("empower")
        val userRef = appRef.child(userId)

        userRef.child(selectedDate).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutList.clear()
                if (snapshot.exists()) {
                    val savedWorkouts = snapshot.children.map { it.getValue(WorkoutModelClass::class.java)!! }
                    workoutList.addAll(savedWorkouts)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
                val inflater: View = layoutInflater.inflate(R.layout.something_went_wrong_custom_toast, findViewById(R.id.customToastMainLayoutIdWrong))
                val toast = Toast(applicationContext)
                toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                toast.duration = Toast.LENGTH_LONG
                toast.view = inflater
                toast.show()
            }
        })
    }

    private fun getDateString(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }
    private fun fetchDates() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: return // Handle case where user is not logged in

        val database = FirebaseDatabase.getInstance().reference
        val appRef = database.child("empower")
        val userRef = appRef.child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dates = mutableListOf<String>()
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        dates.add(childSnapshot.key.toString()) // Extract date from key
                    }
                }

                // Set dates as adapter data and notify changes
                val datesAdapter = DatesAdapter(dates, this@History) // Pass activity context
                datesHorizontalRV.adapter = datesAdapter
                datesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun onDateClick(date: String) {
        // Update EditText with selected date
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        dateEditText.setText(date)

        // If needed, call fetchWorkoutData(date) again to update displayed workouts
    }

}