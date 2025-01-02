package com.rach.firmmanagement.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rach.firmmanagement.viewModel.HolidayViewModel

class HolidayViewModelFactory(private val repository: HolidayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HolidayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
