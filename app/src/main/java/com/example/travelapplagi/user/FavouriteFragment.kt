package com.example.travelapplagi.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapplagi.adapter.TravelAdapter
import com.example.travelapplagi.databinding.FragmentFavouriteBinding
import com.example.travelapplagi.model.Travel

class FavouriteFragment : Fragment() {
    private val binding by lazy {
        FragmentFavouriteBinding.inflate(layoutInflater)
    }
    private val travelsLiveData: MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }
    private val loginEmail = DashboardUserActivity.loginEmail

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getAllFavourites()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllFavourites()
    }

    private fun navigateToOrderActivity(travel: Travel) {
        val action = FavouriteFragmentDirections.actionFavouriteFragmentToOrderActivity(travel.id)
        findNavController().navigate(action)
    }

    private fun getAllFavourites() {
        val listTravel = mutableListOf<Travel>()
        DashboardUserActivity.mFavouriteDao.getUserFavourites(loginEmail).observe(viewLifecycleOwner) { favs ->
            listTravel.clear()
            favs.forEach { fav ->
                val travel = Travel(fav.id_travel, fav.title, fav.asal, fav.tujuan, fav.hargaEkonomi)
                listTravel.add(travel)
            }
            travelsLiveData.postValue(listTravel)
        }
        travelsLiveData.observe(viewLifecycleOwner) { travels ->
            with(binding) {
                rvContent.apply {
                    adapter = TravelAdapter(travels) { travel ->
                        navigateToOrderActivity(travel)
                    }
                    layoutManager = LinearLayoutManager(requireContext())
                }
                if (travels.isEmpty()) {
                    txtMsg.visibility = View.VISIBLE
                }
                else {
                    txtMsg.visibility = View.GONE
                }
            }
        }
    }

}