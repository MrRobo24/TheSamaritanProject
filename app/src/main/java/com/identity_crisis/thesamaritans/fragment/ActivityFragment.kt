package com.identity_crisis.thesamaritans.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.adapter.ActivityAdapter
import kotlinx.android.synthetic.main.fragment_activity.*

class ActivityFragment : Fragment() {

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        viewPager = view.findViewById(R.id.viewpager)
        setUpViewPager(viewPager)

        viewPager.currentItem = 0
        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = ActivityAdapter(childFragmentManager)
        adapter.addFragment(VolunteerFragment(), "Volunteer")
        adapter.addFragment(SeekHelpFragment(), "Seek Help")
        viewPager.adapter = adapter
    }
}