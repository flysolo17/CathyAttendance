package com.ketchupzzz.cathyattendance.loginsystem

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityCreateAccountBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.Users


class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateAccountBinding
    private lateinit var validation : Validation
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var progressDialog : ProgressDialog
    private fun init() {
        validation = Validation()
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        init()
        login(binding.textButtonSignIN)
        val userType = intent.getStringExtra("userType")
        binding.buttonCreateAccount.setOnClickListener {
            val firstname : String = binding.inputFirstname.text.toString()
            val middleName : String = binding.inputMiddleName.text.toString()
            val lastname : String = binding.inputLastname.text.toString()
            val idNumber : String = binding.inputIDNumber.text.toString()
            val email : String = binding.inputEmail.text.toString()
            val password : String = binding.inputPassword.text.toString()
            if (firstname.isEmpty()) {
                binding.inputFirstname.error = "enter firstname"
            }
            else if (middleName.isEmpty()){
                binding.inputFirstname.error = "enter middle name"
            }
            else if (lastname.isEmpty()){
                binding.inputLastname.error = "enter lastname"
            }
            else if (idNumber.isEmpty()){
                binding.inputIDNumber.error = "enter ID number"
            }
            else if (!validation.validateCard(binding.inputEmail) ){
                return@setOnClickListener
            }
            else if (!validation.validatePassword(binding.inputPassword)) {
                return@setOnClickListener
            }
            else {
                if (userType!!.isNotEmpty()){
                    createAccount(firstname,middleName,lastname, userType,idNumber,email,password)
                } else {
                    Toast.makeText(this,"Failed: Invalid user type",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    private fun createAccount(firstname : String,middleName : String,lastname : String,userType : String,idNumber : String, email : String,password : String) {
        progressDialog.loading("Creating account......")
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser : FirebaseUser? = firebaseAuth.currentUser
                    if (currentUser != null) {
                        val userID : String = currentUser.uid
                        val user = Users(userID,idNumber,"",firstname, middleName, lastname,userType,email)
                        progressDialog.stopLoading()
                        saveUserInfo(user)
                    } else {
                        progressDialog.stopLoading()
                        Toast.makeText(this,"Failed to create account",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    private fun saveUserInfo(user: Users){
        progressDialog.loading("Saving......")
        firestore.collection("Users")
            .document(user.userID!!)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Account created: Successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,LoginActivity::class.java))
                } else {
                    Toast.makeText(this,"Failed to create account!",Toast.LENGTH_SHORT).show()
                }
                progressDialog.stopLoading()
            }
    }

    private fun login(textView : TextView) {
        val ss = SpannableString(getString(R.string.already_have_an_account_sign_in))
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(this@CreateAccountActivity,LoginActivity::class.java))
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FFC107")
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan1, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}