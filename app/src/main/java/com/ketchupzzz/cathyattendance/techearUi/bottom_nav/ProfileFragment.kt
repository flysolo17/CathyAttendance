package com.ketchupzzz.cathyattendance.techearUi.bottom_nav

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.databinding.FragmentProfileBinding
import com.ketchupzzz.cathyattendance.dialogs.ChangePasswordDialog
import com.ketchupzzz.cathyattendance.dialogs.EditProfileDialog
import com.ketchupzzz.cathyattendance.loginsystem.LoginActivity
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.viewmodels.UserViewModel
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var firestore : FirebaseFirestore
    private lateinit var userViewModel: UserViewModel
    private var accessToken = AccessToken
    private  var users: Users? = null
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        callbackManager = CallbackManager.Factory.create() //initialize callback manager
        binding.buttonLogout.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out.")
                .setNegativeButton("Cancel") { dialog, i ->
                    dialog.dismiss()
                }.setPositiveButton("Logout") { _, _ ->

                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))

                }.show()

        }
        // Callback registration
        binding.loginButton.setFragment(this)

        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                linkWithFacebook(result.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(view.context,"cancel",Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
            }
        })

        binding.buttonEditProfile.setOnClickListener {
            if (users != null) {
                userViewModel.setUser(users!!)
                val editProfileDialog = EditProfileDialog()
                if (!editProfileDialog.isAdded) {
                    editProfileDialog.show(parentFragmentManager,"Edit Profile")
                }
            }
        }
        binding.buttonChangePassword.setOnClickListener {
            val changePasswordDialog = ChangePasswordDialog()
            if (!changePasswordDialog.isAdded) {
                changePasswordDialog.show(parentFragmentManager,"Change Password")

            }
        }
    }

    private fun getUserInfo(myID: String) {
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Users::class.java)
                    if (user != null) {
                        users = user
                        bindViews(users!!)
                    }
                }
            }

    }

    private fun bindViews(user: Users){
        if (user.userProfile.isNotEmpty()) {
            Picasso.get().load(user.userProfile).into(binding.imageUserProfile)
        }
        val fullName = user.firstname + " " + user.middleName + " " + user.lastname
        binding.textUserFullname.text = fullName
        binding.textUserEmail.text = user.email
        binding.textUserID.text = user.idNumber
        binding.textUsertype.text = user.userType
    }
    private fun linkWithFacebook(token : AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val prevUser = FirebaseAuth.getInstance().currentUser
        prevUser!!.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(binding.root.context,"Success",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context,"failed",Toast.LENGTH_SHORT).show()
                }
            }
    }
    companion object {
        const val TAG = ".ProfileFragment"
    }

    override fun onResume() {
        super.onResume()
        getUserInfo(FirebaseAuth.getInstance().currentUser!!.uid)
    }
}