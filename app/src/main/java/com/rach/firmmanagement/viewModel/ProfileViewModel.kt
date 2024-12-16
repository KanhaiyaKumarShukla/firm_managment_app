package com.rach.firmmanagement.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rach.firmmanagement.login.DataClassRegister
import com.rach.firmmanagement.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    var isLoading = mutableStateOf(false)
        private set

    private val _name =  MutableStateFlow("")
    val name :StateFlow<String> = _name

    fun onChangeName(newName:String){
        _name.value = newName
    }

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber :StateFlow<String> = _phoneNumber

    fun onChangePhoneNumber(newPhoneNumber:String){

        _phoneNumber.value = newPhoneNumber
    }

    private val _firmName = MutableStateFlow("")
    val firmName :StateFlow<String> = _firmName

    fun onChangeFirmName(newFirmName:String){
        _firmName.value = newFirmName
    }

    private val _address = MutableStateFlow("")
    val address :StateFlow<String> = _address

    fun onChangeAddress(newAddress:String){
        _address.value = newAddress
    }

    private val _email = MutableStateFlow("")
    val email : StateFlow<String> = _email

    fun onChangeEmail(newEmail:String){
        _email.value = newEmail
    }

    private val _city = MutableStateFlow("")
    val city : StateFlow<String> = _city


    private val _pincode = MutableStateFlow("")
    val pincode : StateFlow<String> = _pincode


     fun loadProfile(
        role : String
    ) {
        viewModelScope.launch {
            isLoading.value = true
            repository.getProfile(
                role = role ,
                onSuccess = {profile ->
                    _name.value = profile.name ?:""
                    _phoneNumber.value = profile.mobileNumber ?:""
                    _firmName.value = profile.firmName ?:""
                    _address.value  = profile.address ?:""
                    _email.value = profile.email ?:""
                    _city.value=profile.city ?:""
                    _pincode.value = profile.pinCode ?:""
                    isLoading.value = false
                },
                onFailure = {
                    isLoading.value = false
                }
            )
        }
    }

    fun updateProfile(role: String){
        viewModelScope.launch{
            isLoading.value = true
            val updatedData = DataClassRegister(
                name = _name.value,
                mobileNumber = _phoneNumber.value,
                firmName = _firmName.value,
                address = _address.value,
                email = _email.value,
                city = _city.value,
                pinCode = _pincode.value
            )
            repository.updateProfile(
                role = role,
                updatedProfile = updatedData,
                onSuccess = {
                    loadProfile(role)
                    isLoading.value = false
                },
                onFailure = {
                    isLoading.value = false
                }
            )
        }
    }

}