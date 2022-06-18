package com.ketchupzzz.cathyattendance.techearUi.classroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentClassesBinding
import com.ketchupzzz.cathyattendance.databinding.FragmentClassroomBinding
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.techearUi.classroom.tabs.TabAdapter


class ClassroomFragment : Fragment() {
    private val args by navArgs<ClassroomFragmentArgs>()
    private lateinit var binding : FragmentClassroomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentClassroomBinding.inflate(inflater,container,false)
        binding.textSubjectName.text = args.classroom.classTitle
        subjectClass = args.classroom
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()

    }
    private fun setupTabLayout() {
        val tabAdapter = TabAdapter(childFragmentManager,lifecycle)
        binding.viewpager.adapter = tabAdapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewpager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)!!.select()
            }
        })
    }
    companion object {
        var subjectClass : SubjectClass? = null
    }

}