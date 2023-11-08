package com.example.app1intunesdk

import android.app.Application
import android.util.Log
import com.microsoft.intune.mam.client.app.MAMComponents
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry
import com.microsoft.intune.mam.policy.MAMEnrollmentManager
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification
import com.microsoft.intune.mam.policy.notification.MAMNotification
import com.microsoft.intune.mam.policy.notification.MAMNotificationType

class App1IntuneSDKApplication : Application() {
    private var mgr: MAMEnrollmentManager? = null
    override fun onCreate() {
        super.onCreate()
        Log.i("Inside-App1IntuneSDKApplication", "Inside App1IntuneSDKApplication")
        // Registers a MAMAuthenticationCallback, which will try to acquire access tokens for MAM.
        // This is necessary for proper MAM integration.
        mgr = MAMComponents.get(MAMEnrollmentManager::class.java)
        mgr?.registerAuthenticationCallback(AuthenticationCallback(applicationContext))
        /* This section shows how to register a MAMNotificationReceiver, so you can perform custom
         * actions based on MAM enrollment notifications.
         * More information is available here:
         * https://docs.microsoft.com/en-us/intune/app-sdk-android#types-of-notifications */
        MAMComponents.get(MAMNotificationReceiverRegistry::class.java)!!
            .registerReceiver(
                { notification: MAMNotification? ->
                    if (notification is MAMEnrollmentNotification) {
                        val result =
                            (notification as MAMEnrollmentNotification).enrollmentResult
                        when (result) {
                            MAMEnrollmentManager.Result.AUTHORIZATION_NEEDED, MAMEnrollmentManager.Result.NOT_LICENSED, MAMEnrollmentManager.Result.ENROLLMENT_SUCCEEDED, MAMEnrollmentManager.Result.ENROLLMENT_FAILED, MAMEnrollmentManager.Result.WRONG_USER, MAMEnrollmentManager.Result.UNENROLLMENT_SUCCEEDED, MAMEnrollmentManager.Result.UNENROLLMENT_FAILED, MAMEnrollmentManager.Result.PENDING, MAMEnrollmentManager.Result.COMPANY_PORTAL_REQUIRED -> Log.d(
                                "Enrollment Receiver",
                                result.name
                            )

                            else -> Log.d("Enrollment Receiver", result.name)
                        }
                    } else {
                        Log.d(
                            "Enrollment Receiver",
                            "Unexpected notification type received"
                        )
                    }
                    true
                }, MAMNotificationType.MAM_ENROLLMENT_RESULT
            )
    }
}