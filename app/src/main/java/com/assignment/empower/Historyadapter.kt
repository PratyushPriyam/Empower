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

class Historyadapter(private val workouts: MutableList<WorkoutModelClass>, private val context: Context) :
    RecyclerView.Adapter<Historyadapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutNameText: TextView = itemView.findViewById(R.id.histedt1)
        val setsText: TextView = itemView.findViewById(R.id.histedt2)
        val repsText: TextView = itemView.findViewById(R.id.histedt3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutNameText.text = "Workout title: "+workout.name
        holder.setsText.text = "Total Sets: "+workout.sets.toString()
        holder.repsText.text = "Total Reps: "+workout.reps.toString()
    }

    override fun getItemCount(): Int {
        return workouts.size
    }
}
