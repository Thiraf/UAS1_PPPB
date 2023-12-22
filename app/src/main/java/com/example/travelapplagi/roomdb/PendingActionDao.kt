package com.example.travelapplagi.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.travelapplagi.model.PendingAction

@Dao
interface PendingActionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pendingAction: PendingAction)
    @Update
    fun update(pendingAction: PendingAction)
    @Delete
    fun delete(pendingAction: PendingAction)

    @get:Query("SELECT * FROM pending_actions ORDER BY id ASC")
    val allPendingActions: LiveData<List<PendingAction>>
}