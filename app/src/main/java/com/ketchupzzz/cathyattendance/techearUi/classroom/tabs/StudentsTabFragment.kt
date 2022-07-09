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
import com.ketchupzzz.cathyattendance.models.Invitations
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
                            classStudents.add(student)
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
                            if (!users.userType.equals("Teacher")){
                                usersList.add(users)
                                studentList.map { students ->
                                    if (students.studentID.equals(users.userID) ) {
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

    private fun inviteStudent(invitations: Invitations) {
        firestore.collection(Users.TABLE_NAME)
            .document(invitations.studentID!!)
            .collection(Invitations.TABLE_NAME)
            .document(invitations.classID!!)
            .set(invitations)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Invitation Success",Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(binding.root.context, "Invitation Failed",Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun inviteUser(position: Int) {
        val users = usersList[position]
        val classID = ClassroomFragment.subjectClass?.classID!!
        val invitations = Invitations(classID,users.userID)
        inviteStudent(invitations)
    }

    override fun cancelInvitation(position: Int) {
        cancelMyInvitation(position)
    }

    override fun removeFromClass(position: Int) {
        val classID = ClassroomFragment.subjectClass!!.classID
        removeStudentFromClass(classID!!, classStudents[position].studentID!!)
    }

    override fun onStudentClick(position: Int) {
        TODO("Not yet implemented")
    }

    private fun cancelMyInvitation(position : Int) {
        firestore.collection(Users.TABLE_NAME)
            .document(usersList[position].userID!!)
            .collection(Invitations.TABLE_NAME)
            .document(ClassroomFragment.subjectClass?.classID!!)
            .delete()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(view?.context,"Invitation Cancelled!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(view?.context,"Error: Canceling invitation",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun removeStudentFromClass(classroomID: String,studentID : String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classroomID)
            .collection(Students.TABLE_NAME)
            .document(studentID)
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context,"Student removed!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Failed to remove student",Toast.LENGTH_SHORT).show()
                }
            }
    }
}