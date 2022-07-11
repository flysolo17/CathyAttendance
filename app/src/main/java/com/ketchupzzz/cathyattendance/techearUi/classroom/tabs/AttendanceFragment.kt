package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentAttendanceBinding
import com.ketchupzzz.cathyattendance.dialogs.ViewAttendeesFragment
import com.ketchupzzz.cathyattendance.models.Attendance
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.techearUi.adapter.AttendanceAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.viewmodels.AttendanceViewModel


class AttendanceFragment : Fragment(),AttendanceAdapter.AttendanceAdapterListener {

    private lateinit var binding : FragmentAttendanceBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var attendanceList: MutableList<Attendance>
    private lateinit var attendanceViewModel : AttendanceViewModel
    private fun init(subjectID: String) {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewAttendance.layoutManager = LinearLayoutManager(binding.root.context)
        getAllAttendance(subjectID)
        attendanceViewModel = ViewModelProvider(requireActivity())[AttendanceViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(ClassroomFragment.subjectClass?.classID!!)
        binding.fabCreateAttendance.setOnClickListener {
            showCreateAttendanceDialog(view)
        }
    }
    private fun showCreateAttendanceDialog(view: View) {
        val view : View = LayoutInflater.from(view.context).inflate(R.layout.dialog_create_attendance,binding.root,false)
        MaterialAlertDialogBuilder(view.context)
            .setView(view)
            .setPositiveButton("Create") { dialog, _ ->
                val note : EditText = view.findViewById(R.id.inputNote)
                if (note.text.toString().isNotEmpty()) {
                    val classID = ClassroomFragment.subjectClass!!.classID!!
                    val  attendanceID = firestore.collection(SubjectClass.TABLE_NAME).document(classID).collection(Attendance.TABLE_NAME).document().id
                    val attendance = Attendance(attendanceID,note.text.toString())
                    createAttendance(attendance,classID)
                } else {
                    Toast.makeText(view.context,"Cancelled!",Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog , _ ->
                dialog.dismiss()
            }.show()
    }
    private fun createAttendance(attendance: Attendance, subjectID : String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectID)
            .collection(Attendance.TABLE_NAME)
            .document(attendance.attendanceID!!)
            .set(attendance)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context,"Attendance Created!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Attendance Creation Failed!",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun getAllAttendance(subjectID : String){
        attendanceList = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectID)
            .collection(Attendance.TABLE_NAME)
            .addSnapshotListener { value, error ->
                attendanceList.clear()
                if (error != null) {
                    error.printStackTrace()
                } else {
                    value?.map { queryDocumentSnapshot ->
                        val attendance = queryDocumentSnapshot.toObject(Attendance::class.java)
                        attendanceList.add(attendance)
                    }
                    attendanceAdapter = AttendanceAdapter(binding.root.context,attendanceList,this)
                    binding.recyclerviewAttendance.adapter = attendanceAdapter
                }
            }
    }

    override fun viewAttendance(position: Int) {
        attendanceViewModel.setAttendance(attendanceList[position])
        val viewAttendeesFragment = ViewAttendeesFragment();
        if (!viewAttendeesFragment.isAdded) {
            viewAttendeesFragment.show(childFragmentManager,"View Attendees")
        }
    }
}