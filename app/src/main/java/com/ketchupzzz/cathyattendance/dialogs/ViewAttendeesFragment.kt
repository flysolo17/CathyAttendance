package com.ketchupzzz.cathyattendance.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentViewAttendeesBinding
import com.ketchupzzz.cathyattendance.models.Attendees
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.viewmodels.AttendeesViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ViewAttendeesFragment : DialogFragment() {
    private lateinit var binding : FragmentViewAttendeesBinding
    private lateinit var attendeesViewModel: AttendeesViewModel
    private lateinit var firestore: FirebaseFirestore
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
        // Inflate the layout for this fragment
        binding = FragmentViewAttendeesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.buttonback.setOnClickListener { dismiss() }
        attendeesViewModel = ViewModelProvider(requireActivity())[AttendeesViewModel::class.java]
        attendeesViewModel.getAttendance().observe(viewLifecycleOwner) {attendance ->
            if (attendance != null) {
                attendance.attendees.map { attendees ->
                    displayAttendees(attendees)
                }
                binding.textDate.text = timestampToDate(attendance.timestamp) + " attendance"
            } else {
                binding.textNoAttendees.visibility =View.VISIBLE
            }

        }
    }

    private fun displayAttendees(attendees: Attendees){
        val view : View = layoutInflater.inflate(R.layout.row_attendees,binding.root,false)
        val imageSelfie : ImageView = view.findViewById(R.id.imageAttendee)
        val textStudentName : TextView = view.findViewById(R.id.textStudentsFullname)
        val textTimestamp : TextView = view.findViewById(R.id.textTimestamp)
        if (attendees.studentPicture!!.isNotEmpty()) {
            Picasso.get().load(attendees.studentPicture).into(imageSelfie)
        }
        displayStudentName(attendees.studentID!!,textStudentName)
        textTimestamp.text = timestampToTime(attendees.timestamp!!)
        binding.layoutAttendees.addView(view)
    }
    private fun displayStudentName(studentID : String,textView: TextView) {
        firestore.collection(Users.TABLE_NAME)
            .document(studentID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Users::class.java)
                    if (user != null) {
                        textView.text = "${user.firstname} ${user.lastname}"
                    }
                }
            }
    }

    private fun timestampToDate(timestamp : Long) : String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        return format.format(date)
    }
    private fun timestampToTime(timestamp : Long) : String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm aa", Locale.US)
        return format.format(date)
    }
}