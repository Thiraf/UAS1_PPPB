package com.example.travelapplagi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapplagi.databinding.TravelCardBinding
import com.example.travelapplagi.model.Favourite
import com.example.travelapplagi.model.Travel
import com.example.travelapplagi.user.DashboardUserActivity

class TravelAdapter(private val listTravel: List<Travel>,
                    private val onItemClickListener: (Travel) -> Unit
)
    : RecyclerView.Adapter<TravelAdapter.ItemTravelViewHolder>() {
    inner  class ItemTravelViewHolder(private val binding: TravelCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(travel: Travel) {
            with(binding) {
                txtTitle.text = travel.title
//                txtAsal.text = ": ${travel.asal}"
//                txtTujuan.text = ": ${travel.tujuan}"
                txtHarga.text = ": ${travel.hargaEkonomi}"

                btnFav.isChecked = DashboardUserActivity.isFavourite(travel.id)

                btnFav.setOnClickListener {
                    val email = DashboardUserActivity.loginEmail
                    if (btnFav.isChecked) {
                        val fav = Favourite(id_travel = travel.id, user_email = email, title = travel.title, asal = travel.asal,
                                            tujuan = travel.tujuan, hargaEkonomi = travel.hargaEkonomi,
                                            hargaBisnis = travel.hargaBisnis, hargaEksekutif = travel.hargaEksekutif)
                        DashboardUserActivity.addFavourite(fav)
                    }
                    else {
                        DashboardUserActivity.deleteFavByIdTravel(travel.id)
                    }
                }
            }
            itemView.setOnClickListener {
                onItemClickListener.invoke(travel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTravelViewHolder {
        val binding = TravelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemTravelViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTravel.size
    }

    override fun onBindViewHolder(holder: ItemTravelViewHolder, position: Int) {
        holder.bind(listTravel[position])
    }
}