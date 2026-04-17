package com.carless.driverapp.service

import android.util.Log
import com.carless.driverapp.data.api.ApiClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverAppFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.getService().updateFcmToken(mapOf("token" to token))
            } catch (e: Exception) {
                Log.e("FCM", "Failed to update token", e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // For MVP, polling handles UI updates. Push notifications serve as a wake-up signal.
        Log.d("FCM", "Push received: ${message.notification?.title}")
    }
}
