package com.ketchupzzz.cathyattendance.studentUI.tabs

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentStudentAttendanceTabBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.dialogs.ViewAttendeesFragment
import com.ketchupzzz.cathyattendance.models.Attendance
import com.ketchupzzz.cathyattendance.models.Attendees
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.studentUI.adapter.StudentAttendanceAdapter
import com.ketchupzzz.cathyattendance.studentUI.classroom.StudentClassroomFragment
import com.ketchupzzz.cathyattendance.viewmodels.AttendeesViewModel
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


class StudentAttendanceTab : Fragment(),StudentAttendanceAdapter.StudentAttendanceListeners {
    private lateinit var binding: FragmentStudentAttendanceTabBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var attendanceList : MutableList<Attendance>
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter
    private var cameraPermissionGranted = false
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var storage: StorageReference? = null
    private var mUploadTask: StorageTask<*>? = null
    private var selfieUri: Uri? = null
    private var  position: Int ? = null
    private lateinit var progressDialog : ProgressDialog
    private lateinit var attendeesViewModel: AttendeesViewModel
    private fun init(classID : String) {
        progressDialog = ProgressDialog(requireActivity())
        firestore = FirebaseFirestore.getInstance()
        binding.recyclerviewStudentAttendance.layoutManager = LinearLayoutManager(binding.root.context)
        getAllAttendance(classID)
        storage = FirebaseStorage.getInstance().getReference("SubjectClass/$classID/${dateFormatter(System.currentTimeMillis())}/")
        attendeesViewModel = ViewModelProvider(requireActivity())[AttendeesViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentAttendanceTabBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(StudentClassroomFragment.subjectClass?.classID!!)
        //Get image in the gallery
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.data != null) {
                val bitmap = result.data!!.extras!!.get("data") as Bitmap?
                if (bitmap != null) {
                    selfieUri = convertBitmapToUri(System.currentTimeMillis().toString(), bitmap)
                    showSelfieDialog(selfieUri!!, position!!)
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cameraPermissionGranted = true
                launchCamera(cameraLauncher)
            } else {
                Toast.makeText(
                    binding.root.context, "You cannot use camera", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun getAllAttendance(subjectID : String){
        attendanceList = mutableListOf()
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(subjectID)
            .collection(Attendance.TABLE_NAME)
            .addSnapshotListener { value, error ->
                attendanceList.clear()
                if (error != null) {
                    error.printStackTrace()
                } else {
                    value?.map { queryDocumentSnapshot ->
                        val attendance = queryDocumentSnapshot.toObject(Attendance::class.java)
                        attendanceList.add(attendance)
                    }
                    studentAttendanceAdapter = StudentAttendanceAdapter(binding.root.context,attendanceList,this)
                    binding.recyclerviewStudentAttendance.adapter = studentAttendanceAdapter
                }
            }
    }


    override fun takeAttendance(position: Int) {
        this.position = position
        if (attendanceList[position].accepting) {
            checkIfAttendanceExists(attendanceList[position].attendees,FirebaseAuth.getInstance().currentUser!!.uid)
        } else {
            Toast.makeText(binding.root.context,"Attendance is closed",Toast.LENGTH_SHORT).show()
        }

    }

    override fun viewAttendees(position: Int) {
        attendeesViewModel.setAttendance(attendanceList[position])
        val viewAttendeesFragment = ViewAttendeesFragment();
        if (!viewAttendeesFragment.isAdded) {
            viewAttendeesFragment.show(childFragmentManager,"View Attendees")
        }
    }

    private fun checkIfAttendanceExists(attendeesList: List<Attendees>,myID : String) {
        if (attendeesList.isNotEmpty()) {
            attendeesList.map { attendees ->
                if (attendees.studentID!! == myID) {
                    Toast.makeText(binding.root.context,"You already take attendance",Toast.LENGTH_SHORT).show()
                } else {
                    launchCamera(cameraLauncher)
                }
            }
        } else {
            launchCamera(cameraLauncher)
        }
    }

    //TODO: get the file extension of the file
    private fun getFileExtension(uri: Uri): String? {
        val cR = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
    //TODO: pick image from the gallery
    @SuppressLint("QueryPermissionsNeeded")
    private fun launchCamera(launcher: ActivityResultLauncher<Intent>) {
        if (cameraPermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra("position",90)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    launcher.launch(intent)
                }

            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                launcher.launch(intent)
            }
        } else {
            permissionLauncher!!.launch(Manifest.permission.CAMERA)
        }

    }
    private fun convertBitmapToUri(name: String, bitmap: Bitmap): Uri? {
        val imageCollection: Uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }
        return requireActivity().contentResolver.insert(imageCollection, contentValues)?.also {
            requireActivity().contentResolver.openOutputStream(it).use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Failed to save bitmap")
                }
            }
        }
    }

    private fun showSelfieDialog(uri: Uri,position: Int) {
        val view : View = LayoutInflater.from(binding.root.context).inflate(R.layout.view_take_attendance,binding.root,false)
        val selfieImage : ImageView = view.findViewById(R.id.imageMySelfie)
        selfieImage.setImageURI(uri)
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle("Your Selfie")
            .setView(view)
            .setPositiveButton("Save") { dialog ,_ ->
                val attendees = Attendees(FirebaseAuth.getInstance().currentUser?.uid)
                uploadClassProfile(uri,attendees,position)
                dialog.dismiss()
            }
            .setNegativeButton("Retake") { dialog, _ ->
                launchCamera(cameraLauncher)
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") {dialog ,_ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun uploadClassProfile(uploadUri: Uri,attendees: Attendees,position: Int){
        progressDialog.loading("Uploading...")
        val fileReference = storage!!.child(System.currentTimeMillis().toString() + "." + getFileExtension(uploadUri))
        mUploadTask = fileReference.putFile(uploadUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri: Uri ->
                    progressDialog.stopLoading()
                    attendees.studentPicture = uri.toString()
                    attendees.timestamp = System.currentTimeMillis()
                    saveAttendance(attendees, position)
                }
            }.addOnFailureListener {
                progressDialog.stopLoading()
                Toast.makeText(binding.root.context,"Failed: Uploading",Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveAttendance(attendees: Attendees,position: Int) {
        progressDialog.loading("Saving Attendance.....")
        firestore.collection(SubjectClass.TABLE_NAME)
            .document(StudentClassroomFragment.subjectClass?.classID!!)
            .collection(Attendance.TABLE_NAME)
            .document(attendanceList[position].attendanceID!!)
            .update(Attendance.ATTENDEES,FieldValue.arrayUnion(attendees))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context,"Attendance Saved!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Attendance Failed!",Toast.LENGTH_SHORT).show()
                }
                progressDialog.stopLoading()
            }

    }
    private fun dateFormatter(timestamp: Long) : String{
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("dd-MM-yy",Locale.US)
        val dateTime = simpleDateFormat.format(date.time)
        return dateTime.toString()
    }
}