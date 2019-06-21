package com.simsimhan.promissu.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simsimhan.promissu.MainActivity
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.FcmToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class CustomFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"
    private val disposable = CompositeDisposable()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PromissuApplication.diskCache!!.fcmToken = token
        sendFcmTokenToServer(token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {

        if (p0.notification != null) {
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

        var notificationBuilder = NotificationCompat.Builder(this, "Notification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Push Notification FCM")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendFcmTokenToServer(fcmToken: String) {
        disposable.add(
                PromissuApplication.retrofit!!
                        .create(AuthAPI::class.java)
                        .updateFcmToken(PromissuApplication.getVersionInfo(),"Bearer ${PromissuApplication.diskCache!!.userToken}", FcmToken(fcmToken))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            PromissuApplication.diskCache!!.fcmToken = fcmToken
                            Timber.d("Success:: Fcm Token registered ")
                            disposable.clear()
                            // save token
                        }, { onError ->
                            Timber.e("fail:: %s", onError.toString())
                        }))
    }
}