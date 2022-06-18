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
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.squareup.picasso.Picasso

class UsersAdapter(val context: Context, private val studentsList : List<Users>, private val onUserClick: OnUserClick) : RecyclerView.Adapter<UsersAdapter.StudentsViewHolder>() {
    interface OnUserClick {
        fun inviteUser(position: Int)
        fun cancelInvitation(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false)
        return StudentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        val students : Users = studentsList[position]
        if (students.userProfile.isNotEmpty()) {
            Picasso.get().load(students.userProfile).into(holder.imageStudentsProfile)
        }
        val fullname = students.firstname + " " + students.middleName + " " + students.lastname
        holder.textStudentsFullname.text = fullname
        holder.textStatus.text = "To invite"
        holder.buttonInvite.setOnClickListener {
            onUserClick.inviteUser(position)
        }
        holder.bindButtons(students.userID!!)
        holder.buttonCancel.setOnClickListener {
            onUserClick.cancelInvitation(position)
        }
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }
    class StudentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageStudentsProfile : ImageView = itemView.findViewById(R.id.imageStudentProfile)
        val textStudentsFullname : TextView = itemView.findViewById(R.id.textStudentsName)
        val textStatus : TextView = itemView.findViewById(R.id.textStatus)
        val buttonInvite : Button = itemView.findViewById(R.id.buttonInvite)
        val buttonCancel : Button = itemView.findViewById(R.id.buttonCancelInvite)
        val firestore = FirebaseFirestore.getInstance()
        fun bindButtons(studentID : String) {
            firestore
                .collection(SubjectClass.TABLE_NAME)
                .document(ClassroomFragment.subjectClass?.classID!!)
                .collection(Students.TABLE_NAME)
                .document(studentID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val students = document.toObject(Students::class.java)
                        if (students?.studentID.equals(studentID)) {
                            buttonInvite.visibility = View.GONE
                            buttonCancel.visibility = View.VISIBLE
                        } else {
                            buttonInvite.visibility = View.VISIBLE
                            buttonCancel.visibility = View.GONE
                        }
                    }

                }
        }
    }
}