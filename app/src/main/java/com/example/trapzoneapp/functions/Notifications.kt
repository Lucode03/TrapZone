package com.example.trapzoneapp.functions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

var timeTrap = 0L

fun showNearbyTrapNotification(context: Context,title: String, message: String) {

    val currentTime = System.currentTimeMillis()
    if (currentTime - timeTrap < 10000)
        return
    timeTrap = currentTime
    val channelId = "nearby_channel"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Neko ili nešto je u blizini",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
var timeUser = 0L
fun showNearbyUserNotification(context: Context,title: String, message: String) {

    val currentTime = System.currentTimeMillis()
    if (currentTime - timeUser < 30000)
        return
    timeUser = currentTime
    val channelId = "nearby_channel"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Neko ili nešto je u blizini",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}