//package com.example.haru.view.timetable
//
//import android.widget.DatePicker
//import android.widget.Toast
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.lifecycle.ViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//
//class CalendarViewModel: ViewModel() {
//
//    fun showDatePickerDialog(): {
//        val calendar = Calendar.getInstance()
//        val datePicker = DatePicker(requireContext())
//        datePicker.init(
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH),
//            null
//        )
//
//        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
//            .setView(datePicker)
//            .setPositiveButton("apply") {dialog, _ ->
//                val year = datePicker.year
//                val month = datePicker.month + 1
//                val day = datePicker.dayOfMonth
//                val day_of_week = calendar.get(Calendar.DAY_OF_WEEK)
//                val selectedYear = SimpleDateFormat("yyyy년").format(Date(year - 1900, month - 1, day))
//                val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month - 1, day))
//                Toast.makeText(requireContext(), "${day_of_week}", Toast.LENGTH_SHORT).show()
//                dialog.dismiss()
//            }
//            .setNegativeButton("cancel") {dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//        dialog.show()
//    }
//}