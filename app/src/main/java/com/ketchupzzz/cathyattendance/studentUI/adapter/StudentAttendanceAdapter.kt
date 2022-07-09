package com.ketchupzzz.cathyattendance.studentUI.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Attendance
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.classroom.StudentClassroomFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.sql.Date
import java.text.SimpleDateFormat

class StudentAttendanceAdapter(val context: Context, private val attendanceList: List<Attendance>,
                               private val studentAttendanceListeners: StudentAttendanceListeners) :
    RecyclerView.Adapter<StudentAttendanceAdapter.StudentAttendanceViewHolder>() {
    interface StudentAttendanceListeners{
        fun takeAttendance(position: Int)
        fun viewAttendees(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_student_attendance,parent,false)
        return StudentAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        val subjectClass = StudentClassroomFragment.subjectClass!!
        holder.displayTeacherInfo(subjectClass.classTeacherID!!)
        holder.textDate.text = dateFormatter(attendance.timestamp)
        holder.attendanceNote.text = attendance.attendanceNote
        holder.buttonTakeAttendance.setOnClickListener {
            studentAttendanceListeners.takeAttendance(position)
        }
        holder.textAttendeesCount.text = "${attendance.attendees.size} attendees"
        holder.buttonViewAttendees.setOnClickListener { studentAttendanceListeners.viewAttendees(position) }
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }
    class StudentAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTeacherName : TextView = itemView.findViewById(R.id.textClassTeacherName)
        private val imageTeacherProfile : CircleImageView = itemView.findViewById(R.id.imageTeacherProfile)
        val attendanceNote : TextView = itemView.findViewById(R.id.textAtttandanceInfo)
        val textDate : TextView = itemView.findViewById(R.id.textDate)
        val textAttendeesCount : TextView = itemView.findViewById(R.id.textAttendeesCount)
        val buttonViewAttendees : Button = itemView.findViewById(R.id.buttonViewAttendees)
        val buttonTakeAttendance : Button = itemView.findViewById(R.id.buttonTakeAttendance)
        val firestore = FirebaseFirestore.getInstance()
        fun displayTeacherInfo(teacherID : String) {
            firestore.collection(Users.TABLE_NAME)
                .document(teacherID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(Users::class.java)
                        if (user?.userProfile!!.isNotEmpty()) {
                            Picasso.get().load(user.userProfile).into(imageTeacherProfile)

                        }
                        textTeacherName.text = "${user.firstname} ${user.lastname}"

                    }
                }
        }

    }
    private fun dateFormatter(timestamp: Long) : String{
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("EE, dd-MMM-yyyy hh:mm a")
        val dateTime = simpleDateFormat.format(date.time)
        return dateTime.toString()
    }

}