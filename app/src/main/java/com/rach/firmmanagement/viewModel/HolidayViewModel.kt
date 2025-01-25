package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    // salary

    private val _selectedMonth = MutableStateFlow(getCurrentMonthAndYear())
    val selectedMonth: StateFlow<String> = _selectedMonth

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
    }

    fun getCurrentMonthAndYear(): String {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private val _employeeData = MutableStateFlow<AddStaffDataClass?>(null)
    val employeeData: StateFlow<AddStaffDataClass?> get() = _employeeData

    private val _employeeLoading = MutableStateFlow(false)
    val employeeLoading: StateFlow<Boolean> get() = _employeeLoading

    private val _employeeError = MutableStateFlow<String?>(null)
    val employeeError: StateFlow<String?> get() = _employeeError

    private val _festivalsHolidays = MutableStateFlow<List<Festival>>(emptyList())
    val festivalsHolidays: StateFlow<List<Festival>> get() = _festivalsHolidays

    private val _festivalsLoading = MutableStateFlow(false)
    val festivalsLoading: StateFlow<Boolean> get() = _festivalsLoading

    private val _festivalsError = MutableStateFlow<String?>(null)
    val festivalsError: StateFlow<String?> get() = _festivalsError

    private val _regularHolidays = MutableStateFlow<RegularHolidayItems?>(null)
    val regularHolidays: StateFlow<RegularHolidayItems?> get() = _regularHolidays

    private val _regularHolidaysLoading = MutableStateFlow(false)
    val regularHolidaysLoading: StateFlow<Boolean> get() = _regularHolidaysLoading

    private val _regularHolidaysError = MutableStateFlow<String?>(null)
    val regularHolidaysError: StateFlow<String?> get() = _regularHolidaysError

    // Fetch employee data
    fun fetchEmployeeData(employeePhoneNumber: String) {
        _employeeLoading.value = true
        _employeeError.value = null
        viewModelScope.launch {
            try {
                val data = repository.fetchEmployeeData(employeePhoneNumber)
                _employeeData.value = data
            } catch (e: Exception) {
                _employeeError.value = e.message
            } finally {
                _employeeLoading.value = false
            }
        }
    }

    // Fetch festivals for a specific month and year
    fun getFestivalsForMonthAndYear(employeeNumber: String, year: String, month: String) {
        _festivalsLoading.value = true
        _festivalsError.value = null
        viewModelScope.launch {
            try {
                repository.getFestivalsForMonthAndYear(
                    employeeNumber,
                    year,
                    month,
                    onSuccess = { festivals ->
                        _festivalsHolidays.value = festivals
                    },
                    onFailure = { e ->
                        _festivalsError.value = e.message
                    }
                )
            } catch (e: Exception) {
                _festivalsError.value = e.message
            } finally {
                _festivalsLoading.value = false
            }
        }
    }

    // Fetch regular holidays for a specific year
    fun getRegularHolidaysForYear(employeeNumber: String, year: String) {
        _regularHolidaysLoading.value = true
        _regularHolidaysError.value = null
        viewModelScope.launch {
            try {
                repository.getRegularHolidaysForYear(
                    employeeNumber,
                    year,
                    onSuccess = { holidays ->
                        _regularHolidays.value = holidays
                    },
                    onFailure = { e ->
                        _regularHolidaysError.value = e.message
                    }
                )
            } catch (e: Exception) {
                _regularHolidaysError.value = e.message
            } finally {
                _regularHolidaysLoading.value = false
            }
        }
    }

    private val _monthlyWorkHours = MutableStateFlow<Map<String, Long>>(emptyMap())
    val monthlyWorkHours: StateFlow<Map<String, Long>> get() = _monthlyWorkHours

    private val _workHourLoading = MutableStateFlow(false)
    val workHourLoading: StateFlow<Boolean> get() = _workHourLoading


    fun fetchMonthlyWorkHours(employeePhoneNumber: String, year: String, month: String) {
        viewModelScope.launch {
            try {
                _workHourLoading.value = true
                val workHours = repository.getMonthlyWorkHours(employeePhoneNumber, year, month)
                _monthlyWorkHours.value = workHours
                _workHourLoading.value = false
            } catch (e: Exception) {
                _workHourLoading.value = false
                _error.value = "Failed to fetch work hours: ${e.message}"
            }
        }
    }


    private val _leaveData = MutableStateFlow<List<EmployeeLeaveData>>(emptyList())
    val leaveData: StateFlow<List<EmployeeLeaveData>> get() = _leaveData
    private val _leaveLoading = MutableStateFlow(false)
    val leaveLoading: StateFlow<Boolean> get() = _leaveLoading

    fun fetchLeavesForMonthAndYear(
        employeePhoneNumber: String,
        year: String,
        month: String
    ) {
        viewModelScope.launch {
            try {
                _leaveLoading.value = true
                val leaves = repository.getLeavesForMonthAndYear(
                    employeePhoneNumber,
                    year,
                    month
                )
                _leaveLoading.value = false
                _leaveData.value = leaves
            } catch (e: Exception) {
                _leaveLoading.value = false
                _error.value = "Failed to fetch leave data: ${e.message}"
            }
        }
    }

    private val _workHourData = MutableStateFlow<AddWorkingHourDataClass?>(null)
    val workHourData: StateFlow<AddWorkingHourDataClass?> = _workHourData

    fun fetchWorkHourData(
        employeePhoneNumber: String,
        month: String,
        year: String
    ) {
        viewModelScope.launch {
            try {
                _workHourLoading.value=true
                val data = repository.fetchRecentWorkHourData(
                    employeePhoneNumber,
                    month,
                    year
                )
                _workHourLoading.value=false
                _workHourData.value = data
            } catch (e: Exception) {
                _workHourLoading.value=false
                _error.value=e.message
            }
        }
    }


}
