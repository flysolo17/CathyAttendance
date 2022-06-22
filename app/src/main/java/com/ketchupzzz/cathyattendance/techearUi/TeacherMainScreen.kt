package com.ketchupzzz.cathyattendance.techearUi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.ActivityTeacherMainScreenBinding

class TeacherMainScreen : AppCompatActivity() {
    private lateinit var binding : ActivityTeacherMainScreenBinding
    private var navController: NavController? = null
    private fun init() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav()
        binding.floatingActionButton.setOnClickListener {
            navController?.navigate(R.id.createClass)
        }
    }
    private fun setupNav() {
        navController = findNavController(R.id.fragmentContainer)
        binding.teachersNav.setupWithNavController(navController!!)
        binding.teachersNav.background = null
        binding.teachersNav.menu[1].isEnabled = false
        navController!!.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.nav_home -> {
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

        binding.floatingActionButton.show()
    }
    private fun hideBottomNav() {
        binding.bottomAppBar.performHide(true)
        binding.bottomAppBar.hideOnScroll = false
        binding.floatingActionButton.hide()
    }
}