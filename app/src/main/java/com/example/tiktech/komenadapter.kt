package com.example.tiktech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.hdodenhof.circleimageview.CircleImageView

class komenadapter(private val item: List<komenitem>) : RecyclerView.Adapter<komenadapter.ViewHolder>() {
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun insert(item: komenitem,id : Int){
            view.findViewById<TextView>(R.id.username_constribute).text = item.username
            view.findViewById<TextView>(R.id.text_constribute).text = item.text
            view.findViewById<CircleImageView>(R.id.fp_constribute).load(item.foto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_komenlayout, parent, false))

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.insert(item.get(position), position + 1)
    }
}