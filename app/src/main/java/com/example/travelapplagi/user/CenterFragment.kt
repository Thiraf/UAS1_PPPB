package com.example.travelapplagi.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.travelapplagi.R
import com.example.travelapplagi.model.KaloriViewModel
import com.example.travelapplagi.user.DashboardUserActivity.Companion.kaloriViewModel


class CenterFragment : Fragment() {
    private lateinit var totalNominalTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_center, container, false)
        totalNominalTextView = view.findViewById(R.id.total_nominal)

        // Check if kaloriViewModel is not null before accessing it
        if (kaloriViewModel != null) {
            kaloriViewModel.totalNominal.observe(viewLifecycleOwner) { totalNominal ->
                totalNominalTextView.text = totalNominal.toString()
            }
        }

        return view
    }

    // ...
}
