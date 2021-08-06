package com.example.vinsol

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MeetingListAdapter(private val context: Context, val items:List<Meeting>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        val sdf = SimpleDateFormat("HH:mm")
        val start = sdf.parse(currentItem.startTime)
        val end = sdf.parse(currentItem.endTime)
        val sdf2 = SimpleDateFormat("HH:mm aa")
        val startTime  = sdf2.format(start)
        val endTime = sdf2.format(end)
        holder.time.setText(startTime+"-"+endTime)
        holder.description.setText(currentItem.description)

    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    val time: TextView = itemView.findViewById(R.id.Time)
    val description: TextView = itemView.findViewById(R.id.Description)

}
