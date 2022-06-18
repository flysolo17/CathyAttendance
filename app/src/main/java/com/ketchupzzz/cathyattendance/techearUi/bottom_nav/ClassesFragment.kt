package com.ketchupzzz.cathyattendance.techearUi.bottom_nav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentClassesBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.techearUi.adapter.SubjectClassAdapter
import com.squareup.picasso.Picasso


class ClassesFragment : Fragment(),SubjectClassAdapter.ViewClassroom {
    private lateinit var binding : FragmentClassesBinding
    private lateinit var subjectClassAdapter: SubjectClassAdapter
    private lateinit var classList: MutableList<SubjectClass>
    private lateinit var firestore : FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private fun init() {
        progressDialog = ProgressDialog(requireActivity())
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewMyClass.layoutManager = LinearLayoutManager(binding.root.context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getAllMyClasses(FirebaseAuth.getInstance().currentUser!!.uid)
        swipeToDelete(binding.recyclerviewMyClass)
        bindViews(FirebaseAuth.getInstance().currentUser!!.uid)
    }
    private fun bindViews(myID: String) {
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Users::class.java)
                    if (user != null) {
                        if (user.userProfile.isNotEmpty()) {
                            Picasso.get().load(user.userProfile).into(binding.userProfile)
                        }
                        val fullname = user.firstname + " " + user.lastname
                        binding.textUserFullname.text = fullname
                    }
                }
            }
    }
    private fun getAllMyClasses(myID: String) {
        classList = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .whereEqualTo(SubjectClass.TEACHER_ID,myID)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                classList.clear()
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (documentSnapshot in value) {
                        if (documentSnapshot != null) {
                            val subjectClass: SubjectClass = documentSnapshot.toObject(SubjectClass::class.java)
                            classList.add(subjectClass)
                        }
                    }
                    subjectClassAdapter = SubjectClassAdapter(binding.root.context, classList,this)
                    noClasses(classList)
                    binding.recyclerviewMyClass.adapter = subjectClassAdapter
                }
            }
    }
    private fun noClasses(classList : List<SubjectClass>){
        if (classList.isEmpty()) {
            binding.noClassContainer.visibility = View.VISIBLE
        } else {
            binding.noClassContainer.visibility = View.GONE
        }
    }
    private fun swipeToDelete(recyclerView: RecyclerView?) {
        val callback = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Yes") { _,_ ->
                        deleteClass(classList[position].classID!!)
                        subjectClassAdapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton("No") { dialog,_->
                        dialog.dismiss()
                        Toast.makeText(binding.root.context,"Cancelled",Toast.LENGTH_SHORT).show()
                        subjectClassAdapter.notifyItemChanged(position)
                    }
                    .setOnCancelListener {
                        subjectClassAdapter.notifyItemChanged(position)
                    }.show()
            }
        })
        callback.attachToRecyclerView(recyclerView)
    }
    private fun deleteClass(id: String) {
        progressDialog.loading("Deleting.....")
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(id).delete().addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Item deleted successfully..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
                progressDialog.stopLoading()
            }
    }


    companion object {
        const val TAG = ".ClassesFragment"
    }

    override fun onClassroomClick(position: Int) {
        val action :NavDirections = ClassesFragmentDirections.actionNavHomeToClassroomFragment(classList[position])
        Navigation.findNavController(binding.root).navigate(action)
    }
}