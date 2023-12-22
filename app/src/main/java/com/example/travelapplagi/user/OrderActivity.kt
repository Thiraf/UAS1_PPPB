package com.example.travelapplagi.user

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.view.forEach
import androidx.lifecycle.MutableLiveData
import androidx.navigation.navArgs
import com.example.travelapplagi.R
import com.example.travelapplagi.dialog.DatePicker
import com.example.travelapplagi.databinding.ActivityOrderBinding
import com.example.travelapplagi.model.Order
import com.example.travelapplagi.model.Travel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class OrderActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private val binding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val travelsColRef = firestore.collection("travels")
    private lateinit var travel: Travel

    private val ordersCollectionRef = firestore.collection("orders")
    val ordersLiveData: MutableLiveData<List<Order>> by lazy {
        MutableLiveData<List<Order>>()
    }

    private val listClass = mutableMapOf(
        "Pagi" to 0,
        "Siang" to 0,
        "Malam" to 0,
    )
    private val listPaket = mapOf(
        "Nasi" to 112,
        "Kentang" to 87,
        "Buah" to 66,
        "Telur" to 155
    )

    private var hargaPrimer = 0
    private var hargaPaket = 0
    private var stringTglPerjalanan = ""
    private var selectedPaket = mutableListOf<String>()
    private var total_kalori = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val loginEmail = sharedPrefs.getString("email", "")!!

        val args: OrderActivityArgs by navArgs()

        travelsColRef.document(args.idTravel).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot != null) {
                travel = snapshot.toObject(Travel::class.java)!!
                hargaPrimer = travel.hargaEkonomi
                listClass.apply {
                    replace("Pagi", travel.hargaEkonomi)
                    replace("Siang", travel.hargaBisnis)
                    replace("Malam", travel.hargaEksekutif)
                }
                with(binding) {
                    txtTitle.text = travel.title
//                    txtAsalValue.text = travel.asal
//                    txtTujuanValue.text = travel.tujuan
                }
                updateHargaView()

                if (DashboardUserActivity.isFavourite(args.idTravel)) {
                    val fav = DashboardUserActivity.getFavbyTravel(args.idTravel)
                    fav.apply {
                        title = travel.title
                        asal = travel.asal
                        tujuan = travel.tujuan
                        hargaEkonomi = travel.hargaEkonomi
                        hargaBisnis = travel.hargaBisnis
                        hargaEksekutif = travel.hargaEksekutif
                    }
                    DashboardUserActivity.updateFavourite(fav)
                }
            }
            else {
                if (DashboardUserActivity.isFavourite(args.idTravel)) {
                    DashboardUserActivity.deleteFavByIdTravel(args.idTravel)
                }
                finish()
            }
        }

        with(binding) {
            spinnerClass.adapter = ArrayAdapter<String>(this@OrderActivity,
                                    android.R.layout.simple_spinner_item, listClass.keys.toList())
            spinnerClass.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    hargaPrimer = listClass[spinnerClass.selectedItem.toString()]!!
                    updateHargaView()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            btnShowCalendar.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(supportFragmentManager, "tgl_perjalanan")
            }

            toggleGroupPaket.forEach { view ->
                (view as CheckBox).setOnCheckedChangeListener { button, isChecked ->
                    val paket = button.text.toString()
                    if (isChecked) {
                        selectedPaket.add(paket)
                    }
                    else {
                        selectedPaket.remove(paket)
                    }
                    hargaPaket = selectedPaket.sumOf { listPaket[it] ?: 0 }
                    updateHargaView()
                }
            }

            btnOrder.setOnClickListener {

                if (stringTglPerjalanan == "") {
                    Toast.makeText(this@OrderActivity, "Mohon pilih tanggal makan!", Toast.LENGTH_SHORT).show()
                }
                else {
                    val newOrder = Order(
                        title = travel.title, desc = "${travel.asal} - ${travel.tujuan}", user_email = loginEmail!!,
                                        id_travel = args.idTravel, selectedPaket = selectedPaket, totalKalori = MutableLiveData(total_kalori),
                                        date = stringTglPerjalanan, totalPrice = getHargaTotal())
                    addOrder(newOrder)
                    finish()
                }
            }
        }
    }

    private fun getHargaTotal(): Int {
        return hargaPrimer + hargaPaket
    }
    private fun updateHargaView() {
        binding.txtHarga.text = "${getHargaTotal()}"
    }

    

    override fun onDateSet(
        view: android.widget.DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH)
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        var valid = false

        if (year > yearNow) {
            valid = true
        }
        else {
            if (month > monthNow) {
                valid = true
            }
            else {
                if (dayOfMonth >= dayNow) {
                    valid = true
                }
            }
        }
        if (valid) {
            val date = "$dayOfMonth ${DatePicker.monthToString(month)} $year"
            binding.btnShowCalendar.text = date
            stringTglPerjalanan = date
        }
    }

    private fun addOrder(order: Order) {
        ordersCollectionRef.add(order).addOnSuccessListener { ref ->
            val createdId = ref.id
            order.id = createdId
            ref.set(order).addOnFailureListener {
                Log.d("User Order", "Error while updating new id: $it")
            }
            showSuccessNotification(order)
        }.addOnFailureListener {
            Log.d("User Order", "Error add new order: $it")
        }
    }

    private fun showSuccessNotification(order: Order) {
        val channelId = "ORDER_SUCCESS"
        val notifId = 1
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val flag = PendingIntent.FLAG_IMMUTABLE
        val intent = Intent(this, DashboardUserActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Pembelian Berhasil!")
            .setContentText("Makanan ${order.title} telah dicatat pada tanggal (${order.date})")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(channelId, "Notifku", NotificationManager.IMPORTANCE_DEFAULT)
            with(notificationManager) {
                createNotificationChannel(notifChannel)
                notify(notifId, builder.build())
            }
        }
        else {
            notificationManager.notify(notifId, builder.build())
        }
    }

}