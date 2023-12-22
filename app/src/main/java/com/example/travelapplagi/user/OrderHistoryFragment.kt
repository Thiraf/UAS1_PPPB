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
import com.example.travelapplagi.adapter.OrderedTravelAdapter
import com.example.travelapplagi.databinding.FragmentOrderHistoryBinding
import com.example.travelapplagi.dialog.OrderHistoryPopupDialog
import com.example.travelapplagi.model.Order
import com.google.firebase.firestore.FirebaseFirestore


class OrderHistoryFragment : Fragment() {
    private val binding by lazy {
        FragmentOrderHistoryBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollectionRef = firestore.collection("orders")
    val ordersLiveData: MutableLiveData<List<Order>> by lazy {
        MutableLiveData<List<Order>>()
    }
    private val loginEmail = DashboardUserActivity.loginEmail
    private var totalNominal: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersLiveData.observe(viewLifecycleOwner) { orders->
            binding.rvContent.apply {
                adapter = OrderedTravelAdapter(orders) { selectedOrder ->
                    showPopupDialog(selectedOrder)
                }
                layoutManager = LinearLayoutManager(requireContext())
            }
            if (orders.isEmpty()) {
                binding.txtMsg.visibility = View.VISIBLE
            }
            else {
                binding.txtMsg.visibility = View.GONE
            }

        }
        observeOrderChanges()
        binding.btnAddPlan.setOnClickListener {
            val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun showPopupDialog(selectedOrder: Order) {
        val popupDialog = OrderHistoryPopupDialog(selectedOrder, this)
        popupDialog.show(childFragmentManager, "Popup Dialog")
    }

    public fun deleteOrder(order: Order) {
        if (order.id.isEmpty()) {
            Log.d("User Order", "Error no order id")
        }
        ordersCollectionRef.document(order.id).delete().addOnFailureListener {
            Log.d("User Order", "Error deleting order: $it")
        }
    }

    private fun observeOrderChanges() {
        ordersCollectionRef.whereEqualTo("user_email", loginEmail).addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("User HistoryFragment", "Error listening: $error")
            }
            val orders = value?.toObjects(Order::class.java)
            if (orders != null) {
                totalNominal = calculateTotalNominal(orders)
                ordersLiveData.postValue(orders)
                DashboardUserActivity.kaloriViewModel.setTotalNominal(totalNominal)

//                // Dapatkan referensi ke CenterFragment dan perbarui total_nominal
//                val centerFragment = parentFragmentManager.findFragmentByTag("centerFragment") as CenterFragment?
//                centerFragment?.setTotalNominal(totalNominal)
            }
        }
    }

    private fun calculateTotalNominal(orders: List<Order>): Double {
        var total = 0.0
        for (order in orders) {
            // Sesuaikan dengan field nominal pada kelas Order
            total += order.totalPrice
        }
        return total

    }

}