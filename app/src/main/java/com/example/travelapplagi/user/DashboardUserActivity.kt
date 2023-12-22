package com.example.travelapplagi.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.travelapplagi.main.MainActivity
import com.example.travelapplagi.R
import com.example.travelapplagi.databinding.ActivityDashboardUserBinding
import com.example.travelapplagi.model.Favourite
import com.example.travelapplagi.model.KaloriViewModel
import com.example.travelapplagi.roomdb.FavouriteDao
import com.example.travelapplagi.roomdb.AppRoomDB
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DashboardUserActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDashboardUserBinding.inflate(layoutInflater)
    }
    companion object {
        lateinit var mFavouriteDao: FavouriteDao
        lateinit var executorService: ExecutorService
        lateinit var loginEmail: String
        private lateinit var _kaloriViewModel: KaloriViewModel

        val kaloriViewModel: KaloriViewModel
            get() = _kaloriViewModel

        fun addFavourite(favourite: Favourite) {
            executorService.execute {
                mFavouriteDao.insert(favourite)
            }
        }
        fun isFavourite(idTravel: String): Boolean {
            return executorService.submit<Boolean> {
                mFavouriteDao.getUserFavListByTravel(idTravel, loginEmail).isNotEmpty()
            }.get()
        }
        fun deleteFavByIdTravel(idTravel: String) {
            executorService.execute {
                mFavouriteDao.deleteUserFavByTravel(idTravel, loginEmail)
            }
        }
        fun getFavbyTravel(idTravel: String): Favourite {
            return executorService.submit<Favourite> {
            mFavouriteDao.getUserFavByTravel(idTravel, loginEmail)
            }.get()
        }
        fun updateFavourite(favourite: Favourite) {
            executorService.execute {
                mFavouriteDao.update(favourite)
            }
        }
    }

    private lateinit var kaloriViewModel: KaloriViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        _kaloriViewModel = ViewModelProvider(this)[KaloriViewModel::class.java]

        val sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        if (!sharedPrefs.contains("email")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        loginEmail = sharedPrefs.getString("email", "")!!

        executorService = Executors.newSingleThreadExecutor()
        val db = AppRoomDB.getDatabase(this)
        mFavouriteDao = db!!.favouriteDao()!!

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavView.setupWithNavController(navController)
    }
}