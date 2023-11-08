package com.example.app1intunesdk

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback

class AuthenticationCallback(private val mContext: Context) : MAMServiceAuthenticationCallback {

    val msalUtilObj = MSALUtil()
    override fun acquireToken(upn: String, aadId: String, resourceId: String): String? {
        try {
            val scopes = arrayOf("$resourceId/.default")
            val result: IAuthenticationResult? =
                msalUtilObj.acquireTokenSilentSync(mContext, aadId, scopes)
            if (result != null) {
                return result.accessToken
            }
        } catch (e: MsalException) {
            /*LOGGER.log(Level.SEVERE, "Failed to get token for MAM Service", e)*/
            Log.e("MAM-Service-Error", "Failed to get token for MAM Service. ${e.toString()}")
            return null
        } catch (e: InterruptedException) {
            /*LOGGER.log(Level.SEVERE, "Failed to get token for MAM Service", e)*/
            Log.e("MAM-Service-Error", "Failed to get token for MAM Service. ${e.toString()}")
            return null
        }
        /*LOGGER.warning("Failed to get token for MAM Service - no result from MSAL")*/
        Log.e("MAM-Service-Error", "Failed to get token for MAM Service - no result from MSAL")
        return null
    }
}

