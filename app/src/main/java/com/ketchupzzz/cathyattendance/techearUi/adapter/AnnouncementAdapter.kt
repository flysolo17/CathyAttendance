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

class AnnouncementAdapter(val context: Context, private val announcementList: List<Announcements>) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_anouncement,parent,false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcements = announcementList[position]
        holder.textContent.text = announcements.announcementContent
        holder.textCommentCounter.text = announcements.comments.size.toString()
        holder.bindWriterInfo(announcements.writerID!!)
        holder.textTimestamp.text = announcements.timestamp.toString()
    }

    override fun getItemCount(): Int {
        return announcementList.size
    }
    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageWriter : ImageView = itemView.findViewById(R.id.imageWriter)
        private val textWriterName : TextView = itemView.findViewById(R.id.textWriterName)
        val buttonSettings : ImageButton = itemView.findViewById(R.id.buttonSettings)
        val textContent : TextView = itemView.findViewById(R.id.textContent)
        val textTimestamp : TextView = itemView.findViewById(R.id.textTimestamp)
        val textCommentCounter : TextView = itemView.findViewById(R.id.textCommentCounter)
        fun bindWriterInfo(writerID : String) {
            FirebaseFirestore.getInstance().collection(Users.TABLE_NAME).document(writerID)
                .get().addOnSuccessListener { document ->
                    if (document.exists()){
                        val users = document.toObject(Users::class.java)
                        if (users != null) {
                            if (users.userProfile.isNotEmpty()) {
                                Picasso.get().load(users.userProfile).into(imageWriter)
                            }
                            textWriterName.text = "${users.firstname} ${users.lastname}"
                            if (users.userID.equals(writerID)){
                                buttonSettings.visibility = View.VISIBLE
                            } else {
                                buttonSettings.visibility = View.GONE
                            }
                        }
                    }

                }
        }
    }
}