package com.example.travelapplagi.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class ConnectivityReceiver(private val callback: NetworkCallback): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (isNetworkAvailable(context)) {
                Log.d("ConnectivityReceiver", "Network is available")
                callback.onNetworkAvailable()
            }
            else {
                Log.w("ConnectivityReceiver", "Network is unavailable")
                callback.onNetworkLost()
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    interface NetworkCallback {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }
}