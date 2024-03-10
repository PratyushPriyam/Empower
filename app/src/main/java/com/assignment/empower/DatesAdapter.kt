package com.assignment.empower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DatesAdapter(private val dates: List<String>, private val activity: History) :
    RecyclerView.Adapter<DatesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = dates[position]
        holder.dateText.text = date
        holder.itemView.setOnClickListener { activity.onDateClick(date) } // Set click listener to call activity's method
    }

    override fun getItemCount(): Int {
        return dates.size
    }
}
