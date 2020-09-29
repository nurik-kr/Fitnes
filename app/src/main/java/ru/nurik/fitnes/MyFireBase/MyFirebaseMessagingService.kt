package ru.nurik.fitnes.MyFireBase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() { // для уведом из firebase

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        NotificationUtils.createNotification(applicationContext,
            p0.notification?.title,
            p0.notification?.body
        )
    }
}