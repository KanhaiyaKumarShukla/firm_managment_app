package com.rach.firmmanagement.appOwner

sealed class ScreenAppOwner(val route: String) {
    object AppOwnerPanel : ScreenAppOwner("AppOwnerPanel")
    object AddSuperAdmin : ScreenAppOwner("AddSuperAdmin")

}