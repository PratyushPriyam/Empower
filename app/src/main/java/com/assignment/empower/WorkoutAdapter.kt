package com.assignment.empower

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private val workouts: MutableList<WorkoutModelClass>, private val context: Context) :
    RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutNameText: TextView = itemView.findViewById(R.id.workoutName_Edt)
        val setsText: TextView = itemView.findViewById(R.id.sets_Edt)
        val repsText: TextView = itemView.findViewById(R.id.reps_Edt)
        val deleteButton: ImageButton = itemView.findViewById(R.id.recDelBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutNameText.text = "Workout title: "+workout.name
        holder.setsText.text = "Total Sets: "+workout.sets.toString()
        holder.repsText.text = "Total Reps: "+workout.reps.toString()

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Removal")
                .setMessage("Are you sure you want to remove this workout?")
                .setPositiveButton("Remove") { _, _ ->
                    workouts.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return workouts.size
    }
}
