package com.example.travelapplagi.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelapplagi.admin.DashboardAdminActivity
import com.example.travelapplagi.databinding.FragmentLoginBinding
import com.example.travelapplagi.user.DashboardUserActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private val binding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val usersRefColl = firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = activity as MainActivity
        val sharedPrefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        with(binding) {
            txtDontHaveAccount.setOnClickListener {
                main.viewPager2.setCurrentItem(1)
            }
            btnLogin.setOnClickListener {
                if (allFieldsFilled()) {
                    val username = editUsername.text.toString()
                    val password = editPassword.text.toString()

                    usersRefColl.whereEqualTo("username", username).get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val docSnapshot = querySnapshot.documents.get(0)
                            val storedPassword = docSnapshot.getString("password")
                            if (password.equals(storedPassword)) {
                                val email = docSnapshot.id
                                val usn = docSnapshot.getString("username")
                                val role = docSnapshot.getString("role")
                                editor.putString("email", email)
                                editor.putString("username", usn)
                                editor.putString("role", role)
                                editor.apply()

                                if (role == "user") {
                                    val intent = Intent(requireContext(), DashboardUserActivity::class.java)
                                    startActivity(intent)
                                }
                                else if (role == "admin") {
                                    val intent = Intent(requireContext(), DashboardAdminActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            else {
                                Toast.makeText(requireContext(), "Username atau password salah!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Username atau password salah!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(requireContext(), "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }
    private fun allFieldsFilled(): Boolean {
        return binding.editUsername.text.isNotEmpty() && binding.editPassword.text.isNotEmpty()
    }
}