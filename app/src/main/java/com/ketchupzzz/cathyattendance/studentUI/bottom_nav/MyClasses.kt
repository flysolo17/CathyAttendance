package com.ketchupzzz.cathyattendance.studentUI.bottom_nav

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentClassesBinding
import com.ketchupzzz.cathyattendance.databinding.FragmentMyClassesBinding
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.adapter.StudentClassesAdapter
import com.ketchupzzz.cathyattendance.techearUi.bottom_nav.ClassesFragmentDirections
import com.squareup.picasso.Picasso


class MyClasses : Fragment(),StudentClassesAdapter.ViewClassroom {

    private lateinit var binding : FragmentMyClassesBinding
    private lateinit var studentClassesAdapter: StudentClassesAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var classList : MutableList<SubjectClass>

    private fun init(myID: String) {
        binding.recyclerviewMyClass.layoutManager = LinearLayoutManager(binding.root.context)
        firestore = FirebaseFirestore.getInstance()
        getAllClasses(myID)
        fetchMyInfo(myID)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyClassesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(FirebaseAuth.getInstance().currentUser!!.uid)
    }
    private fun getAllClasses(myID: String) {
        classList = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .addSnapshotListener { value, error ->
                classList.clear()
                if (error != null) {
                    error.printStackTrace()
                } else {
                    value?.map { document ->
                        val subjectClass = document.toObject(SubjectClass::class.java)
                        fetchAllMyClass(subjectClass,myID)
                    }

                }
            }
    }
    private fun fetchAllMyClass(subjectClass: SubjectClass,myID: String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectClass.classID!!)
            .collection(Students.TABLE_NAME)
            .document(myID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                } else {
                    if (value != null) {
                        if (value.exists()) {
                            classList.add(subjectClass)
                            studentClassesAdapter = StudentClassesAdapter(binding.root.context,classList,this)
                            binding.recyclerviewMyClass.adapter = studentClassesAdapter
                            noClasses(classList)
                        }
                    }
                }
            }
    }

    private fun fetchMyInfo(myID: String) {
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                } else {
                    if (value != null) {
                        if (value.exists()) {
                            val user = value.toObject(Users::class.java)
                            bindMyInfo(user!!)
                        }
                    }
                }
            }
    }
    private fun bindMyInfo(user : Users) {
        if (user.userProfile.isNotEmpty()) {
            Picasso.get().load(user.userProfile).into(binding.userProfile)
        }
        binding.textUserFullname.text = "${user.firstname} ${user.middleName} ${user.lastname}"
    }
    private fun noClasses(classList : List<SubjectClass>){
        if (classList.isEmpty()) {
            binding.noClassContainer.visibility = View.VISIBLE
        } else {
            binding.noClassContainer.visibility = View.GONE
        }
    }
    companion object {
        const val  TAG = ".MyClasses"
    }

    override fun onClassroomClick(position: Int) {
        val action : NavDirections = MyClassesDirections.actionNavMyClassToStudentClassroomFragment(classList[position])
        Navigation.findNavController(binding.root).navigate(action)
    }

}