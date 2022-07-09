package com.ketchupzzz.cathyattendance.studentUI

import android.support.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ketchupzzz.cathyattendance.studentUI.tabs.AnnouncementsTab
import com.ketchupzzz.cathyattendance.studentUI.tabs.StudentAttendanceTab
import com.ketchupzzz.cathyattendance.studentUI.tabs.StudentRecordTab
import com.ketchupzzz.cathyattendance.techearUi.classroom.tabs.HomeTabFragment

class StudentUITabAdapter (
    @NonNull fragmentManager: FragmentManager?,
    @NonNull lifecycle: Lifecycle?) :
    FragmentStateAdapter(fragmentManager!!, lifecycle!!) {
    @NonNull
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnnouncementsTab()
            1 -> StudentAttendanceTab()
            2 -> StudentRecordTab()
            else -> AnnouncementsTab()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}