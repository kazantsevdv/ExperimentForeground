package com.example.testforeground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

object Notifications {

    private var id: Int = UUID.randomUUID().hashCode()
    private val CHANNEL_ID = "CHANNEL_ID"

    fun createChannel(
        context: Context,
        channelId: String = CHANNEL_ID,
        @StringRes channelName: Int,
        @StringRes channelDescription: Int,
        importanceLevel: Int = NotificationManager.IMPORTANCE_HIGH
    ) {
        val channel = NotificationChannel(
            channelId,
            context.getString(channelName),
            importanceLevel
        ).apply {
            description = context.getString(channelDescription)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(
        context: Context,
        title: String,
        content: String,
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
       val  notification = builder.setContentTitle(title).setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground).build()
        return notification

    }
}