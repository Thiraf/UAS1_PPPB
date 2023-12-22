package com.example.travelapplagi.admin

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.navArgs
import com.example.travelapplagi.databinding.ActivityManageTravelBinding
import com.example.travelapplagi.model.PendingAction
import com.example.travelapplagi.model.Travel
import com.example.travelapplagi.receiver.ConnectivityReceiver
import com.google.firebase.firestore.FirebaseFirestore

class ManageTravelActivity : AppCompatActivity(), ConnectivityReceiver.NetworkCallback {
    private val binding by lazy {
        ActivityManageTravelBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val travelsColRef = firestore.collection("travels")
    private lateinit var travel: Travel
    private val connectivityReceiver = ConnectivityReceiver(this)
    private var hasInternet: Boolean = false
    private var lastNetworkAvailableTime = 0L
    private val debounceInterval = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        val args: ManageTravelActivityArgs by navArgs()

        if (args.travelId != null) {
            setupEditMode(args.travelId!!)
        }
        else {
            setupCreateMode()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    private fun setupCreateMode() {
        with(binding) {
            btnDelete.visibility = View.GONE
            btnSave.setOnClickListener {
                val title = editTitle.text.ifEmpty { 0 }.toString()
                val asal = editAsal.text.ifEmpty { 0 }.toString()
                val tujuan = editTujuan.text.ifEmpty {  }.toString()
                val ekonomi = editHargaEkonomi.text.ifEmpty { 0 }.toString().toInt()
                val bisnis = editHargaBisnis.text.ifEmpty { 0 }.toString().toInt()
                val eksekutif = editHargaEksekutif.text.ifEmpty { 0 }.toString().toInt()

                val newTravel = Travel("", title, asal, tujuan, ekonomi, bisnis, eksekutif)

                if (hasInternet) {
                    travelsColRef.add(newTravel).addOnSuccessListener { docRef ->
                        newTravel.id = docRef.id
                        docRef.set(newTravel).addOnFailureListener {
                            Log.d("Admin Manage Makanan", "Error set new id on travel: $it")
                        }
                        Toast.makeText(
                            this@ManageTravelActivity,
                            "Menu makanan baru berhasil ditambahkan!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }.addOnFailureListener {
                        Log.d("Admin ManageTravel", "Error creating new travel: $it")
                    }
                } else {
                    addToPendingAction("create", newTravel)
                    Toast.makeText(
                        this@ManageTravelActivity,
                        "Tambah makanan tertunda :(",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun setupEditMode(travelId: String) {
        travelsColRef.document(travelId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot != null) {
                travel = snapshot.toObject(Travel::class.java)!!
                with(binding) {
                    btnDelete.visibility = View.VISIBLE
                    editTitle.setText(travel.title)
                    editAsal.setText(travel.asal)
                    editTujuan.setText(travel.tujuan)
                    editHargaEkonomi.setText(travel.hargaEkonomi.toString())
                    editHargaBisnis.setText(travel.hargaBisnis.toString())
                    editHargaEksekutif.setText(travel.hargaEksekutif.toString())
                }
            }
        }.addOnFailureListener {
            Log.d("Admin ManageTravel", "Error get current travel data: $it")
        }

        with(binding) {
            btnSave.setOnClickListener {
                val title = editTitle.text.toString()
                val asal = editAsal.text.toString()
                val tujuan = editTujuan.text.toString()
                val ekonomi = editHargaEkonomi.text.ifEmpty { 0 }.toString().toInt()
                val bisnis = editHargaBisnis.text.ifEmpty { 0 }.toString().toInt()
                val eksekutif = editHargaEksekutif.text.ifEmpty { 0 }.toString().toInt()

                val updated = Travel(travel.id, title, asal, tujuan, ekonomi, bisnis, eksekutif)

                if (hasInternet) {
                    travelsColRef.document(travel.id).set(updated).addOnSuccessListener {
                        Toast.makeText(this@ManageTravelActivity, "Informasi makanan berhasil diubah!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Log.d("Admin Manage Makanan", "Error updating makanan: $it")
                    }
                }
                else {
                    addToPendingAction("update", updated)
                    Toast.makeText(this@ManageTravelActivity, "Update makanan tertunda :(", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            btnDelete.setOnClickListener {
                if (hasInternet) {
                    travelsColRef.document(travel.id).delete().addOnSuccessListener {
                        Toast.makeText(this@ManageTravelActivity, "Makanan berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Log.d("Admin Manage Makanan", "Error deleting makanan: $it")
                    }
                }
                else {
                    addToPendingAction("delete", travel)
                    Toast.makeText(this@ManageTravelActivity, "Hapus makanan tertunda :(", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun addToPendingAction(type: String, travelData: Travel) {
        val pendingAction = PendingAction(type = type.lowercase(), travel_id = travelData.id, title = travelData.title,
                                            asal = travelData.asal, tujuan = travelData.tujuan,
                                            hargaEkonomi = travelData.hargaEkonomi, hargaBisnis = travelData.hargaBisnis,
                                            hargaEksekutif = travelData.hargaEksekutif)
        DashboardAdminActivity.addPendingAct(pendingAction)
    }

    override fun onNetworkAvailable() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNetworkAvailableTime > debounceInterval) {
            lastNetworkAvailableTime = currentTime

            if (!hasInternet) {
                hasInternet = true
            }
        }
    }

    override fun onNetworkLost() {
        if (hasInternet) {
            hasInternet = false
        }
    }
}