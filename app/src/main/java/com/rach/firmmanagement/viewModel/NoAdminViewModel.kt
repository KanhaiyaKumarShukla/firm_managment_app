package com.rach.firmmanagement.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass
import com.rach.firmmanagement.repository.HomeRepository
import com.rach.firmmanagement.repository.NoAdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoAdminViewModel : ViewModel() {

    //firm Name
    private val _firmName = MutableStateFlow("")
    val firmName: StateFlow<String> = _firmName

    fun onChangeFirmName(newFirmName: String) {
        _firmName.value = newFirmName
    }

    //progressbar State

    private val _progressbarState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressbarState

    fun onChangeState(newState: Boolean) {
        _progressbarState.value = newState
    }

    // phoneNumber

    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber

    private val _phoneNumber = MutableStateFlow("$currentUserPhoneNumber")
    val phoneNumber: StateFlow<String> = _phoneNumber

    fun onChangePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    //onwer Name

    private val _ownerName = MutableStateFlow("")
    val ownerName: StateFlow<String> = _ownerName

    fun onChangeownerName(changeOwnerName: String) {
        _ownerName.value = changeOwnerName
    }

    //Area Pincode

    private val _areaPincode = MutableStateFlow("")
    val areaPincode: StateFlow<String> = _areaPincode

    fun onChangeAreaPinCode(newPincode: String) {
        _areaPincode.value = newPincode
    }

    //Address

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    fun onAddressChange(newAddress: String) {
        _address.value = newAddress
    }

    // buttonClicked

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked: StateFlow<Boolean> = _buttonClicked

    fun onButtonClickedStateChange(newClick: Boolean) {
        _buttonClicked.value = newClick
    }

    //Store Data on FireStore

    val adminRepository = NoAdminRepository()

    fun raiseRequest(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {

        viewModelScope.launch {
            adminRepository.raiseARequest(
                phoneNumber = _phoneNumber.value,
                noAdminDataClass = NoAdminDataClass(
                    firmName = _firmName.value,
                    phoneNumber = _phoneNumber.value,
                    ownerName = _ownerName.value,
                    address = _address.value,
                    pinCode = _areaPincode.value,
                    status = "pending"
                ),
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }

    }

    // Main Home

    sealed class UiState {
        object Loading : UiState()
        object AdminPanel : UiState()
        object OnFailure : UiState()
        object OnPending : UiState()
        object EmployeeUi : UiState()
        object NoEmployee : UiState()
        object AppOwner: UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    val uiState: StateFlow<UiState> = _uiState

    fun onChangeUiState(newUiState: UiState) {
        _uiState.value = newUiState
    }

    val homeRepository = HomeRepository()

    var progressState = mutableStateOf(false)
        private set

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh:StateFlow<Boolean> = _isRefresh

    fun onChangeRefreshState(newState: Boolean){
        _isRefresh.value = newState
    }

    private val _gender =MutableStateFlow("Employee")
    val gender:StateFlow<String> = _gender

    fun getGender(){
        viewModelScope.launch {
            homeRepository.getGender(
                phoneNumber = _phoneNumber.value,
                dataFound = { gender->
                    Log.d("Hins", "Data Found Gender: $gender")
                    _gender.value=gender
                },
                noDataFound = {
                    Log.d("Hins", "No Data Found Gender")
                    _gender.value="Employee"
                }
            )
        }
    }

    fun checkUserExist(
        genderState: String,
        adminNumber: String,
        dataFound: () -> Unit,
        noData: () -> Unit,
        pendingData: () -> Unit
    ) {

        Log.d("Hins", "Check UserExist: $genderState + $adminNumber")
        viewModelScope.launch {
            _isRefresh.value = true
            if (genderState == "Super Admin" || genderState=="Admin") {
                homeRepository.checkEmployeeOrEmployee(
                    phoneNumber = _phoneNumber.value,
                    genderState = genderState,
                    adminNumber = adminNumber,
                    dataFound = {
                        onChangeUiState(UiState.AdminPanel)
                        dataFound()
                    },
                    noDataFound = {
                        onChangeUiState(UiState.OnFailure)
                        noData()
                    },
                    pendingDataFound = {
                        onChangeUiState(UiState.OnPending)
                        pendingData()
                    }
                )
                _isRefresh.value = false

            }else if(genderState=="App Owner"){
                progressState.value = true
                homeRepository.checkEmployeeOrEmployee(
                    phoneNumber = _phoneNumber.value,
                    genderState = genderState,
                    adminNumber = adminNumber,
                    dataFound = {
                        progressState.value = false
                        onChangeUiState(UiState.AppOwner)
                        dataFound()
                    },
                    noDataFound = {
                        progressState.value = false
                        onChangeUiState(UiState.OnFailure)
                        noData()
                    },
                    pendingDataFound = {
                        progressState.value = false
                        onChangeUiState(UiState.OnPending)
                        pendingData()

                    }
                )
                _isRefresh.value = false

            }
            else {

                progressState.value = true

                homeRepository.checkEmployeeOrEmployee(
                    phoneNumber = _phoneNumber.value,
                    genderState = genderState,
                    adminNumber = adminNumber,
                    dataFound = {

                        progressState.value = false
                        onChangeUiState(UiState.EmployeeUi)
                        dataFound()

                    },
                    noDataFound = {

                        progressState.value = false

                        onChangeUiState(UiState.NoEmployee)
                        noData()

                    },
                    pendingDataFound = {

                        progressState.value = false

                    }
                )

                _isRefresh.value = false

            }

        }

    }

}