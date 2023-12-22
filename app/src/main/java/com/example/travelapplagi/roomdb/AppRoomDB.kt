package com.example.travelapplagi.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.travelapplagi.model.Favourite
import com.example.travelapplagi.model.PendingAction

@Database(entities = [Favourite::class, PendingAction::class], version = 2, exportSchema = false)
abstract class AppRoomDB : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao?
    abstract fun pendingActionDao(): PendingActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDB? = null
        fun getDatabase(context: Context): AppRoomDB? {
            if (INSTANCE == null) {
                synchronized(AppRoomDB::class.java) {
                    INSTANCE = databaseBuilder(context.applicationContext, AppRoomDB::class.java, "travellagi_db").fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }
    }
}