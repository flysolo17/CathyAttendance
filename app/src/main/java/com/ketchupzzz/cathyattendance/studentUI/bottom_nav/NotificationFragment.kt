package com.ketchupzzz.cathyattendance.studentUI.bottom_nav

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentNotificationBinding
import com.ketchupzzz.cathyattendance.models.Invitations
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.adapter.InvitationAdapter
import com.ketchupzzz.cathyattendance.techearUi.adapter.UsersAdapter
import com.ketchupzzz.cathyattendance.techearUi.classroom.tabs.StudentsFragment

class NotificationFragment : Fragment(),InvitationAdapter.InvitationClicks {

    private lateinit var binding : FragmentNotificationBinding
    private lateinit var invitationList : MutableList<Invitations>
    private lateinit var invitationAdapter: InvitationAdapter
    private lateinit var firestore : FirebaseFirestore


    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewInvitations.layoutManager = LinearLayoutManager(view?.context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val myID = FirebaseAuth.getInstance().currentUser!!.uid
        getAllInvitations(myID)
    }
    private fun getAllInvitations(myID : String){
        invitationList = mutableListOf()
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .collection(Invitations.TABLE_NAME)
            .addSnapshotListener{ value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null
                ) {
                   error.printStackTrace()
                } else {
                    if (value != null) {
                        invitationList.clear()
                        for (documentSnapshot in value) {
                            if (documentSnapshot != null) {
                                val invitations: Invitations = documentSnapshot.toObject(Invitations::class.java)
                                invitationList.add(invitations)
                            }
                        }
                        invitationAdapter = InvitationAdapter(binding.root.context,invitationList,this)
                        binding.recyclerviewInvitations.adapter = invitationAdapter
                    }
                }

            }
    }

    override fun accept(position: Int) {
        val invitations = invitationList[position]
        val students = Students(invitations.studentID,1)
        acceptInvite(invitations.classID!!,students)
    }

    override fun reject(position: Int) {
        val invitations = invitationList[position]
        deleteInvitation(invitations.classID!!,invitations.studentID!!)
    }
    private fun acceptInvite(classID : String,students: Students) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(classID)
            .collection(Students.TABLE_NAME)
            .document(students.studentID!!)
            .set(students)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(view?.context,"You successfully joined the class",Toast.LENGTH_SHORT).show()
                    deleteInvitation(classID,students.studentID)
                }
                else {
                    Toast.makeText(view?.context,"You rejected to join the class",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun deleteInvitation(invitationID: String,myID: String) {
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .collection(Invitations.TABLE_NAME)
            .document(invitationID)
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(".NotificationFragment","Invitation: Deleted Success!")
                } else {
                    Log.d(".NotificationFragment","Invitation: Deleted Failed!")
                }

            }
    }
}