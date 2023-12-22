package com.example.travelapplagi.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapplagi.main.LoginFragment
import com.example.travelapplagi.main.RegisterFragment

class TabAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    private val pages = arrayOf<Fragment>(LoginFragment(), RegisterFragment())

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}