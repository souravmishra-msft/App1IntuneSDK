package com.example.app1intunesdk.authentication

import android.content.Context
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback
import java.util.logging.Level
import java.util.logging.Logger

class authCallback(applicationContext: Context) : MAMServiceAuthenticationCallback {
    private val LOGGER = Logger.getLogger(authCallback::class.java.name)

    private var mContext: Context? = null

    fun authCallback(context: Context) {
        mContext = context.applicationContext
    }

    override fun acquireToken(upn: String, aadId: String, resourceId: String): String? {
        try {
            // Create the MSAL scopes by using the default scope of the passed in resource id.
            val scopes = arrayOf<String?>("$resourceId/.default")
            val result = MSALUtil.acquireTokenSilentSync(mContext!!, aadId, scopes)
            if (result != null) return result.accessToken
        } catch (e: MsalException) {
            LOGGER.log(Level.SEVERE, "Failed to get token for MAM Service", e)
            return null
        } catch (e: InterruptedException) {
            LOGGER.log(Level.SEVERE, "Failed to get token for MAM Service", e)
            return null
        }
        LOGGER.warning("Failed to get token for MAM Service - no result from MSAL")
        return null
    }
}

