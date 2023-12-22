package com.example.travelapplagi.admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapplagi.R
import com.example.travelapplagi.adapter.EditTravelAdapter
import com.example.travelapplagi.adapter.ManageUserAdapter
import com.example.travelapplagi.databinding.FragmentManageUserBinding
import com.example.travelapplagi.model.Travel
import com.example.travelapplagi.model.User
import com.google.firebase.firestore.FirebaseFirestore

class ManageUserFragment : Fragment() {
    private val binding by lazy {
        FragmentManageUserBinding.inflate(layoutInflater)
    }
    val firestore = FirebaseFirestore.getInstance()
    val usersColRef = firestore.collection("users")
    val usersLiveData: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        username = sharedPrefs.getString("username", "")!!

        observeUsers()
        getAllUsers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllUsers()
    }

    private fun observeUsers() {
        usersLiveData.observe(viewLifecycleOwner) { users->
            val listAdapter = ManageUserAdapter(users)
            binding.rvContent.apply {
                adapter = listAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
    }

    private fun getAllUsers() {
        observeUserChanges()
    }

    private fun observeUserChanges() {
        usersColRef.whereNotEqualTo("username", username).addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("Admin HomeFragment", "Error listening: $error")
            }
            val user = value?.toObjects(User::class.java)
            if (user != null) {
                usersLiveData.postValue(user)
            }
        }
    }

    public fun setUserChange(user: User) {
        usersColRef.document(user.email).set(user).addOnSuccessListener {
            Toast.makeText(requireContext(), "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.d("Admin ManageUser", "Failed to update role: $it")
        }
    }
    public fun deleteUser(user: User) {
        usersColRef.document(user.email).delete().addOnSuccessListener {
            Toast.makeText(requireContext(), "Berhasil dihapus!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.d("Admin ManageUser", "Failed to delete user: $it")
        }
    }


}