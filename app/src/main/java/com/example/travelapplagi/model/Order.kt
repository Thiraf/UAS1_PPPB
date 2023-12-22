package com.example.travelapplagi.model
import androidx.lifecycle.MutableLiveData


data class Order(
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var user_email: String = "",
    var id_travel: String = "",
    var selectedPaket: List<String> = listOf(),
    var date: String = "",
    var totalPrice: Int = 0,  // Ganti totalKalori dengan totalPrice
    val totalKalori: MutableLiveData<Int>? = MutableLiveData()  // Tambah MutableLiveData<Int>?
)
