package com.example.travelapplagi.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelapplagi.main.MainActivity
import com.example.travelapplagi.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        if (sharedPrefs.contains("username") && sharedPrefs.contains("email") && sharedPrefs.contains("role")) {
            with(binding) {
                txtUsername.text = sharedPrefs.getString("username", "")
                txtEmail.text = sharedPrefs.getString("email", "")
                txtRole.text = sharedPrefs.getString("role", "")
            }
        }
        with(binding) {
            btnLogout.setOnClickListener {
                editor.remove("username")
                editor.remove("email")

                editor.remove("role")
                editor.apply()
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        return binding.root
    }

}