package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentSettingsTabBinding
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.techearUi.TeacherMainScreen
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsTabBinding
    private lateinit var subjectClass: SubjectClass
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
        bindViews(subjectClass)

        binding.buttonSend.setOnClickListener {
            shareClassCode(subjectClass.classCode)
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
    }
}