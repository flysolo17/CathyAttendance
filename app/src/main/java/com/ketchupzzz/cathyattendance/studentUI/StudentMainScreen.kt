package com.ketchupzzz.cathyattendance.studentUI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityStudentMainScreenBinding
import com.ketchupzzz.cathyattendance.loginsystem.LoginActivity

class StudentMainScreen : AppCompatActivity() {
    private lateinit var binding : ActivityStudentMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}