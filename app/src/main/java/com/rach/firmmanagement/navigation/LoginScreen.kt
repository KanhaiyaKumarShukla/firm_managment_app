package com.rach.firmmanagement.navigation

import okhttp3.Route

sealed class LoginScreen(val route: String) {

    object PhoneNumberLogin:LoginScreen("Login")
    object RegisterScreen:LoginScreen("Register")
    object OtpScreen:LoginScreen("OtpScreen")
}