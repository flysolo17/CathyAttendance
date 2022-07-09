package com.ketchupzzz.cathyattendance.techearUi.classroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentUpdateClassBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.viewmodels.SubjectClassViewModel
import com.squareup.picasso.Picasso
import java.io.IOException


class UpdateClassFragment : DialogFragment() {
    private lateinit var binding :FragmentUpdateClassBinding
    private lateinit var subjectClassViewModel: SubjectClassViewModel
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var cameraPermissionGranted = false
    private var mUploadTask: StorageTask<*>? = null
    private var subjectClass : SubjectClass?  = null

    private var classPicture : Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private var storage: StorageReference? = null
    private lateinit var firestore : FirebaseFirestore
    private fun init(myID : String) {
        firestore = FirebaseFirestore.getInstance()

        progressDialog = ProgressDialog(requireActivity())

        storage = FirebaseStorage.getInstance().getReference("$myID/classImages")
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
        binding = FragmentUpdateClassBinding.inflate(inflater,container,false)
        subjectClassViewModel = ViewModelProvider(requireActivity())[SubjectClassViewModel::class.java]
        subjectClassViewModel.getSubjectClass().observe(viewLifecycleOwner) { subjectClass ->
            this.subjectClass = subjectClass
            displayViews(subjectClass)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cameraPermissionGranted = true

            } else {
                Toast.makeText(
                    binding.root.context,
                    "You cannot use gallery",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //Get image in the gallery
        init(FirebaseAuth.getInstance().currentUser!!.uid)
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                if (data?.data != null) {
                    binding.imageClass.setImageURI(data.data)
                    classPicture = data.data
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        binding.buttonGallery.setOnClickListener {
            launchGallery()
        }
        binding.fabSaveClass.setOnClickListener {
            val classTitle : String = binding.inputClassTitle.text.toString()
            val classDesc = binding.inputClassDesc.text.toString()
            if (classTitle.isEmpty()) {
                binding.inputClassTitle.error = "enter title"
            } else if (classDesc.isEmpty()) {
                binding.inputClassDesc.error = "enter desc"
            } else {
                val subjectClass = SubjectClass(this.subjectClass?.classID,this.subjectClass?.classTeacherID,"",classTitle,classDesc,this.subjectClass?.open)
                if (classPicture != null) {
                    uploadClassProfile(classPicture!!,subjectClass)
                } else {
                    saveClass(subjectClass)
                }
            }
        }
    }
    private fun displayViews(subjectClass: SubjectClass) {
        binding.inputClassTitle.setText(subjectClass.classTitle)
        binding.inputClassDesc.setText(subjectClass.classDesc)
        if (subjectClass.classProfile!!.isNotEmpty()) {
            Picasso.get().load(subjectClass.classProfile).into(binding.imageClass)
        }
    }
    private fun uploadClassProfile(uploadUri: Uri,subjectClass: SubjectClass){
        progressDialog.loading("Uploading...")

        val fileReference = storage!!.child(System.currentTimeMillis().toString() + "." + getFileExtension(uploadUri))
        mUploadTask = fileReference.putFile(uploadUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri: Uri ->
                    progressDialog.stopLoading()
                    subjectClass.classProfile = uri.toString()
                    saveClass(subjectClass)

                }
            }.addOnFailureListener {
                progressDialog.stopLoading()
                Toast.makeText(binding.root.context,"Failed: Uploading", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveClass(subjectClass: SubjectClass){
        progressDialog.loading("Saving.....")
        firestore.collection(SubjectClass.TABLE_NAME).document(subjectClass.classID!!)
            .set(subjectClass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(binding.root.context,"Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Failed", Toast.LENGTH_SHORT).show()
                }

                progressDialog.stopLoading()
                dismiss()
            }
    }
    //TODO: get the file extension of the file
    private fun getFileExtension(uri: Uri): String? {
        val cR = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    //TODO: pick image from the gallery
    private fun launchGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher!!.launch(galleryIntent)
    }

}