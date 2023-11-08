package com.example.app1intunesdk

import android.app.Activity
import android.content.Context
import android.util.Log
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalUiRequiredException
import java.util.Arrays

class MSALUtil {
    private var mMsalPublicClientApplication: IPublicClientApplication? = null

    fun acquireToken(
        fromActivity: Activity,
        scopes: Array<String>,
        loginHint: String?,
        callback: AuthenticationCallback
    ) {
        initializeMsalClientApplication(fromActivity.applicationContext)
        val params = AcquireTokenParameters.Builder()
            .withScopes(listOf(*scopes))
            .withCallback(callback)
            .startAuthorizationFromActivity(fromActivity)
            .withLoginHint(loginHint)
            .build()

        mMsalPublicClientApplication?.acquireToken(params)
    }

    fun acquireTokenSilent(
        appContext: Context,
        aadId: String,
        scopes: Array<String>,
        callback: AuthenticationCallback
    ) {
        initializeMsalClientApplication(appContext.applicationContext)

        val account = getAccount(aadId)
        if (account == null) {
            /*LOGGER.severe("Failed to acquire token: no account found for $aadId")*/
            Log.e("MSAL-Acquire-Token-Error", "Failed to acquire token: no account found for $aadId")
            callback.onError(
                MsalUiRequiredException(
                    MsalUiRequiredException.NO_ACCOUNT_FOUND,
                    "no account found for $aadId"
                )
            )
            return
        }

        val params = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .fromAuthority(account.authority)
            .withScopes(listOf(*scopes))
            .withCallback(callback)
            .build()

        mMsalPublicClientApplication?.acquireTokenSilentAsync(params)
    }

    fun acquireTokenSilentSync(
        appContext: Context,
        aadId: String,
        scopes: Array<String>
    ): IAuthenticationResult? {
        initializeMsalClientApplication(appContext)
        val account = getAccount(aadId)
        if (account == null) {
            /*LOGGER.severe("Failed to acquire token: no account found for $aadId")*/
            Log.e("MSAL-Acquire-Token-Error", "Failed to acquire token: no account found for $aadId")
            throw MsalUiRequiredException(
                MsalUiRequiredException.NO_ACCOUNT_FOUND,
                "no account found for $aadId"
            )
        }

        val params = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .fromAuthority(account.authority)
            .withScopes(Arrays.asList(*scopes))
            .build()

        return mMsalPublicClientApplication?.acquireTokenSilent(params)
    }

    fun signOutAccount(appContext: Context, aadId: String) {
        initializeMsalClientApplication(appContext)
        val account = getAccount(aadId)

        if (account == null) {
            /*LOGGER.warning("Failed to sign out account: No account found for $aadId")*/
            Log.e("MSAL-Sign-Out-Account-Error", "Failed to sign out account: No account found for $aadId")
            return
        }

        if (mMsalPublicClientApplication is IMultipleAccountPublicClientApplication) {
            val multiAccountPCA =
                mMsalPublicClientApplication as IMultipleAccountPublicClientApplication

            multiAccountPCA.removeAccount(account)
        } else if (mMsalPublicClientApplication is ISingleAccountPublicClientApplication) {
            val singleAccountPCA =
                mMsalPublicClientApplication as ISingleAccountPublicClientApplication

            singleAccountPCA.signOut()
        }
    }

    private fun getAccount(aadId: String): IAccount? {
        var account: IAccount? = null

        if (mMsalPublicClientApplication is IMultipleAccountPublicClientApplication) {
            val multiAccountPCA =
                mMsalPublicClientApplication as IMultipleAccountPublicClientApplication

            account = multiAccountPCA.getAccount(aadId)
        } else if (mMsalPublicClientApplication is ISingleAccountPublicClientApplication) {
            val singleAccountPCA =
                mMsalPublicClientApplication as ISingleAccountPublicClientApplication

            val accountResult = singleAccountPCA.currentAccount
            if (accountResult != null) {
                account = accountResult.currentAccount
                // make sure this is the correct user
                if (account != null && account.id != aadId)
                    account = null
            }
        }
        return account
    }

    private fun initializeMsalClientApplication(appContext: Context) {
        if (mMsalPublicClientApplication == null) {

            mMsalPublicClientApplication = PublicClientApplication.create(appContext, R.raw.msal_auth_config)
        }
    }
}