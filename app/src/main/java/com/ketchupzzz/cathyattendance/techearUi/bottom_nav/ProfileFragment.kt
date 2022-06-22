package com.ketchupzzz.cathyattendance.techearUi.bottom_nav

import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.databinding.FragmentProfileBinding
import com.ketchupzzz.cathyattendance.loginsystem.LoginActivity
import com.ketchupzzz.cathyattendance.models.Users
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var firestore : FirebaseFirestore
    private fun init(myID: String) {
        firestore = FirebaseFirestore.getInstance()
        bindViews(myID)
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
        init(FirebaseAuth.getInstance().currentUser?.uid!!)
        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
        // Callback registration
        binding.loginButton.setFragment(this)
        callbackManager = CallbackManager.Factory.create() //initialize callback manager
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                linkWithFacebook(result.accessToken!!)
            }

            override fun onCancel() {
                Toast.makeText(view.context ?: requireContext(),"cancel",Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(view.context ?: requireContext(),exception.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })
        binding.loginButton.setOnClickListener{

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        }
    }

    private fun bindViews(myID: String) {
        firestore.collection(Users.TABLE_NAME)
            .document(myID)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Users::class.java)
                    if (user != null) {
                        if (user.userProfile.isNotEmpty()) {
                            Picasso.get().load(user.userProfile).into(binding.imageUserProfile)
                        }
                        val fullname = user.firstname + " " + user.middleName + " " + user.lastname
                        binding.textUserFullname.text = fullname
                        binding.textUserEmail.text = user.email
                        binding.textUserID.text = user.idNumber
                        binding.textUsertype.text = user.userType
                    }
                }
            }
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
}