package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rach.firmmanagement.login.DataClassRegister
import com.rach.firmmanagement.login.RegisterRepository
import com.rach.firmmanagement.realRoomDatabase.CheckingData
import com.rach.firmmanagement.realRoomDatabase.CheckingRepository
import com.rach.firmmanagement.realRoomDatabase.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val repository: CheckingRepository = Graph.checkingRepository
) : ViewModel() {

    private val _phoneNumberInput = MutableStateFlow("")

    val phoneNumberInput: StateFlow<String> = _phoneNumberInput

    fun onPhoneNumberChanged(newPhoneNumber: String) {

        _phoneNumberInput.value = newPhoneNumber
    }

    // check Progress BAr

    private val _checkLoginnedProges = MutableStateFlow(false)
    val checkLoginedProgressBar: StateFlow<Boolean> = _checkLoginnedProges

    fun OnChangeProgressBar(newPro: Boolean) {
        _checkLoginnedProges.value = newPro
    }


    private val _firmOwnerNumber = MutableStateFlow("")

    val firmOwnerNumber: StateFlow<String> = _firmOwnerNumber


    fun onFirmOwnerChangedNumber(newOwnerNumber: String) {
        _firmOwnerNumber.value = newOwnerNumber
    }


    private val _selectGenderState = MutableStateFlow("Employee")
    val selectGenderState: StateFlow<String> = _selectGenderState

    fun onGenderChanged(newGender: String) {
        _selectGenderState.value = newGender
    }


    init {
        viewModelScope.launch {
            val existingData = repository.getData()
            if (existingData != null) {
                _firmOwnerNumber.value = existingData.ownerPhoneNumber
                _selectGenderState.value = existingData.whoIs
            }
        }
    }

    fun saveOrUpdateData() {
        viewModelScope.launch {
            val currentData = repository.getData()

            if (currentData != null) {
                repository.updateData(
                    CheckingData(
                        id = "1",
                        whoIs = _selectGenderState.value,
                        ownerPhoneNumber = _firmOwnerNumber.value
                    )
                )
            } else {
                repository.addData(
                    CheckingData(
                        id = "1",
                        whoIs = _selectGenderState.value,
                        ownerPhoneNumber = _firmOwnerNumber.value
                    )
                )
            }
        }
    }

    /// Register Screen ViewModel

    //Name
    private val _resisterName = MutableStateFlow("")

    val registerName: StateFlow<String> = _resisterName

    fun onChangeRegisterName(newRegisterName: String) {
        _resisterName.value = newRegisterName
    }

    //Mobile Number

    private val _registerMobileNumber = MutableStateFlow("")
    val registerMobileNumber: StateFlow<String> = _registerMobileNumber

    fun onChangeRegisterMobileNumber(newMobileNumberReg: String) {
        _registerMobileNumber.value = newMobileNumberReg
    }

    // Email

    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail

    fun onChangeRegisterEmail(newRegisterChangeEmail: String) {
        _registerEmail.value = newRegisterChangeEmail
    }

    // Firm Name

    private val _registerFirmName = MutableStateFlow("")
    val registerFirmName: StateFlow<String> = _registerFirmName

    fun onChangeFirmName(newFirmName: String) {
        _registerFirmName.value = newFirmName
    }

    // Address

    private val _registerAddress = MutableStateFlow("")
    val registeredAddress: StateFlow<String> = _registerAddress

    fun onChangeAddress(newChangeAddress: String) {
        _registerAddress.value = newChangeAddress
    }

    // City

    private val _cityName = MutableStateFlow("")

    val cityName: StateFlow<String> = _cityName

    fun onChangeCityName(newCityName: String) {
        _cityName.value = newCityName
    }

    // Pincode

    private val _pincode = MutableStateFlow("")
    val pincode: StateFlow<String> = _pincode

    fun onChangePinCode(changePincode: String) {
        _pincode.value = changePincode
    }

    // Radio Button

    private val _registerGenderState = MutableStateFlow("Employee")

    val registerGenderState: StateFlow<String> = _registerGenderState

    fun onRegChangeGenderState(onRegChangeRegisterState: String) {
        _registerGenderState.value = onRegChangeRegisterState
    }

    // Check Box

    private val _isCheckedBoxCheck = MutableStateFlow(false)
    val isCheckedBoxCheck: StateFlow<Boolean> = _isCheckedBoxCheck

    fun onCheckBox(onChangeCheckBox: Boolean) {
        _isCheckedBoxCheck.value = onChangeCheckBox
    }

    // Otp Screen

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp

    fun onChangeOtp(newOtp: String) {
        _otp.value = newOtp
    }

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun onVerficationIdChanged(id: String) {
        _verificationId.value = id
    }

    // Register Screen DataBase Code
    private val dataUploadRepos = RegisterRepository()

    fun saveUserRegisterData(
        role: String,
        name: String,
        mobileNumber: String,
        email: String,
        firmName: String,
        address: String,
        city: String,
        pinCode: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val dataClassRegister = DataClassRegister(
            name = name,
            mobileNumber = mobileNumber,
            email = email,
            firmName = firmName,
            address = address,
            city = city,
            pinCode = pinCode
        )

        viewModelScope.launch {
            dataUploadRepos.saveUserData(
                role, dataClassRegister,
                phoneNumber = mobileNumber,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }

    }

    private val _progressBarState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressBarState

    fun onProgressBarState(newState: Boolean) {
        _progressBarState.value = newState
    }

    // CheckLogin Or not

    fun checkLoginOrNot(
        role: String,
        mobileNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        viewModelScope.launch {
            dataUploadRepos.checkRegisterOrNot(
                role = role,
                phoneNumber = mobileNumber,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }

    }

}