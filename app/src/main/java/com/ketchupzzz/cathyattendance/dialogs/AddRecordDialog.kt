package com.ketchupzzz.cathyattendance.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.databinding.DialogAddRecordBinding
import com.ketchupzzz.cathyattendance.models.Activities
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.techearUi.classroom.ClassroomFragment
import com.ketchupzzz.cathyattendance.viewmodels.StudentsViewModel


class AddRecordDialog : DialogFragment() {
    private lateinit var binding : DialogAddRecordBinding
    private lateinit var studentsViewModel: StudentsViewModel
    private var students : Students?= null
    private var dateTaken : Long? = null
    private lateinit var firestore: FirebaseFirestore
    private var progressDialog : ProgressDialog? = null
    private fun init() {
        progressDialog = ProgressDialog(requireActivity())
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
        // Inflate the layout for this fragment
        binding = DialogAddRecordBinding.inflate(inflater,container,false)
        studentsViewModel = ViewModelProvider(requireActivity())[StudentsViewModel::class.java]
        studentsViewModel.getStudent().observe(viewLifecycleOwner){ student ->
            this.students = student
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.buttonBack.setOnClickListener { dismiss() }
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("SELECT A DATE")
        val materialDatePicker = materialDateBuilder.build()
        binding.buttonDate.setOnClickListener {
            materialDatePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")
        }
        materialDatePicker.addOnPositiveButtonClickListener {
            binding.textDateTaken.text = materialDatePicker.headerText
            dateTaken = materialDatePicker.selection as Long?
        }
        binding.buttonSave.setOnClickListener {
            val activityName : String = binding.inputActivityName.text.toString()
            val activityType : String = binding.inputActivityType.text.toString()
            val score : String = binding.inputScore.text.toString()
            val maxScore : String = binding.inputMaxScore.text.toString()
            if (activityName == "") {
                binding.inputActivityName.error = "Input Activity Name"
            }
            else if (activityType == "") {
                binding.inputActivityType.error = "Input Activity Type"
            }
            else if (score == "") {
                binding.inputScore.error = "Input Score"
            }
            else if (maxScore == "") {
                binding.inputMaxScore.error = "Input max score"
            } else if (Integer.parseInt(score) > Integer.parseInt(maxScore)) {
                binding.inputMaxScore.error = "score should be lesser than activity total score"
            }
            else if (dateTaken == null) {
                Snackbar.make(view,"Put date",Snackbar.LENGTH_SHORT).show()
            } else {
                val activityID = firestore.collection(Activities.TABLE_NAME).document().id
                val activities = Activities(activityID, ClassroomFragment.subjectClass!!.classID,
                    students?.studentID,activityName,activityType,Integer.parseInt(score),Integer.parseInt(maxScore),dateTaken)
                addNewRecord(activities)
            }
        }
    }
    private fun addNewRecord(activities: Activities) {
        progressDialog?.loading("Adding new Record......")
        firestore.collection(Activities.TABLE_NAME)
            .document(activities.activityID!!)
            .set(activities)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    Toast.makeText(view?.context,"New Record Added",Toast.LENGTH_SHORT).show()
                    dismiss()
                }else {
                    dismiss()
                    Toast.makeText(view?.context,"Record Addition Failed",Toast.LENGTH_SHORT).show()
                }
                progressDialog?.stopLoading()
            }
    }

}