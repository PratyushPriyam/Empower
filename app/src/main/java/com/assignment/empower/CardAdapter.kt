package com.assignment.empower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CardAdapter(private val cardList: List<Card>) :
    RecyclerView.Adapter<CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_motivation_card, parent, false)
        return CardViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        Picasso.get()
            .load(card.imageUrl)
            .into(holder.cardImage)
        holder.cardText.text = card.text
    }

    override fun getItemCount(): Int = cardList.size

}

class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardImage: ImageView = itemView.findViewById(R.id.card_image)
    val cardText: TextView = itemView.findViewById(R.id.card_text)
}
