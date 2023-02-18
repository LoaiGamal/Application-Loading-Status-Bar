package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, appContext: Context, pendingDetailIntent: PendingIntent){
    val startIntent = Intent(appContext, MainActivity::class.java)
    val pendingStartIntent = PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        startIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val downloadImage = BitmapFactory.decodeResource(appContext.resources, R.drawable.downloaded)
    val bigImage = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        appContext,
        appContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(pendingStartIntent)
        .setAutoCancel(true)
        .setStyle(bigImage)
        .setLargeIcon(downloadImage)
        .addAction(0, appContext.getString(R.string.action_name), pendingDetailIntent)

    notify(NOTIFICATION_ID, builder.build())
}