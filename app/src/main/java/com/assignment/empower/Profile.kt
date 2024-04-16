package com.assignment.empower

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.RIGHT)
        setContentView(R.layout.activity_profile)


        // Setting profile name.
        val profileNameTxt = findViewById<TextView>(R.id.profileNameTxt)
        val profileImg = findViewById<CardView>(R.id.profileImg)
        profileImg.setOnClickListener {
            startActivity(Intent(this, WaterIntake::class.java))
        }

        // Get current user and extract email address
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) { // Check if user is logged in
            val email = currentUser.email
            if (email != null) {
                profileNameTxt.text = "Welcome: $email" // Display email in TextView
            } else {
                Log.w("ProfileActivity", "Email address not found for current user.")
            }
        } else {
            Log.w("ProfileActivity", "No user logged in.")
        }

        // Last worked out on.
        val lastWorkedOnTxt = findViewById<TextView>(R.id.lastWorkedOnTxt)

        val database = FirebaseDatabase.getInstance().reference
        val currentUser1 = FirebaseAuth.getInstance().currentUser
        val userId = currentUser1?.uid ?: return // Handle case where user is not logged in

        val userRef = database.child("empower").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var mostRecentDate = ""
                for (childSnapshot in snapshot.children) {
                    val date = childSnapshot.key.toString() // Key represents date
                    if (date > mostRecentDate) {
                        mostRecentDate = date
                    }
                }

                if (mostRecentDate.isNotEmpty()) {
                    val formattedDate = formatDate(mostRecentDate) // Format date for display
                    lastWorkedOnTxt.text = "You last worked on: $formattedDate"
                } else {
                    lastWorkedOnTxt.text = "No workouts recorded yet." // Handle no workouts case
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Error reading data: $error")
            }
        })

        val backBtn = findViewById<CardView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finishAfterTransition()
//            finish()
        }

        // Delete My Account.
        val delMyAccBtn = findViewById<Button>(R.id.delMyAcc)
        delMyAccBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Opening Pratyush|Portfolio website.
        val goPremiumBtn = findViewById<Button>(R.id.goPremiumBtn)
        goPremiumBtn.setOnClickListener {
//            val url = "https://pratyushpriyam.github.io/3D-Portfolio/"

            // Open the URL in the default web browser
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(Intent(this, WebView::class.java))
        }
        val logoutBtn = findViewById<CardView>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            val profileIntent = Intent(this, Login::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(profileIntent, options.toBundle())
        }

        countChildrenInCurrentUserNode { numChildren ->
            // Use numChildren to update your text view content
            val noOfDaysWorkedOutTxt = findViewById<TextView>(R.id.noOfDaysWorkedOutTxt)
            noOfDaysWorkedOutTxt.text = "$numChildren"
        }
    }

    fun countChildrenInCurrentUserNode(completion: (Int) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("empower").child(FirebaseAuth.getInstance().currentUser?.uid ?: return)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val numChildren = snapshot.childrenCount
                completion(numChildren.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("MainActivity", "Error counting children: $error")
                completion(0)
            }
        })
    }

    private fun formatDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust format if needed
        val date = sdf.parse(dateString) ?: return "" // Handle parsing errors

        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        return formattedDate
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account permanently? This action cannot be reversed.")
        builder.setPositiveButton("Confirm") { dialog, _ ->
            deleteAccount()
            dialog.dismiss()
            val inflater: View = layoutInflater.inflate(R.layout.account_deleted, findViewById(R.id.customToastMainLayoutIdDelet))
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = inflater
            toast.show()
            startActivity(Intent(this, Login::class.java))
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteAccount() {
        val database = FirebaseDatabase.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return // Handle case where user is not logged in

        // Delete user's data from database
        val userRef = database.child("empower").child(userId)
        userRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Delete user from Firebase Authentication
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnCompleteListener { deleteUserTask ->
                            if (deleteUserTask.isSuccessful) {
                                Log.d("ProfileActivity", "Account deleted successfully")
                                // Navigate to login or appropriate screen
                                val backToLoginIntent = Intent(this, Login::class.java)
                                startActivity(backToLoginIntent)
                                finish()
                            } else {
                                Log.w("ProfileActivity", "Account deletion failed: ${deleteUserTask.exception}")
                            }
                        }
                } else {
                    Log.w("ProfileActivity", "Database deletion failed: ${task.exception}")
                }
            }
    }
}