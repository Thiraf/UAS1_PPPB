package com.example.travelapplagi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapplagi.databinding.PendingActionCardBinding
import com.example.travelapplagi.model.PendingAction

class PendingActionAdapter(private val listAction: List<PendingAction>)
    : RecyclerView.Adapter<PendingActionAdapter.ItemPendingActionViewHolder>() {
    inner class ItemPendingActionViewHolder(private val binding: PendingActionCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(action: PendingAction) {
            with(binding) {
                txtTitle.text = action.title
                txtDesc.text = "${action.asal} - ${action.tujuan}, ${action.hargaEkonomi} - ${action.hargaEksekutif}"
                txtType.text = action.type.uppercase()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPendingActionViewHolder {
        val binding = PendingActionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemPendingActionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listAction.size
    }

    override fun onBindViewHolder(holder: ItemPendingActionViewHolder, position: Int) {
        holder.bind(listAction[position])
    }
}