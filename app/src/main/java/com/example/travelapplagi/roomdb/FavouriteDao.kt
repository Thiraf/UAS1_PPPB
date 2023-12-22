package com.example.travelapplagi.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.travelapplagi.model.Favourite

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favourite: Favourite)
    @Update
    fun update(favourite: Favourite)
    @Delete
    fun delete(favourite: Favourite)

    @Query("SELECT * FROM favourites WHERE user_email = :userEmail ORDER BY id ASC")
    fun getUserFavourites(userEmail: String): LiveData<List<Favourite>>
    @Query("SELECT * FROM favourites WHERE id_travel = :idTravel AND user_email = :userEmail")
    fun getUserFavListByTravel(idTravel: String, userEmail: String): List<Favourite>
    @Query("DELETE FROM favourites WHERE id_travel = :idTravel AND user_email = :userEmail")
    fun deleteUserFavByTravel(idTravel: String, userEmail: String)
    @Query("SELECT * FROM favourites WHERE id_travel = :idTravel AND user_email = :userEmail LIMIT 1")
    fun getUserFavByTravel(idTravel: String, userEmail: String): Favourite
}