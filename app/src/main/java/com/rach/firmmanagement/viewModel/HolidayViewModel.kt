package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HolidayViewModel(private val repository: HolidayRepository) : ViewModel() {

    private val _festivals = MutableStateFlow<List<Festival>>(emptyList())
    val festivals: StateFlow<List<Festival>> = _festivals

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)

    fun updateFestivalSelection(festival: Festival, isSelected: Boolean) {
        val updatedFestivals = _festivals.value.map {
            if (it == festival) it.copy(selected = isSelected) else it
        }
        _festivals.value = updatedFestivals
    }

    suspend fun fetchFestivals(year: String) {
        _loading.value = true
        _error.value = null
        repository.getFestivals(
            year = year,
            onSuccess = { fetchedFestivals ->
                _festivals.value = fetchedFestivals
                _loading.value = false
                Log.d("Holiday", _festivals.value.toString())
            },
            onFailure = {
                _error.value = "Failed to fetch festivals."
                _loading.value = false
            }
        )
    }


    fun addFestival(festival: Festival) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.addFestival(
                festival = festival,
                onSuccess = {
                    _festivals.value = _festivals.value + festival
                    _loading.value = false
                },
                onFailure = {
                    _error.value = "Failed to add festival."
                    _loading.value = false
                }
            )

        }
    }

    private val _additionalHolidays = MutableStateFlow<List<String>>(emptyList())
    val additionalHolidays: StateFlow<List<String>> = _additionalHolidays
    suspend fun fetchAdditionalHolidays() {
        _loading.value = true
        _error.value = null
        repository.fetchAdditionalHolidays(
            onSuccess = { fetchedHolidays ->
                _additionalHolidays.value = fetchedHolidays
                _loading.value = false
                Log.d("Holiday", _additionalHolidays.value.toString())
            },
            onFailure = {
                _error.value = "Failed to fetch festivals."
                _loading.value = false
            }
        )
    }

    fun addAdditionalHolidays(holiday: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.addAdditionalHolidays(
                holiday=holiday,
                onSuccess = {
                    _additionalHolidays.value=_additionalHolidays.value+holiday
                    _loading.value = false
                },
                onFailure = {
                    _error.value = "Failed to add Holiday"
                    _loading.value = false
                }
            )
        }
    }
    fun saveRegularHolidaysForSelectedEmployees(selectedEmployees: List<ViewAllEmployeeDataClass>, regularHolidays: RegularHolidayItems, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                selectedEmployees.forEach { employee ->
                    repository.saveRegularHolidays(
                        regularHolidays = regularHolidays,
                        employeeNumber = employee.phoneNumber.toString(),
                        year=year.toString(),
                        onSuccess = {
                            _loading.value = false
                            onSuccess()
                        },
                        onFailure = {
                            _error.value = "Failed to save festival holidays for ${employee.name}"
                            _loading.value = false
                        }
                    )

                }
            } catch (e: Exception) {
                _error.value = "Failed to save regular holidays."
                _loading.value = false
            }
        }
    }
    fun saveFestivalHolidaysForSelectedEmployees(selectedEmployees: List<ViewAllEmployeeDataClass>, festivalHolidays: List<Festival>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                selectedEmployees.forEach { employee ->
                    repository.saveFestivals(
                        festivals = festivalHolidays,
                        employeeNumber = employee.phoneNumber.toString(),
                        onSuccess = {
                            onSuccess()
                        },
                        onFailure = {
                            _error.value = "Failed to save festival holidays for ${employee.name}"
                        }
                    )
                }
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "An error occurred while saving festival holidays."
                _loading.value = false
            }
        }
    }
}
