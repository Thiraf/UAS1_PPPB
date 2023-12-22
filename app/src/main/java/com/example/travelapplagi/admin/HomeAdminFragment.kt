package com.example.travelapplagi.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapplagi.R
import com.example.travelapplagi.adapter.EditTravelAdapter
import com.example.travelapplagi.adapter.TravelAdapter
import com.example.travelapplagi.databinding.FragmentHomeAdminBinding
import com.example.travelapplagi.model.Travel
import com.example.travelapplagi.user.HomeUserFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeAdminFragment : Fragment() {
    private val binding by lazy {
        FragmentHomeAdminBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val travelsCollectionRef = firestore.collection("travels")
    val travelsLiveData: MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }
    private var listenerReg: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(binding) {
            btnAdd.setOnClickListener {
                val action = HomeAdminFragmentDirections.actionHomeAdminFragmentToManageTravelActivity()
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        observeTravels()
        getAllTravels()
    }

    private fun observeTravels() {
        travelsLiveData.observe(viewLifecycleOwner) {
                travels->
            val listAdapter = EditTravelAdapter(travels)
            binding.rvContent.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun getAllTravels() {
        observeTravelChanges()
    }

    private fun observeTravelChanges() {
        if (listenerReg == null) {
            travelsCollectionRef.addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("Admin HomeFragment", "Error listening: $error")
                }
                val travel = value?.toObjects(Travel::class.java)
                if (travel != null) {
                    travelsLiveData.postValue(travel)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerReg?.remove()
    }

}