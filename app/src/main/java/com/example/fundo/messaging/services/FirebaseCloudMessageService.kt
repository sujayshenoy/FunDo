package com.example.fundo.messaging.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.fundo.R
import com.example.fundo.common.NotifyWorker
import com.example.fundo.ui.splash.SplashScreenActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseCloudMessageService: FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(p0: RemoteMessage) {
        val title = p0.notification?.title
        val message = p0.notification?.body

        createNotificationChannel()
        val intent = Intent(this,SplashScreenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,0,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, NotifyWorker.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_splash)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NotifyWorker.NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Fundo Notify Channel"
        val desc = "Fundo Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NotifyWorker.CHANNEL_ID, name, importance)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}