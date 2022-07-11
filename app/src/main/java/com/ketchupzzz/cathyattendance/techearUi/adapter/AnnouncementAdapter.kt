package com.ketchupzzz.cathyattendance.techearUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Announcements
import com.ketchupzzz.cathyattendance.models.Users
import com.squareup.picasso.Picasso
import kotlinx.coroutines.processNextEventInCurrentThread
import java.sql.Date
import java.text.SimpleDateFormat

class AnnouncementAdapter(val context: Context, private val announcementList: List<Announcements>) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_anouncement,parent,false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcements = announcementList[position]
        holder.textContent.text = announcements.announcementContent

        holder.textTimestamp.text = dateFormatter(announcements.timestamp)
    }

    override fun getItemCount(): Int {
        return announcementList.size
    }
    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageWriter : ImageView = itemView.findViewById(R.id.imageWriter)
        private val textWriterName : TextView = itemView.findViewById(R.id.textWriterName)
        val textContent : TextView = itemView.findViewById(R.id.textContent)
        val textTimestamp : TextView = itemView.findViewById(R.id.textTimestamp)



    }
    private fun dateFormatter(timestamp: Long) : String{
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("EE, dd-MMM-yyyy")
        val dateTime = simpleDateFormat.format(date.time)
        return dateTime.toString()
    }
}