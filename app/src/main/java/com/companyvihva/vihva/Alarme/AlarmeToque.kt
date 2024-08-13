package com.companyvihva.vihva.Alarme

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.companyvihva.vihva.R

class AlarmeToque : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmeToque", "Alarme tocou!")

        val notificationManager = NotificationManagerCompat.from(context)

        // Criação do canal de notificação para Android O e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Canal de Alarme",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                description = "Canal para alarmes"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, DesligarAlarme::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("Alarme Disparado!")
            .setContentText("Seu alarme está tocando.")
            .setSmallIcon(R.drawable.ic_alarme)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Verifica a permissão de notificação
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            Log.e("AlarmeToque", "Permissão de notificação não concedida.")
        }

        tocarSomDeAlarme(context)
    }

    private fun tocarSomDeAlarme(context: Context) {
        // Libera o MediaPlayer anterior se estiver em uso
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.alarme)
        mediaPlayer?.apply {
            isLooping = true // Reproduz o som em loop
            start()
        }
    }

    // Método opcional para parar o som do alarme
    private fun pararSomDeAlarme() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                release()
            }
        }
        mediaPlayer = null
    }
}
