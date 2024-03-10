package com.assignment.empower

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GridViewAdapter(private val data: List<Explore.ExploreDataClass>, val tts: TextToSpeech?) : RecyclerView.Adapter<GridViewAdapter.ViewHolder>() {

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
    }
}
