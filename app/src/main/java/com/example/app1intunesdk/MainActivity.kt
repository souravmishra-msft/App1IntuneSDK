package com.example.app1intunesdk

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app1intunesdk.databinding.ActivityMainBinding
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalIntuneAppProtectionPolicyRequiredException
import com.microsoft.identity.client.exception.MsalUserCancelException
import com.microsoft.intune.mam.client.app.MAMComponents
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry
import com.microsoft.intune.mam.policy.MAMEnrollmentManager
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification
import com.microsoft.intune.mam.policy.notification.MAMNotification
import com.microsoft.intune.mam.policy.notification.MAMNotificationType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var msalApp: ISingleAccountPublicClientApplication? = null
    private var msalAccount: IAccount? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("Inside-onCreateMainActivity", "Inside onCreateMainActivity")

        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.msal_auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication?) {
                    msalApp = application

                    loadAccount()
                }

                override fun onError(exception: MsalException?) {
                    Log.i("PublicClientApplicationInitError", "${exception.toString()}")
                }
            }
        )
    }

    /*private fun initializeIntuneComponents() {
        // Initialize MAMEnrollmentManager and other Intune SDK components
        try {

            // Other Intune SDK initialization here...
            *//*mamEnrollmentManager?.registerAuthenticationCallback(authenticationCallback)*//*

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
        } catch (e: AssertionError) {
            Log.e("IntuneInitializationError", e.toString())
        }
    }*/

    // Define the authentication callback
    /*private val authenticationCallback = object: MAMServiceAuthenticationCallback {
        override fun acquireToken(p0: String, p1: String, p2: String): String? {
            msalApp.acquireToken()
        }
    }*/

    override fun onResume() {
        super.onResume()
        initializeUI()
        loadAccount()
    }

    private fun initializeUI() {
        binding.btnLogin.setOnClickListener {
            val acquireTokenParameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(this)
                .withScopes(arrayOf("user.read").toList())
                .withCallback(getAuthCallback())

            msalApp?.acquireToken(acquireTokenParameters.build())
            /*val silentAcquireTokenParameters = AcquireTokenSilentParameters.Builder()
                .withScopes(arrayOf("user.read").toList())
                .fromAuthority(msalAccount?.getAuthority())
                .withCallback(getAuthCallback())
                .forAccount(msalAccount)*/

            Log.i("SignIn --> Account", "${msalAccount}")
            /*Log.d("Silent-Parameters", "${silentAcquireTokenParameters}")*/
            Log.d("Acquire-Token-Parameters", "${acquireTokenParameters}")
            /*if (msalAccount != null) {
                msalApp?.acquireTokenSilentAsync(silentAcquireTokenParameters.build())
            } else if(msalAccount == null) {
                msalApp?.acquireToken(acquireTokenParameters.build())
            }*/
            /*val signInParameters = SignInParameters.builder()
                .withActivity(this)
                .withScopes(arrayOf("user.read").toList())
                .withCallback(getAuthCallback())

            msalApp?.signIn(signInParameters.build())*/
        }

        binding.btnLogout.setOnClickListener {
            msalApp?.signOut(
                object : ISingleAccountPublicClientApplication.SignOutCallback {
                    override fun onSignOut() {
                        updateUI(null)
                        signOut()
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("SignOut Error", "${exception.toString()}")
                        /*tvLog?.text = exception.toString()*/
                    }
                }
            )
        }
    }

    private fun loadAccount() {
        if (msalApp == null) {
            return
        }

        msalApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    msalAccount = activeAccount
                    updateUI(msalAccount)
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    if (currentAccount == null) {
                        msalAccount = null
                        signOut()
                    }
                }

                override fun onError(exception: MsalException) {
                    Log.i("Logout Error", "${exception.toString()}")
                    /*binding.tvLogs.text = exception.toString()*/
                }
            }
        )
    }

    private fun getAuthCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                msalAccount = authenticationResult?.account
                updateUI(msalAccount)

                if (msalAccount != null) {
                    Log.i("MSAL-Account", "${msalAccount!!.claims}")
                    var upn = msalAccount!!.claims?.get("preferred_username").toString()
                    var aadId = msalAccount!!.claims?.get("oid").toString()
                    var tenantId = msalAccount!!.claims?.get("tid").toString()
                    var authorityURL = "https://login.microsoftonline.com/$tenantId"

                    Log.i("Inside-onSuccess", "Inside onSuccess")
                }

            }

            override fun onError(exception: MsalException?) {
                Log.i("AuthCallback Error", "${exception.toString()}")
                /*binding.tvLogs.text = exception.toString()*/
                if (exception is MsalIntuneAppProtectionPolicyRequiredException) {
                    val appException = exception as MsalIntuneAppProtectionPolicyRequiredException
                    // Note: An app that has enabled APP CA with Policy Assurance would need to pass these values to `remediateCompliance`.
                    // For more information, see https://docs.microsoft.com/en-us/mem/intune/developer/app-sdk-android#app-ca-with-policy-assurance
                    val upn = appException.accountUpn
                    val aadid = appException.accountUserId
                    val tenantId = appException.tenantId
                    val authorityURL = appException.authorityUrl
                    val message = "Intune App Protection Policy required."
                    Toast.makeText(this@MainActivity,"Intune App Protection Policy required.",Toast.LENGTH_SHORT).show()
                    Log.i("MsalIntuneAppProtectionPolicyRequiredException","MsalIntuneAppProtectionPolicyRequiredException received.")
                    Log.i("Data-From-Broker","Data from broker: UPN: $upn; AAD ID: $aadid; Tenant ID: $tenantId; Authority: $authorityURL")
                } else if (exception is MsalUserCancelException) {
                    Toast.makeText(this@MainActivity, "User cancelled sign-in request.", Toast.LENGTH_SHORT).show()
                    Log.e("MsalUserCancelException", "${exception.toString()}")
                } else {
                    Toast.makeText(this@MainActivity, "Exception occurred - check logcat.", Toast.LENGTH_SHORT).show()
                    Log.e("Error", "${exception.toString()}")
                }
            }

            override fun onCancel() {
                Log.i("AuthCallback Error", "${"User Cancelled!"}")
                /*binding.tvLogs.text = "User Cancelled!"*/
            }
        }
    }

    private fun updateUI(msalAccount: IAccount?) {
        /*if (msalAccount != null) {
            Log.i("User", "${msalAccount.claims?.get("name").toString()}")
        }*/
        if (msalAccount != null) {
            binding.btnLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
            binding.username.text = "User: ${msalAccount.claims?.get("name").toString()}"
        }
    }

    private fun signOut() {
        Toast.makeText(this, "You have successfully logged out!", Toast.LENGTH_SHORT).show()
        binding.btnLogin.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE
        binding.username.text = ""
        /*binding.userprofilelayout.visibility = View.GONE
        binding.profileImage.setImageResource(R.drawable.android_logo)
        binding.tvLogs.text = ""*/
    }
}