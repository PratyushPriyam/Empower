package com.assignment.empower

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var emailEdt: EditText
    lateinit var passEdt: EditText
    lateinit var logInBtn: AppCompatButton
    lateinit var signUpBtn: AppCompatButton
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.LEFT)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEdt = findViewById(R.id.editTextText)
        passEdt = findViewById(R.id.editTextText3)
        signUpBtn = findViewById(R.id.button2)
        signUpBtn.setOnClickListener {
            val profileIntent = Intent(this, Signup::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(profileIntent, options.toBundle())
        }

        logInBtn = findViewById(R.id.button)
        logInBtn.setOnClickListener {
            auth.signInWithEmailAndPassword(emailEdt.text.toString(), passEdt.text.toString()).addOnSuccessListener {
                val inflater: View = layoutInflater.inflate(R.layout.log_in_custom_toast, findViewById(R.id.customToastMainLayoutIdLogIn))
                val toast = Toast(applicationContext)
                toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                toast.duration = Toast.LENGTH_LONG
                toast.view = inflater
                toast.show()
                val profileIntent = Intent(this, MainActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(profileIntent, options.toBundle())
            }
                .addOnFailureListener {
                    val inflater: View = layoutInflater.inflate(R.layout.something_went_wrong_custom_toast, findViewById(R.id.customToastMainLayoutIdWrong))
                    val toast = Toast(applicationContext)
                    toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = inflater
                    toast.show()
                }
        }
    }
}