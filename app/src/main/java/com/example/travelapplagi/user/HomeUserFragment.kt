package com.example.travelapplagi.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapplagi.adapter.TravelAdapter
import com.example.travelapplagi.databinding.FragmentHomeUserBinding
import com.example.travelapplagi.model.Travel
import com.google.firebase.firestore.FirebaseFirestore

class HomeUserFragment : Fragment() {
    private val binding by lazy {
        FragmentHomeUserBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val travelsCollectionRef = firestore.collection("travels")
    val travelsLiveData: MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        observeTravels()
        getAllTravels()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllTravels()
    }

    private fun navigateToOrderActivity(travel: Travel) {
        val action = HomeUserFragmentDirections.actionHomeFragmentToOrderActivity(
             idTravel = travel.id
        )
        findNavController().navigate(action)
    }

    private fun observeTravels() {
        travelsLiveData.observe(viewLifecycleOwner) {
            travels->
            val listAdapter = TravelAdapter(travels) { travel ->
                navigateToOrderActivity(travel)
            }
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
        travelsCollectionRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("User HomeFragment", "Error listening: $error")
            }
            val travels = value?.toObjects(Travel::class.java)
            if (travels != null) {
                travelsLiveData.postValue(travels)
            }
        }
    }

}