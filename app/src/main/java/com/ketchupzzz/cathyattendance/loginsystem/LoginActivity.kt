package com.ketchupzzz.cathyattendance.loginsystem


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityLoginBinding
import com.ketchupzzz.cathyattendance.dialogs.ProgressDialog
import com.ketchupzzz.cathyattendance.models.Users
import com.ketchupzzz.cathyattendance.studentUI.StudentMainScreen
import com.ketchupzzz.cathyattendance.techearUi.TeacherMainScreen
import java.util.*


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestore : FirebaseFirestore
    private lateinit var progressDialog : ProgressDialog
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var validation: Validation
    private lateinit var callbackManager: CallbackManager


    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
        firebaseAuth = FirebaseAuth.getInstance()
        validation = Validation()


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        init()
        signUp(binding.textCreateAccount)



        binding.buttonLoginAccount.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            if (!validation.validateCard(binding.inputEmail)) {
                return@setOnClickListener
            }
            else if (!validation.validatePassword(binding.inputPassword)){
                return@setOnClickListener
            } else {
                signInWithEmail(email,password)
            }
        }

        callbackManager = create()


        // Callback registration
        // Callback registration
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                loginWithFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity,"Cancelled",Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG,exception.message.toString())
            }
        })
        binding.loginButton.setOnClickListener{

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        }
    }
    private fun signInWithEmail(email : String, password : String){
        progressDialog.loading("Logging in....")
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser: FirebaseUser? = firebaseAuth.currentUser
                    progressDialog.stopLoading()
                    updateUI(currentUser!!.uid)
                } else if (!it.isSuccessful) {
                    try {
                        throw Objects.requireNonNull<Exception>(it.exception)
                    } // if user enters wrong email.
                    catch (invalidEmail: FirebaseAuthInvalidUserException) {
                        progressDialog.stopLoading()
                        Log.d(LOGIN_ACTIVITY, "onComplete: invalid_email")
                        Toast.makeText(applicationContext, "Invalid Email", Toast.LENGTH_SHORT)
                            .show()

                    } // if user enters wrong password.
                    catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                        Log.d(
                            LOGIN_ACTIVITY, "onComplete: wrong_password"
                        )
                        progressDialog.stopLoading()
                        Toast.makeText(applicationContext, "Wrong Password", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: Exception) {
                        progressDialog.stopLoading()
                        Log.d(LOGIN_ACTIVITY, "onComplete: " + e.message)
                    }
                } else {
                    progressDialog.stopLoading()
                    // If sign in fails, display a message to the user.
                    Log.w(LOGIN_ACTIVITY, "signInWithCredential:failure", it.exception)
                    Snackbar.make(binding.root, "Login Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
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
        progressDialog.loading("Verifying user......")
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
    private fun loginWithFacebook(token: AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                val currentUser: FirebaseUser? = task.result.user
                updateUI(currentUser?.uid!!)
            }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            updateUI(currentUser.uid)
        }
    }



    companion object {
        const val LOGIN_ACTIVITY = ".LoginActivity"
        const val TAG = ".SignInGoogle"
        const val EMAIL = "email"
    }

}