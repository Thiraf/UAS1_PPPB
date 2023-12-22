package com.example.travelapplagi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "id_travel")
    val id_travel: String = "",
    @ColumnInfo(name = "user_email")
    val user_email: String = "",
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "asal")
    var asal: String = "",
    @ColumnInfo(name = "tujuan")
    var tujuan: String = "",
    @ColumnInfo(name = "harga_ekonomi")
    var hargaEkonomi: Int = 0,
    @ColumnInfo(name = "harga_bisnis")
    var hargaBisnis: Int = 0,
    @ColumnInfo(name = "harga_eksekutif")
    var hargaEksekutif: Int = 0,
)
