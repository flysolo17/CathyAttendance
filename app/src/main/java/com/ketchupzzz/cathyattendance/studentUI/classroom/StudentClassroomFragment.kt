package com.ketchupzzz.cathyattendance.studentUI.classroom

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentStudentClassroomBinding
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.StudentUITabAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragmentArgs
import com.ketchupzzz.cathyattendance.techearUi.classroom.tabs.TabAdapter


class StudentClassroomFragment : Fragment() {
    private val args by navArgs<StudentClassroomFragmentArgs>()
    private lateinit var binding : FragmentStudentClassroomBinding
    private lateinit var firestore: FirebaseFirestore
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        binding.textClassTitle.text = args.classroom.classTitle
        getTeacherInfo(args.classroom.classTeacherID!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentClassroomBinding.inflate(inflater,container,false)
        subjectClass = args.classroom
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
        setupTabLayout()
    }
    private fun getTeacherInfo(teacherID : String) {
        firestore.collection(Users.TABLE_NAME)
            .document(teacherID)
            .addSnapshotListener { value, error ->
                if (error != null ){
                    error.printStackTrace()
                } else{
                if (value != null) {
                    val user = value.toObject(Users::class.java)
                    if (user != null) {
                        displayTeacherInfo(user)
                    }
                }
            }
            }

    }
    private fun displayTeacherInfo(users: Users) {
        binding.textTeachersName.text = "${users.firstname} ${users.lastname}"
    }
    private fun setupTabLayout() {
        val tabAdapter = StudentUITabAdapter(childFragmentManager,lifecycle)
        binding.viewpager.adapter = tabAdapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewpager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)!!.select()
            }
        })
    }
    companion object {
        var subjectClass : SubjectClass? = null
    }
}