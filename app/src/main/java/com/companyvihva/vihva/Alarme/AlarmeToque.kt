package com.companyvihva.vihva.Alarme

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.companyvihva.vihva.R

class AlarmeToque : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmeToque", "Alarme tocou!")

        // Criar e exibir a notificação quando o alarme tocar
        val notificationManager = NotificationManagerCompat.from(context)

        // Verificar se é necessário criar o canal de notificação (para Android Oreo e superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Canal de Alarme",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, CriaAlarme::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construção da notificação
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("Alarme Disparado!")
            .setContentText("Seu alarme está tocando.")
            .setSmallIcon(R.drawable.ic_alarme) // Ícone da notificação
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true) // Fechar a notificação quando clicar nela
            .build()

        // Verificação de permissões (exemplo incorreto, verificar o tipo correto de permissão)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Tratar permissão negada aqui
        }

        // Exibir a notificação
        notificationManager.notify(1, notificationBuilder)

        // Tocar som de alarme
        tocarSomDeAlarme(context)
    }

    private fun tocarSomDeAlarme(context: Context) {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone: Ringtone = RingtoneManager.getRingtone(context, notification)
            ringtone.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
