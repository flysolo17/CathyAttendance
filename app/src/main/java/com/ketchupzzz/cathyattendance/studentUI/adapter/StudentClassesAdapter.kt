package com.ketchupzzz.cathyattendance.studentUI.adapter

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

class StudentClassesAdapter(val context: Context, private val classList: List<SubjectClass>,
                          private val viewClassroom: ViewClassroom) :
    RecyclerView.Adapter<StudentClassesAdapter.StudentClassesViewHolder>() {
    interface ViewClassroom {
        fun onClassroomClick(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentClassesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_class,parent,false)
        return StudentClassesViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentClassesViewHolder, position: Int) {
        val subjectClass = classList[position]
        holder.textClassTitle.text = subjectClass.classTitle
        holder.textClassDesc.text = subjectClass.classDesc
        holder.textClassStatus.visibility = View.GONE
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
    class StudentClassesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textClassTitle : TextView = itemView.findViewById(R.id.textClassTitle)
        val textClassDesc: TextView = itemView.findViewById(R.id.textClasDesc)
        val imageClass : ImageView = itemView.findViewById(R.id.imageClass)
        val textClassStatus: TextView = itemView.findViewById(R.id.textClassStatus)
    }

}