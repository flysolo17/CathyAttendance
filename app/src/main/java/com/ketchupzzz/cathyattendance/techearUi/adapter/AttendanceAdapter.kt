package com.ketchupzzz.cathyattendance.techearUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Attendance
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.adapter.StudentAttendanceAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.sql.Date
import java.text.SimpleDateFormat


class AttendanceAdapter(val context: Context, private val attendanceList: List<Attendance>,val attendanceListeners: AttendanceAdapter.AttendanceAdapterListener) :
        RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>(){

    interface AttendanceAdapterListener {
        fun viewAttendance(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_attendance,parent,false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        val subjectClass = ClassroomFragment.subjectClass!!
        holder.attendanceNote.text = attendance.attendanceNote
        holder.textDate.text = dateFormatter(attendance.timestamp)
        holder.textAttendeesCount.text = "${attendance.attendees.size} Attendees"
        holder.displayTeacherInfo(subjectClass.classTeacherID!!)
        holder.switchButton.isChecked = attendance.accepting
        holder.switchButton.setOnCheckedChangeListener { _, isChecked ->
            holder.updateAttendanceStatus(isChecked,subjectClass.classID!!,
                attendance.attendanceID!!
            )
        }
        holder.buttonViewAttendees.setOnClickListener {
            attendanceListeners.viewAttendance(position)
        }

    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }
    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTeacherName : TextView = itemView.findViewById(R.id.textClassTeacherName)
        private val imageTeacherProfile :  CircleImageView = itemView.findViewById(R.id.imageTeacherProfile)
        val attendanceNote : TextView = itemView.findViewById(R.id.textAtttandanceInfo)
        val textAttendeesCount : TextView= itemView.findViewById(R.id.textAttendeesCount)
        val textDate : TextView = itemView.findViewById(R.id.textDate)
        val buttonViewAttendees : Button = itemView.findViewById(R.id.buttonViewAttendees)
        val switchButton : SwitchCompat = itemView.findViewById(R.id.toggleButton)

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
        fun updateAttendanceStatus(accepting: Boolean,subjectClassID : String,attendanceID : String){
            firestore.collection(SubjectClass.TABLE_NAME)
                .document(subjectClassID)
                .collection(Attendance.TABLE_NAME)
                .document(attendanceID)
                .update(Attendance.ACCEPTING,accepting)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if(accepting) {
                            Toast.makeText(itemView.context,"Attendance is now open",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(itemView.context,"Attendance is closed",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(itemView.context,"Failed to update",Toast.LENGTH_SHORT).show()
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