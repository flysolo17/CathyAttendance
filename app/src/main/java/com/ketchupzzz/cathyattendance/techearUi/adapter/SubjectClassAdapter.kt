package com.ketchupzzz.cathyattendance.techearUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.squareup.picasso.Picasso

class SubjectClassAdapter(val context: Context, private val classList: List<SubjectClass>,
                          private val viewClassroom: ViewClassroom) :
    RecyclerView.Adapter<SubjectClassAdapter.SubjectClassViewHolder>() {
    interface ViewClassroom {
        fun onClassroomClick(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectClassViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_class,parent,false)
        return SubjectClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectClassViewHolder, position: Int) {
        val subjectClass = classList[position]
        holder.textClassTitle.text = subjectClass.classTitle
        holder.textClassDesc.text = subjectClass.classDesc
        if (subjectClass.open == true) {
            holder.textClassStatus.text = "Available for enrollment"
        } else {
            holder.textClassStatus.text = "Not available for enrollment"
        }
        if (subjectClass.classProfile!!.isNotEmpty()) {
            Picasso.get().load(subjectClass.classProfile).placeholder(R.drawable.teaching).into(holder.imageClass)
        }
        holder.itemView.setOnClickListener {
            viewClassroom.onClassroomClick(position)
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }
    class SubjectClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textClassTitle : TextView = itemView.findViewById(R.id.textClassTitle)
        val textClassDesc: TextView = itemView.findViewById(R.id.textClasDesc)
        val textClassStatus: TextView = itemView.findViewById(R.id.textClassStatus)
        val imageClass : ImageView = itemView.findViewById(R.id.imageClass)
    }
}