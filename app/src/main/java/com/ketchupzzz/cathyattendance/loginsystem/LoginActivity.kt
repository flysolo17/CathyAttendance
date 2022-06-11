package com.ketchupzzz.cathyattendance.loginsystem


import android.content.Intent
import android.graphics.Color
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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityLoginBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.StudentMainScreen
import com.ketchupzzz.cathyattendance.techearUi.TeacherMainScreen


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestore : FirebaseFirestore
    private lateinit var progressDialog : ProgressDialog
    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        init()
        signUp(binding.textCreateAccount)


    }
    private fun signUp( textview : TextView) {
        val ss = SpannableString(getString(R.string.don_t_have_an_account_sign_up))
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                chooseUserType()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FFC107")
                ds.isUnderlineText = true
            }

        }
        ss.setSpan(clickableSpan1, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textview.text = ss
        textview.movementMethod = LinkMovementMethod.getInstance()

    }
    private fun chooseUserType() {
        val singleItems = arrayOf("Student","Teacher")
        var checkedItem = 0
        var  selectedType = singleItems[checkedItem]
        MaterialAlertDialogBuilder(this)
            .setTitle("Choose Account")
            .setPositiveButton("Continue") { _, _ ->
                startActivity(Intent(this@LoginActivity,CreateAccountActivity::class.java).putExtra("userType",selectedType))
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
                selectedType = singleItems[which]
            }
            .show()
    }
    private fun updateUI(userID : String){
        progressDialog.loading("Logging in......")
        firestore.collection("Users")
            .document(userID)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user  = document.toObject(Users::class.java)
                    if (user != null) {
                        when {
                            user.userType.equals("Student") -> {
                                progressDialog.stopLoading()
                                startActivity(Intent(this,StudentMainScreen::class.java))
                            }
                            user.userType.equals("Teacher") -> {
                                progressDialog.stopLoading()
                                startActivity(Intent(this,TeacherMainScreen::class.java))
                            }
                            else -> {
                                progressDialog.stopLoading()
                                Toast.makeText(this,"Invalid user",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
            }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            updateUI(currentUser.uid)
        }
    }
}