package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass
import com.rach.firmmanagement.repository.AppOwnerRepository
import com.rach.firmmanagement.repository.NoAdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppOwnerViewModel() : ViewModel() {
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


    private val _phoneNumber = MutableStateFlow("")
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

    val appOwnerRepository = AppOwnerRepository()

    fun addFirm(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {

        viewModelScope.launch {
            appOwnerRepository.addFirm(
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
    private val _firms = MutableStateFlow<List<NoAdminDataClass>>(emptyList())
    val firms: StateFlow<List<NoAdminDataClass>> = _firms

    fun getAllFirms(){
        viewModelScope.launch {
            appOwnerRepository.getAllFirms(
                onSuccess = {
                    _firms.value = it
                },
                onFailure = {
                    _firms.value = emptyList()
                }
            )
        }
    }

    fun deleteFirm(firm: NoAdminDataClass) {
        viewModelScope.launch {
            appOwnerRepository.deleteFirm(
                phoneNumber = firm.phoneNumber.toString(),
                onSuccess = {
                    getAllFirms()
                },
                onFailure = {e->
                    Log.d("App Owner", "deleteFirm: $e")
                }
            )
        }
    }

    fun updateFirm(updatedFirm: NoAdminDataClass) {
        viewModelScope.launch {
            appOwnerRepository.updateFirm(
                phoneNumber = updatedFirm.phoneNumber.toString(),
                updatedFirm = updatedFirm,
                onSuccess = {
                    getAllFirms()
                },
                onFailure = {e->
                    Log.d("App Owner", "deleteFirm: $e")
                }
            )
        }

    }
    private var firmsListener: ListenerRegistration? = null

    fun listenToFirmsUpdates() {
        firmsListener?.remove() // Remove any previous listener to prevent duplication

        firmsListener = appOwnerRepository.listenToFirms(
            onSuccess = { updatedFirms ->
                _firms.value = updatedFirms
            },
            onFailure = { exception ->
                Log.e("App Owner", "Error listening to firms: ${exception.message}")
            }
        )
    }

}