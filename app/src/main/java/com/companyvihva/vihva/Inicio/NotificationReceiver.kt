package com.companyvihva.vihva

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.companyvihva.vihva.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1 // Identificador único para a notificação

        val title = intent.getStringExtra("title") ?: "Lembrete"
        val description = intent.getStringExtra("description") ?: "Você tem um lembrete!"

        val notificationChannelId = "event_notifications"
        val notificationChannelName = "Event Notifications"

        // Criar o canal de notificação (necessário para Android 8.0 e superior)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.sininho) // Substitua pelo seu ícone
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
