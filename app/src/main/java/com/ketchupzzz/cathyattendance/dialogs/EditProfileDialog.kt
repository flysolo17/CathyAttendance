package com.ketchupzzz.cathyattendance.dialogs

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentEditProfileDialogBinding
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import java.io.IOException


class EditProfileDialog : DialogFragment() {
    private lateinit var binding : FragmentEditProfileDialogBinding
    private lateinit var userViewModel: UserViewModel
    private  var users: Users? = null
    private lateinit var firestore: FirebaseFirestore
    private var imageUserProfile : Uri? = null
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private lateinit var progressDialog: ProgressDialog
    private var storage: StorageReference? = null
    private var mUploadTask: StorageTask<*>? = null
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        progressDialog = ProgressDialog(requireActivity())
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
        binding = FragmentEditProfileDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        userViewModel.getUser().observe(viewLifecycleOwner) { users ->
            this.users = users
            storage = FirebaseStorage.getInstance().getReference("${users.userID}/profile")
            bindViews(users)
        }
        //Get image in the gallery
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                if (data?.data != null) {
                    binding.imageUserProfile.setImageURI(data.data)
                    imageUserProfile = data.data
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        binding.buttonGallery.setOnClickListener {
            launchGallery()
        }
        binding.buttonSave.setOnClickListener {
            val firstname = binding.inputFirstname.text.toString()
            val middleName = binding.inputMiddleName.text.toString()
            val lastname = binding.inputLastname.text.toString()
            val idNumber = binding.inputIDNumber.text.toString()
            if (firstname.isEmpty()) {
                binding.inputFirstname.error = "enter firstname"
            }
            if (middleName.isEmpty()) {
                binding.inputMiddleName.error = "enter middle name"
            }
            else if (lastname.isEmpty()) {
                binding.inputLastname.error = "enter lastname"
            }
            else if (idNumber.isEmpty()) {
                binding.inputIDNumber.error = "enter id number"
            }
            else {
                val users = Users(users?.userID,idNumber, users!!.userProfile,firstname,middleName,lastname,
                    users!!.userType,
                    users!!.email)
                if (imageUserProfile != null) {
                    uploadUserProfile(imageUserProfile!!,users)
                } else {
                    updateUser(users)
                }
            }
        }

    }
    private fun uploadUserProfile(uploadUri: Uri,users: Users){
        progressDialog.loading("Uploading...")

        val fileReference = storage!!.child(System.currentTimeMillis().toString() + "." + getFileExtension(uploadUri))
        mUploadTask = fileReference.putFile(uploadUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri: Uri ->
                    progressDialog.stopLoading()
                    users.userProfile = uri.toString()
                    updateUser(users)
                }
            }.addOnFailureListener {
                progressDialog.stopLoading()
                Toast.makeText(binding.root.context,"Failed: Uploading",Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateUser(users: Users) {
        progressDialog.loading("Updating profile.......")
        firestore.collection(Users.TABLE_NAME)
            .document(users.userID!!)
            .set(users)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(view?.context,"Successful",Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(view?.context,"Failed",Toast.LENGTH_SHORT).show()
                }
                progressDialog.stopLoading()

            }
    }
    private fun bindViews(users: Users){
        if (users.userProfile.isNotEmpty()) {
            Picasso.get().load(users.userProfile).into(binding.imageUserProfile)
        }
        binding.inputFirstname.setText(users.firstname)
        binding.inputMiddleName.setText(users.middleName)
        binding.inputLastname.setText(users.lastname)
        binding.inputIDNumber.setText(users.idNumber)
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