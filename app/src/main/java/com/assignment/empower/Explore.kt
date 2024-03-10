package com.assignment.empower

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class Explore : AppCompatActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private val data = listOf(
        ExploreDataClass(
            "https://th.bing.com/th/id/OIP.wJlBqapVLULQtoKJRG9uLAHaE8?w=237&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
            "Push Up",
            "Plank, lower chest to near floor (45° elbows), core tight, back straight. Push back up, breathe: out down, in up. Straight line head to heels. Modify with knees if needed. Listen to body, gradually increase reps."
        ),
        ExploreDataClass("https://th.bing.com/th/id/OIP.WGNIQi_rBTn8bAPJqL06pQHaE8?w=249&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "Pull Up", "Do it"),
        ExploreDataClass("https://th.bing.com/th/id/OIP.U3yAlsvj5N2NyDHwMlYB6QHaE8?w=268&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "Squat", "Do it"),
        ExploreDataClass("https://th.bing.com/th/id/OIP.IqnX6pfZiy-RlBfgraSZQQHaE8?w=228&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7", "Deadlift", "Do it")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.RIGHT)
        setContentView(R.layout.activity_explore)
        textToSpeech = TextToSpeech(this, null)
        val exploreRecView = findViewById<RecyclerView>(R.id.exploreRecView)
        exploreRecView.adapter = GridViewAdapter(data, textToSpeech)
        exploreRecView.layoutManager = GridLayoutManager(this, 2)
        val backBtn = findViewById<CardView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finishAfterTransition()
        }
    }

    // Data class for the workout items
    data class ExploreDataClass(val img: String, val name: String, val description: String)

    // Adapter class for the RecyclerView
    class GridViewAdapter(private val data: List<ExploreDataClass>, val tts: TextToSpeech?) : RecyclerView.Adapter<GridViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_explore_item, parent, false)
            return ViewHolder(view, this)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = data[position].name
            Glide.with(holder.itemView.context)
                .load(data[position].img)
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = data.size

        class ViewHolder(itemView: View, private val adapter: GridViewAdapter) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.workoutNameTxt)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView2)

            init {  // Initialize click listener in the ViewHolder constructor
                itemView.setOnClickListener {
                    val position = adapter.data.indexOf(adapter.data[bindingAdapterPosition])
                    val workoutName = adapter.data[position].name
                    val workoutDescription = adapter.data[position].description
                    val workoutImageUrl = adapter.data[position].img
                    val intent = Intent(itemView.context, WorkoutDesc::class.java)
                    intent.putExtra("workoutName", workoutName)
                    intent.putExtra("workoutImgUrl", workoutImageUrl)
                    intent.putExtra("workoutDescription", workoutDescription)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
    private fun speakWorkoutName(workoutName: String) {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop() // Stop any ongoing speech before speaking new name
        }
        textToSpeech.speak(workoutName, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}


