package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentSettingsTabBinding
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.techearUi.TeacherMainScreen
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.techearUi.classroom.UpdateClassFragment
import com.ketchupzzz.cathyattendance.viewmodels.SubjectClassViewModel
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsTabBinding
    private lateinit var subjectClass: SubjectClass
    private lateinit var firestore: FirebaseFirestore
    private lateinit var subjectClassViewModel: SubjectClassViewModel
    private fun init(classID : String) {
        firestore = FirebaseFirestore.getInstance()
        subjectClassViewModel = ViewModelProvider(requireActivity())[SubjectClassViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjectClass  = ClassroomFragment.subjectClass!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(subjectClass.classID!!)
        bindViews(subjectClass)

        binding.buttonSend.setOnClickListener {
            shareClassCode(subjectClass.classCode)
        }
        binding.switchButton.setOnCheckedChangeListener { compoundButton, isChecked ->
            acceptingStudents(isChecked, subjectClass.classID!!)
        }
        binding.buttonEditClass.setOnClickListener {
            subjectClassViewModel.setSubjectClass(subjectClass)
            val updateClassFragment = UpdateClassFragment()
            if (!updateClassFragment.isAdded) {
                updateClassFragment.show(childFragmentManager,"Update Class Fragment")
            }
        }

    }
    private fun shareClassCode(code : String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(shareIntent,"Share to"))
    }
    private fun bindViews(subjectClass: SubjectClass) {
        binding.textClassTitle.text = subjectClass.classTitle
        binding.textClassDesc.text = subjectClass.classDesc
        if (subjectClass.classProfile!!.isNotEmpty()) {
            Picasso.get().load(subjectClass.classProfile).into(binding.imageClassProfile)
        }
        binding.switchButton.isChecked = subjectClass.open!!

    }
    private fun acceptingStudents(isOpen: Boolean,classID: String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classID)
            .update(SubjectClass.IS_OPEN,isOpen)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (isOpen) {
                        Toast.makeText(binding.root.context,"Is now accepting Students",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(binding.root.context,"This subject is closed",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context,"Failed update open field in database",Toast.LENGTH_SHORT).show()
                }

            }
    }
}