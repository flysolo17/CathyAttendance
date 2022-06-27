package com.ketchupzzz.cathyattendance.studentUI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityStudentMainScreenBinding
import com.ketchupzzz.cathyattendance.loginsystem.LoginActivity

class StudentMainScreen : AppCompatActivity() {
    private lateinit var binding : ActivityStudentMainScreenBinding
    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav()
    }
    private fun setupNav() {
        navController = findNavController(R.id.fragmentStudentsUIContainer)
        binding.studentsNav.setupWithNavController(navController!!)
        navController!!.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.nav_my_class -> {
                    showBottomNav()
                }
                R.id.nav_notification -> {
                    showBottomNav()
                }
                R.id.nav_search_class -> {
                    showBottomNav()
                }
                else -> {
                    hideBottomNav()
                }
            }
        }
    }
    private fun showBottomNav() {
        binding.bottomAppBar.performShow(true)
        binding.bottomAppBar.hideOnScroll = true


    }
    private fun hideBottomNav() {
        binding.bottomAppBar.performHide(true)
        binding.bottomAppBar.hideOnScroll = false
    }
}