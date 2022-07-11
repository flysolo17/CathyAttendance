package com.ketchupzzz.cathyattendance.studentUI.tabs

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentStudentRecordTabBinding
import com.ketchupzzz.cathyattendance.models.*
import com.ketchupzzz.cathyattendance.otheradapter.ActivitiesAdapter
import com.ketchupzzz.cathyattendance.studentUI.classroom.StudentClassroomFragment
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.squareup.picasso.Picasso

class StudentRecordTab : Fragment() {
    private lateinit var binding : FragmentStudentRecordTabBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var activitiesAdapter: ActivitiesAdapter
    private lateinit var activitiesList: MutableList<Activities>
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(binding.root.context)
        binding.recyclerViewActivities.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerViewActivities.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentRecordTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val subjectID = StudentClassroomFragment.subjectClass!!.classID
        val studentID = FirebaseAuth.getInstance().currentUser!!.uid
        getStudentInfo(subjectID!!, studentID)
        getAllRecords(subjectID,studentID)
    }
    private fun getAttendance(clasID : String,studentID: String) {
        val attendanceList : MutableList<Attendance> = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(clasID)
            .collection(Attendance.TABLE_NAME)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                } else {
                    value?.map { document ->
                        val attendance = document.toObject(Attendance::class.java)
                        attendanceList.add(attendance)

                    }
                    binding.textAttendance.text = attendanceList.size.toString()
                    binding.textAbsent.text = computeAbsent(attendanceList, studentID).toString()
                }
            }
    }
    private fun computeAbsent(attendanceList: List<Attendance>, studentID: String) : Int {
        var count = 0
        attendanceList.map { attendance ->
            attendance.attendees.map { attendees ->
                if (attendees.studentID.equals(studentID))   {
                    count += 1
                }
            }

        }
        return attendanceList.size - count
    }
    private fun getStudentInfo(subjectID : String,studentID : String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectID)
            .collection(Students.TABLE_NAME)
            .document(studentID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                } else {
                    if (value != null) {
                        if (value.exists()) {
                            val student = value.toObject(Students::class.java)
                            if (student != null) {
                                getUser(student.studentID!!)

                                getAttendance(subjectID,student.studentID)
                                student.gradeList.map { grade ->
                                    displayGrades(grade)
                                }
                            }
                        }
                    }
                }
            }
    }
    private fun getUser(userID : String) {
        firestore.collection(Users.TABLE_NAME)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Users::class.java)
                    if (user != null) {
                        displayUserInfo(user)
                    }
                }
            }
    }

    private fun displayUserInfo(user: Users) {
        if (user.userProfile.isNotEmpty()) {
            Picasso.get().load(user.userProfile).into(binding.imageStudentProfile)
        }
        binding.textStudentsName.text = "${user.firstname} ${user.lastname}"
        binding.textIdNumber.text = user.idNumber
    }
    private fun displayGrades(grade: Grade) {
        val view : View = layoutInflater.inflate(R.layout.row_student_grade,binding.root,false)
        val textTerm: TextView = view.findViewById(R.id.textTerm)
        val textGrade : TextView = view.findViewById(R.id.textGrade)
        textTerm.text = grade.term
        textGrade.text = grade.grade.toString()

        binding.layoutGrade.addView(view)
    }
    private fun getAllRecords(subjectID : String,studentID : String) {
        activitiesList = mutableListOf()
        firestore.collection(Activities.TABLE_NAME)
            .orderBy(Activities.TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                activitiesList.clear()
                if (error != null) {
                    error.printStackTrace()
                } else {
                    value?.map { document ->
                        val activities = document.toObject(Activities::class.java)
                        if (activities.classID == subjectID && activities.studentID.equals(studentID)) {
                            activitiesList.add(activities)
                        }

                    }
                    activitiesAdapter = ActivitiesAdapter(binding.root.context,activitiesList)
                    binding.recyclerViewActivities.adapter = activitiesAdapter

                }
            }
    }

}