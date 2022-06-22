package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentHomeTabBinding
import com.ketchupzzz.cathyattendance.models.Announcements
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.techearUi.adapter.AnnouncementAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.techearUi.classroom.CreateAnnouncementFragment


class HomeTabFragment : Fragment() {

    private lateinit var binding : FragmentHomeTabBinding
    private lateinit var announcementAdapter: AnnouncementAdapter
    private lateinit var announcementList: MutableList<Announcements>
    private lateinit var firestore: FirebaseFirestore
    private fun init(){
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewAnnouncements.layoutManager = LinearLayoutManager(binding.root.context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.fabCreateAnnouncements.setOnClickListener{
            val createAnnouncementFragment = CreateAnnouncementFragment()
            if (!createAnnouncementFragment.isAdded) {
                createAnnouncementFragment.show(childFragmentManager,"Create Announcements")
            }
        }
        getAllAnnouncements()
    }
    fun getAllAnnouncements(){
        announcementList = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME).document(ClassroomFragment.subjectClass?.classID!!)
            .collection(Announcements.TABLE_NAME)
            .orderBy(Announcements.TIMESTAMP,Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                announcementList.clear()
                if (error != null) {
                    Log.d(TAG, error.message.toString())
                }
                if (value != null) {
                    for (documents in value.documents) {
                        val announcements = documents.toObject(Announcements::class.java)
                        if (announcements != null) {
                            announcementList.add(announcements)
                        }
                    }
                    announcementAdapter = AnnouncementAdapter(binding.root.context,announcementList)
                    binding.recyclerviewAnnouncements.adapter = announcementAdapter
                }
            }
    }
    companion object {
        const val  TAG = ".HomeTabFragment"
    }

}