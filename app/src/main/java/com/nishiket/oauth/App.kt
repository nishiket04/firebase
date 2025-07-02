package com.nishiket.oauth

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        val firebaseAuth: FirebaseAuth by lazy {
            FirebaseAuth.getInstance()
        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
    }
}