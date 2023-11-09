package com.example.app1intunesdk

import android.app.Application
import android.util.Log
import com.example.app1intunesdk.authentication.authCallback
import com.microsoft.intune.mam.client.app.MAMComponents
import com.microsoft.intune.mam.policy.MAMEnrollmentManager

class app1IntuneSDKApplication: Application(){
    private var mamEnrollmentManager: MAMEnrollmentManager? = null

    override fun onCreate() {
        super.onCreate()
        Log.i("Inside-AuthCallbackApplication", "Inside AuthCallbackApplication")
        mamEnrollmentManager = MAMComponents.get(MAMEnrollmentManager::class.java)
        mamEnrollmentManager?.registerAuthenticationCallback(authCallback(applicationContext))
    }
}