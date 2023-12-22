package com.example.travelapplagi.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePicker: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireActivity(),
            activity as DatePickerDialog.OnDateSetListener,
            year,
            month,
            day
        )
    }
    companion object {
        public fun monthToString(month: Int): String {
            return when(month) {
                0 -> "Januari"
                1 -> "Februari"
                2 -> "Maret"
                3 -> "April"
                4 -> "Mei"
                5 -> "Juni"
                6 -> "Juli"
                7 -> "Agustus"
                8 -> "September"
                9 -> "Oktober"
                10 -> "November"
                11 -> "Desember"
                else -> "Tidak valid"
            }
        }
    }
}