package com.ketchupzzz.cathyattendance.techearUi.classroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentCreateAnnouncementBinding
import com.ketchupzzz.cathyattendance.models.Announcements
import com.ketchupzzz.cathyattendance.models.SubjectClass


class CreateAnnouncementFragment : DialogFragment() {
    private lateinit var binding : FragmentCreateAnnouncementBinding
    private lateinit var firestore : FirebaseFirestore
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAnnouncementBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.buttonCreateAnnouncement.setOnClickListener {
            val announcement : String = binding.inputAnnouncement.text.toString()
            if (announcement.isEmpty()) {
                Toast.makeText(view.context,"Invalid announcement",Toast.LENGTH_SHORT).show()
            } else {
                val writerID = FirebaseAuth.getInstance().currentUser?.uid
                val announcementID = firestore.collection(SubjectClass.TABLE_NAME).document(ClassroomFragment.subjectClass?.classID!!).collection(Announcements.TABLE_NAME).document().id
                val announcements = Announcements(announcementID,writerID,announcement)
                createAnnouncement(announcements)
            }
        }
    }
    private fun createAnnouncement(announcements: Announcements) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(ClassroomFragment.subjectClass?.classID!!)
            .collection(Announcements.TABLE_NAME)
            .document(announcements.announcementID!!)
            .set(announcements)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context,"Announcement Created Successful",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else{
                    Toast.makeText(binding.root.context,"Failed to create announcement",Toast.LENGTH_SHORT).show()
                }
            }
    }


}