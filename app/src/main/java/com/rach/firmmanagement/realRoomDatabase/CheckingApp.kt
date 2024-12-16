package com.rach.firmmanagement.realRoomDatabase

import android.app.Application

class CheckingApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}