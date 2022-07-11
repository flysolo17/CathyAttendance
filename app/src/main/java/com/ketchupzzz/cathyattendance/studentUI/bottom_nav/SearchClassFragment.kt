package com.ketchupzzz.cathyattendance.studentUI.bottom_nav

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.eventbus.SubscriberExceptionContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentSearchClassBinding
import com.ketchupzzz.cathyattendance.models.Invitations
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.adapter.StudentClassesAdapter
import org.w3c.dom.Text


class SearchClassFragment : Fragment(),StudentClassesAdapter.ViewClassroom {
    private lateinit var binding : FragmentSearchClassBinding
    private lateinit var classList : MutableList<SubjectClass>
    private lateinit var studentClassesAdapter : StudentClassesAdapter
    private lateinit var firestore : FirebaseFirestore
    private fun init(myID: String) {
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewOtherClasses.layoutManager = LinearLayoutManager(binding.root.context)
        getALlClasses(myID)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchClassBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myID = FirebaseAuth.getInstance().currentUser!!.uid
        init(myID)

    }
    private fun getALlClasses(myID: String){
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
    private fun fetchAllMyClass(subjectClass: SubjectClass, myID: String) {
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectClass.classID!!)
            .collection(Students.TABLE_NAME)
            .document(myID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                } else {
                    if (value != null) {
                        if (!value.exists()) {
                            classList.add(subjectClass)
                            studentClassesAdapter = StudentClassesAdapter(binding.root.context,classList,this)
                            binding.recyclerviewOtherClasses.adapter = studentClassesAdapter
                        }
                    }
                }
            }
    }

    override fun onClassroomClick(position: Int) {
        val subjectClass = classList[position]
        if (subjectClass.open!!) {
            showJoinClassDialog(position)
        } else {
            Toast.makeText(context,"${subjectClass.classTitle} is not accepting students anymore",Toast.LENGTH_SHORT).show()
        }

    }
    private fun showJoinClassDialog(position: Int) {
        val view = LayoutInflater.from(binding.root.context).inflate(R.layout.dialog_join_class,binding.root,false)
        val subjectClass = classList[position]
      MaterialAlertDialogBuilder(binding.root.context)
            .setView(view)
          .setNegativeButton("Cancel") { dialog, i ->
              dialog.dismiss()
          }
          .setPositiveButton("Accept") { dialog, i ->
              val input : EditText = view.findViewById(R.id.inputClassCode)
              if (subjectClass.classCode == input.text.toString()) {
                  val students = Students(FirebaseAuth.getInstance().currentUser?.uid)
                  acceptInvite(subjectClass.classID!!,students)
              } else {
                  Toast.makeText(context, "Invalid Code", Toast.LENGTH_SHORT).show()
              }
          }.show()
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
                    Log.d(".SearchClassFragment","Invitation: Deleted Success!")
                } else {
                    Log.d(".SearchClassFragment","Invitation: Deleted Failed!")
                }

            }
    }
}