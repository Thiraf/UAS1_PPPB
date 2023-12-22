package com.example.travelapplagi.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KaloriViewModel : ViewModel() {
    private val _totalNominal = MutableLiveData<Double>()
    val totalNominal: LiveData<Double>
        get() = _totalNominal

    fun setTotalNominal(value: Double) {
        _totalNominal.value = value
    }
}