package com.example.travelapplagi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapplagi.admin.HomeAdminFragmentDirections
import com.example.travelapplagi.databinding.TravelEditCardBinding
import com.example.travelapplagi.model.Travel

class EditTravelAdapter(private val listTravel: List<Travel>)
    : RecyclerView.Adapter<EditTravelAdapter.ItemEditTravelViewHolder>() {
    inner  class ItemEditTravelViewHolder(private val binding: TravelEditCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(travel: Travel) {
            with(binding) {
                txtTitle.text = travel.title
//                txtAsal.text = ": ${travel.asal}"
//                txtTujuan.text = ": ${travel.tujuan}"
                txtHarga.text = ": ${travel.hargaEkonomi}"
                btnEdit.setOnClickListener {
                    val action = HomeAdminFragmentDirections.actionHomeAdminFragmentToManageTravelActivity(travel.id)
                    itemView.findNavController().navigate(action)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemEditTravelViewHolder {
        val binding = TravelEditCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemEditTravelViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTravel.size
    }

    override fun onBindViewHolder(holder: ItemEditTravelViewHolder, position: Int) {
        holder.bind(listTravel[position])
    }
}