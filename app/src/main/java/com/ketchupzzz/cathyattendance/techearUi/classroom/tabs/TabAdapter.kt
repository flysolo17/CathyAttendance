package com.ketchupzzz.cathyattendance.techearUi.classroom.tabs

import android.support.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ketchupzzz.cathyattendance.models.SubjectClass


class TabAdapter(
    @NonNull fragmentManager: FragmentManager?,
    @NonNull lifecycle: Lifecycle?) :
    FragmentStateAdapter(fragmentManager!!, lifecycle!!) {
    @NonNull
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeTabFragment()
            1 -> StudentsFragment()
            2 -> SettingsFragment()
            else -> HomeTabFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}