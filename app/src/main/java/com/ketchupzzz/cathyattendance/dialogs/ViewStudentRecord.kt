package com.ketchupzzz.cathyattendance.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentViewStudentRecordBinding
import com.ketchupzzz.cathyattendance.models.*
import com.ketchupzzz.cathyattendance.otheradapter.ActivitiesAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.viewmodels.StudentsViewModel
import com.squareup.picasso.Picasso


class ViewStudentRecord : DialogFragment() {
    private lateinit var binding : FragmentViewStudentRecordBinding
    private lateinit var studentsViewModel: StudentsViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var activitiesAdapter: ActivitiesAdapter
    private lateinit var activitiesList: MutableList<Activities>
    private  var students: Students? = null
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(binding.root.context)
        binding.recyclerViewActivities.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerViewActivities.context,
                DividerItemDecoration.VERTICAL
            )
        )
        studentsViewModel = ViewModelProvider(requireActivity())[StudentsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewStudentRecordBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.buttonBack.setOnClickListener { dismiss() }
        studentsViewModel.getStudent().observe(viewLifecycleOwner) { student ->
            if (student  != null) {
                this.students = student
                getUser(student.studentID!!)
                getAttendance(student.studentID)
                getAllRecords(ClassroomFragment.subjectClass?.classID!!, students!!.studentID!!)
                var count = 0
                student.gradeList.map { grade ->
                    displayGrades(grade,count)
                    count += 1
                }
            }
        }
        binding.buttonAddRecord.setOnClickListener {

            if (students != null) {
                val addRecordDialog = AddRecordDialog()
                if (!addRecordDialog.isAdded){
                    addRecordDialog.show(childFragmentManager,"Add New Record")
                }

            }
        }
    }
    private fun getUser(studentID : String) {
        firestore.collection(Users.TABLE_NAME)
            .document(studentID)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user  = document.toObject(Users::class.java)
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
    private fun getAttendance(studentID: String) {
        val attendanceList : MutableList<Attendance> = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(ClassroomFragment.subjectClass?.classID!!)
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
    private fun computeAbsent(attendanceList: List<Attendance>,studentID: String) : Int {
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
    private fun getAllRecords(subjectID : String,studentID : String) {
        activitiesList = mutableListOf()
        firestore.collection(Activities.TABLE_NAME)
            .orderBy(Activities.TIMESTAMP,Query.Direction.DESCENDING)
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
    private fun showEditGradeDialog(position : Int,grade: Grade) {
        val view : View = layoutInflater.inflate(R.layout.dialog_add_grades,binding.root,false)
        val inputGrade : EditText = view.findViewById(R.id.inputGrade)
        MaterialAlertDialogBuilder(binding.root.context)
            .setView(view)
            .setTitle("Edit Grade for ${grade.term}")
            .setPositiveButton("Save") { dialog,_ ->
                val grade = inputGrade.text.toString()
                if (grade.isEmpty()) {
                    inputGrade.error  = "enter grade"
                } else {
                    students!!.gradeList[position].grade= Integer.parseInt(grade)
                    updateGrade(students?.studentID!!, students!!.gradeList)
                }
            }
            .show()
    }
    private fun displayGrades(grade: Grade,count : Int) {
        val view : View = layoutInflater.inflate(R.layout.row_grades,binding.root,false)
        val textTerm: TextView = view.findViewById(R.id.textTerm)
        val textGrade : TextView = view.findViewById(R.id.textGrade)
        val buttonEditGrade : ImageButton = view.findViewById(R.id.buttonEditGrade)
        textTerm.text = grade.term
        textGrade.text = grade.grade.toString()
        buttonEditGrade.setOnClickListener {
            showEditGradeDialog(count,grade)

        }
        binding.layoutGrade.addView(view)
    }
    private fun updateGrade(studentID : String,listGrade : List<Grade>){
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(ClassroomFragment.subjectClass?.classID!!)
            .collection(Students.TABLE_NAME)
            .document(studentID)
            .update(Students.GRADE,listGrade)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context,"Grade updated Successful",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Failed to update grade",Toast.LENGTH_SHORT).show()
                }
            }
    }
}