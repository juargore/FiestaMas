package com.universal.fiestamas.presentation.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.MainActivity

class PushFirebaseListenerService: FirebaseMessagingService() {

    @Suppress("RedundantOverride")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["body"]
        val screen = remoteMessage.data["screen"] ?: ""
        val serviceEventId = remoteMessage.data["id_service_event"] ?: ""
        println("New message: $title && $message && $screen && $serviceEventId")
        NotificationBuilder(applicationContext).setUpNotification(title, message, screen, serviceEventId)
    }

    class NotificationBuilder(val context: Context) {
        @SuppressLint("ObsoleteSdkInt")
        fun setUpNotification(title: String?, message: String?, screen: String, serviceEventId: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "fiestamas_channel_01"
            val channelDesc = "This is Fiestamas Channel"
            val channelName = "fiestamas_channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(channelId, channelName, importance).apply {
                    this.description = channelDesc
                    this.enableLights(true)
                    this.lightColor = Color.RED
                    this.setShowBadge(false)
                }
                notificationManager.createNotificationChannel(mChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)

            val notificationIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("screen", screen)
                putExtra("serviceEventId", serviceEventId)
            }
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder.setContentIntent(pendingIntent)
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}
