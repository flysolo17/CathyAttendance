package com.ketchupzzz.cathyattendance.techearUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.Users
import com.squareup.picasso.Picasso

class StudentsAdapter(val context: Context, private val studentsList: List<Students>,private val studentsClickListener: StudentsClickListener) : RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>() {
    interface StudentsClickListener {
        fun removeFromClass(position: Int)
        fun onStudentClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_students,parent,false)
        return StudentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        holder.bindStudents(studentsList[position].studentID!!)
        holder.buttonRemove.setOnClickListener {
            studentsClickListener.removeFromClass(position)
        }
        holder.buttonViewRecord.setOnClickListener {
            studentsClickListener.onStudentClick(position)
        }

    }

    override fun getItemCount(): Int {
        return studentsList.size
    }
    class StudentsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val imageStudentProfile : ImageView = itemView.findViewById(R.id.imageStudentProfile)
        private val textStudentName : TextView = itemView.findViewById(R.id.textStudentsName)
        private val textStudentIDNumber : TextView = itemView.findViewById(R.id.textIdNumber)
        val buttonRemove : Button = itemView.findViewById(R.id.buttonRemoveFromClass)
        val buttonViewRecord : Button = itemView.findViewById(R.id.buttonViewRecords)
        private val firestore = FirebaseFirestore.getInstance()
        fun bindStudents(studentID : String) {
            firestore
                .collection(Users.TABLE_NAME)
                .document(studentID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(Users::class.java)
                        if (user?.userProfile!!.isNotEmpty()) {
                            Picasso.get().load(user.userProfile).into(imageStudentProfile)
                        }
                        textStudentName.text = "${user.firstname} ${user.middleName} ${user.lastname}"
                        textStudentIDNumber.text = user.idNumber

                    }

                }
        }
    }
}