package com.example.travelapplagi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_actions")
data class PendingAction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "type")
    val type: String = "",
    @ColumnInfo(name = "is_processed")
    var isProcessed: Boolean = false,
    @ColumnInfo(name = "travel_id")
    val travel_id: String = "",
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "asal")
    val asal: String = "",
    @ColumnInfo(name = "tujuan")
    val tujuan: String = "",
    @ColumnInfo(name = "harga_ekonomi")
    val hargaEkonomi: Int = 0,
    @ColumnInfo(name = "harga_bisnis")
    val hargaBisnis: Int = 0,
    @ColumnInfo(name = "harga_eksekutif")
    val hargaEksekutif: Int = 0,
)
