package com.example.travelapplagi.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapplagi.R
import com.example.travelapplagi.adapter.TabAdapter
import com.example.travelapplagi.admin.DashboardAdminActivity
import com.example.travelapplagi.databinding.ActivityMainBinding
import com.example.travelapplagi.user.CenterFragment
import com.example.travelapplagi.user.DashboardUserActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewPager2 = binding.viewPager

        val sharedPrefs = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        if (sharedPrefs.contains("email")) {
            val role = sharedPrefs.getString("role", "user")
            if (role == "user") {
                val intent = Intent(this, DashboardUserActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, DashboardAdminActivity::class.java)
                startActivity(intent)
            }
        }

        with(binding) {
            viewPager2.adapter = TabAdapter(this@MainActivity)
            TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
                tab.text = when (pos) {
                    0 -> "Login"
                    1 -> "Register"
                    else -> ""
                }
            }.attach()
        }
    }
}