package com.aplikasi.chatappstrials.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aplikasi.chatappstrials.R
import com.aplikasi.chatappstrials.models.PushNotifications
import com.aplikasi.chatappstrials.ui.BottomNav
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlin.random.Random

class FirebaseNotifService : FirebaseMessagingService() {

    val CHANNEL_ID = "my_notification_channel"

    companion object{
        var sharedPref: SharedPreferences? = null

        var token:String?
            get(){
                return sharedPref?.getString("token","")
            }
            set(value){
                sharedPref?.edit()?.putString("token",value)?.apply()
            }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(p0: RemoteMessage) {
        handleRemoteMsg(p0)


    }

    private fun handleRemoteMsg(p0: RemoteMessage) {
        val intent = Intent(this, BottomNav::class.java)
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifId = Random.nextInt()
        val notifMsg = Gson().fromJson(Gson().toJson(p0.data), PushNotifications::class.java)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotifChannel(notifManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0, intent, FLAG_MUTABLE)
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notifMsg.data.title)
            .setContentText(notifMsg.data.message)
            .setSmallIcon(R.drawable.ic_chat)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notifManager.notify(notifId, notif)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifChannel(notifManager: NotificationManager) {

        val channelName = "FirebaseChatChannel"
        val channel = NotificationChannel(CHANNEL_ID, channelName,IMPORTANCE_HIGH).apply {
            description = "My Firebase Chat Description"
            enableLights(true)
            lightColor = Color.WHITE
        }

        notifManager.createNotificationChannel(channel)

    }


}