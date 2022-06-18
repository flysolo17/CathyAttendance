package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.ketchupzzz.cathyattendance.databinding.FragmentStudentsTabBinding
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.techearUi.adapter.StudentsAdapter
import com.ketchupzzz.cathyattendance.techearUi.adapter.UsersAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment


class StudentsFragment : Fragment(),UsersAdapter.OnUserClick,StudentsAdapter.StudentsClickListener {

    private lateinit var binding : FragmentStudentsTabBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var studentsAdapter: StudentsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var usersList: MutableList<Users>
    private lateinit var classStudents: MutableList<Students>
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewInviteStudents.apply {
            layoutManager = LinearLayoutManager(binding.root.context)
        }
        binding.recyclerviewClassStudent.apply {
            layoutManager = LinearLayoutManager(binding.root.context)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentsTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getAllMyStudents(ClassroomFragment.subjectClass?.classID!!)
        getAllUsers(classStudents)
    }
    private fun getAllMyStudents(classroomID : String){
        classStudents = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classroomID)
            .collection(Students.TABLE_NAME)
            .addSnapshotListener{ value : QuerySnapshot? , error: FirebaseFirestoreException? ->
                classStudents.clear()
                if (error != null
                ) {
                    Log.d(TAG, error.message.toString())
                }
                if (value != null) {
                    classStudents.clear()
                    for (document in value) {
                        if (document != null) {
                            val student = document.toObject(Students::class.java)
                            if (student.studentStatus == 1) {
                                classStudents.add(student)
                            }
                        }
                    }

                }
                studentsAdapter = StudentsAdapter(binding.root.context,classStudents,this)
                binding.recyclerviewClassStudent.adapter = studentsAdapter
                getAllUsers(classStudents)
                if (classStudents.size == 0) {
                    binding.textClassID.visibility =View.VISIBLE
                }
            }
    }
    private fun getAllUsers(studentList: List<Students>){
        usersList = mutableListOf()
        firestore.collection(Users.TABLE_NAME)
            .addSnapshotListener{ value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null
                ) {
                    Log.d(TAG, error.message.toString())
                }
                if (value != null) {
                    usersList.clear()
                    for (documentSnapshot in value) {
                        if (documentSnapshot != null) {
                            val users: Users = documentSnapshot.toObject(Users::class.java)
                            usersList.add(users)
                            for (students in studentList) {
                                 if (students.studentID.equals(users.userID)) {
                                     if (students.studentStatus == 1) {
                                         usersList.remove(users)
                                     }
                                 }
                             }
                        }
                    }
                    usersAdapter = UsersAdapter(binding.root.context,usersList,this)
                    binding.recyclerviewInviteStudents.adapter = usersAdapter
                }
            }
    }
    companion object {
        const val TAG = ".StudentsTabFragment"
    }

    private fun inviteStudent(classID : String ,student : Students) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classID)
            .collection(Students.TABLE_NAME)
            .document(student.studentID!!)
            .set(student)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Invitation Success",Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(binding.root.context, "Invitation Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun inviteUser(position: Int) {
        val users = usersList[position]
        val student = Students(users.userID,0)
        inviteStudent(ClassroomFragment.subjectClass?.classID!!,student)
    }

    override fun cancelInvitation(position: Int) {
        removeStudentFromClass(ClassroomFragment.subjectClass?.classID!!,
            usersList[position].userID!!,0
        )
    }

    override fun removeFromClass(position: Int) {
        removeStudentFromClass(ClassroomFragment.subjectClass?.classID!!,
            classStudents[position].studentID!!,
        1)
    }

    /**
     * TODO remove student from class and cancel Invitation
     * status: 1 for remove from class 0 for cancel inviation
     */
    private fun removeStudentFromClass(classID : String,studentID : String,status : Int) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classID)
            .collection(Students.TABLE_NAME)
            .document(studentID)
            .delete()
            .addOnCompleteListener { task ->
                if (task .isSuccessful) {
                    if (status == 1) {
                        Toast.makeText(binding.root.context,"Student removed!" ,Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(binding.root.context,"Invitation cancelled!" ,Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(binding.root.context,"Student failed to remove!" ,Toast.LENGTH_SHORT).show()
                }
            }
    }

}