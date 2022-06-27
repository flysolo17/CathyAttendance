package com.ketchupzzz.cathyattendance.techearUi.bottom_nav

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.ketchupzzz.cathyattendance.databinding.FragmentCreateClassBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.SubjectClass
import java.io.IOException
import java.io.NotActiveException


class CreateClassFragment : Fragment() {
    private lateinit var binding : FragmentCreateClassBinding

    private var classPicture : Uri? = null
    private lateinit var firestore : FirebaseFirestore
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var storage: StorageReference? = null
    private var mUploadTask: StorageTask<*>? = null
    private lateinit var myID : String
    private var cameraPermissionGranted = false
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        myID = FirebaseAuth.getInstance().currentUser!!.uid
        progressDialog = ProgressDialog(requireActivity())

        storage = FirebaseStorage.getInstance().getReference("$myID/classImages")
    }
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateClassBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        permissionLauncher = registerForActivityResult(
            RequestPermission()
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

        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
        binding.fabSaveClass.setOnClickListener {
            val classID = firestore.collection(SubjectClass.TABLE_NAME).document().id
            val classTitle : String = binding.inputClassTitle.text.toString()
            val classDesc = binding.inputClassDesc.text.toString()
            if (classTitle.isEmpty()) {
                binding.inputClassTitle.error = "enter title"
            } else if (classDesc.isEmpty()) {
                binding.inputClassDesc.error = "enter desc"
            } else {
                val subjectClass = SubjectClass(classID,myID,"",classTitle,classDesc)
                if (classPicture != null) {
                    uploadClassProfile(classPicture!!,subjectClass)
                } else {
                    saveClass(subjectClass)
                }
            }
        }
        binding.buttonGallery.setOnClickListener {
            launchGallery()
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
                Toast.makeText(binding.root.context,"Failed: Uploading",Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveClass(subjectClass: SubjectClass){
        progressDialog.loading("Saving.....")
        firestore.collection(SubjectClass.TABLE_NAME).document(subjectClass.classID!!)
            .set(subjectClass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(binding.root.context,"Success",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"Failed",Toast.LENGTH_SHORT).show()
                }
                Navigation.findNavController(binding.root).popBackStack()
                progressDialog.stopLoading()
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