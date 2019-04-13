package com.simsimhan.promissu.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simsimhan.promissu.MainActivity
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import timber.log.Timber


class CustomFirebaseMessagingService : FirebaseMessagingService(){

    private val TAG = "FirebaseService"

    override fun onNewToken(token: String) {
        Timber.d("@@@@ONNEWTOKEN")
        super.onNewToken(token)
        PromissuApplication.diskCache!!.fcmToken = token

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            PromissuApplication.diskCache!!.fcmToken = it.result!!.token
            Timber.d("Register Fcm Token : ${it.result!!.token}")
        }
        Log.d(TAG, "new Token: $token")
    }

    override fun onMessageReceived(p0: RemoteMessage) {

        Log.d(TAG, "From: " + p0.from)

        if(p0.notification != null) {
            Log.d(TAG, "Notification Message Body: ${p0.notification?.body}")
            sendNotification(p0.notification?.body)
        }
    }

    private fun sendNotification(body: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this,"Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Push Notification FCM")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

}