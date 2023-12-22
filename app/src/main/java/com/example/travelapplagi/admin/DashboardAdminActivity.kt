package com.example.travelapplagi.admin

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.travelapplagi.R
import com.example.travelapplagi.databinding.ActivityDashboardAdminBinding
import com.example.travelapplagi.model.PendingAction
import com.example.travelapplagi.model.Travel
import com.example.travelapplagi.receiver.ConnectivityReceiver
import com.example.travelapplagi.roomdb.AppRoomDB
import com.example.travelapplagi.roomdb.PendingActionDao
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors

class DashboardAdminActivity : AppCompatActivity(), ConnectivityReceiver.NetworkCallback {
    private val binding by lazy {
        ActivityDashboardAdminBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val travelsColRef = firestore.collection("travels")
    private val connectivityReceiver = ConnectivityReceiver(this)
    private var hasInternet: Boolean = false
    private var lastNetworkAvailableTime = 0L
    private val debounceInterval = 1000 // Set a suitable interval in milliseconds


    companion object {
        private val executorService = Executors.newSingleThreadExecutor()
        lateinit var mPendingActionDao: PendingActionDao

        fun addPendingAct(action: PendingAction) {
            executorService.execute {
                mPendingActionDao.insert(action)
            }
        }
        fun deletePendingAction(action: PendingAction) {
            executorService.execute {
                mPendingActionDao.delete(action)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val db = AppRoomDB.getDatabase(this)
        mPendingActionDao = db!!.pendingActionDao()

        with(binding) {
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavView.setupWithNavController(navController)
        }
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    private fun processPendingAction(pendingAction: PendingAction) {
        val travel = Travel(
            "",
            pendingAction.title,
            pendingAction.asal,
            pendingAction.tujuan,
            pendingAction.hargaEkonomi,
            pendingAction.hargaBisnis,
            pendingAction.hargaEksekutif
        )

        if (!pendingAction.isProcessed && hasInternet) {
            pendingAction.isProcessed = true
            deletePendingAction(pendingAction)
            when (pendingAction.type) {
                "create" -> {
//                    Log.d("ConnectivityReceiver Abal2", "proses createeee")
                    processCreateAction(travel, pendingAction)
                }
                "update" -> {
                    processUpdateAction(travel, pendingAction)
                }
                "delete" -> {
                    processDeleteAction(travel, pendingAction)
                }
            }
        }
    }

    private fun processCreateAction(travel: Travel, pendingAction: PendingAction) {
        travelsColRef.add(travel).addOnSuccessListener { docRef ->
            travel.id = docRef.id
            docRef.set(travel).addOnSuccessListener {
                deletePendingAction(pendingAction)
            }.addOnFailureListener {
                handleActionFailure(pendingAction, "Fail to assign new travel id: $it")
            }
        }.addOnFailureListener {
            handleActionFailure(pendingAction, "Fail to store new travel to firestore: $it")
        }
    }

    private fun processUpdateAction(travel: Travel, pendingAction: PendingAction) {
        travel.id = pendingAction.travel_id
        travelsColRef.document(travel.id).set(travel).addOnSuccessListener {
            deletePendingAction(pendingAction)
        }.addOnFailureListener {
            handleActionFailure(pendingAction, "Fail to update travel to firestore: $it")
        }
    }

    private fun processDeleteAction(travel: Travel, pendingAction: PendingAction) {
        travel.id = pendingAction.travel_id
        travelsColRef.document(travel.id).delete().addOnSuccessListener {
            deletePendingAction(pendingAction)
        }.addOnFailureListener {
            handleActionFailure(pendingAction, "Fail to delete: $it")
        }
    }

    private fun handleActionFailure(pendingAction: PendingAction, errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.d("Admin PendingAction", errorMessage)
    }

    private fun getAllPendingActions() {
        mPendingActionDao.allPendingActions.observe(this) { actions ->
            if (actions.isNotEmpty()) {
                actions.forEach { pendingAction ->
                    processPendingAction(pendingAction)
                }
            }
        }
    }

    override fun onNetworkAvailable() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNetworkAvailableTime > debounceInterval) {
            lastNetworkAvailableTime = currentTime
            if (!hasInternet) {
                hasInternet = true
                getAllPendingActions()
            }
        }
    }

    override fun onNetworkLost() {
        if (hasInternet) {
            hasInternet = false
        }
    }
}