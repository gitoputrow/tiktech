package com.example.tiktech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.hdodenhof.circleimageview.CircleImageView

class notifadapter (private val item: List<notifitem>,var clicklistener : clicklistener_notif) : RecyclerView.Adapter<notifadapter.ViewHolder>(){
    inner class ViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        fun insert(item: notifitem, id: Int,action: clicklistener_notif){
            view.findViewById<CircleImageView>(R.id.fotoprofile_notif).load(item.photo)
            view.findViewById<TextView>(R.id.isinotif).text = item.text
            view.findViewById<TextView>(R.id.usernotif).text = item.username
            itemView.setOnClickListener {
                action.onnotifclick(item,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_notifitem, parent, false))


    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.insert(item.get(position), position + 1,clicklistener)
    }
}