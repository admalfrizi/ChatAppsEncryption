package com.aplikasi.chatappstrials.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.aplikasi.chatappstrials.R
import com.aplikasi.chatappstrials.ui.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseNotifService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteData: RemoteMessage) {
        super.onMessageReceived(remoteData)

        if(remoteData.data.isNotEmpty()){

            val map : Map<String, String> = remoteData.data

            val title = map["title"]
            val message = map["message"]

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                createOreoNotif(title, message)
            }
            else {
                createNotif(title, message)
            }
        }

        if (remoteData.notification != null){
            remoteData.notification?.let {
                Log.d(TAG, "Pesan Notifikasi Body : ${it.body}")

            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun createOreoNotif(title: String?, message: String?) {

        val channel = NotificationChannel(Constants.CHANNEL_ID, "Message", IMPORTANCE_HIGH)

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notif = Notification.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_chat)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.white, null))
            .build()

        manager.notify(100, notif)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotif(title: String?, message: String?) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_chat)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.white, null))
            .setSound(uri)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65), builder.build())
    }

    private fun updateToken(token: String) {
        Log.d(TAG, "updateToken($token)")
    }
}