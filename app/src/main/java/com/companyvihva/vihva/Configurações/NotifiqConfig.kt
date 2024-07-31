package com.companyvihva.vihva.Configuracoes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.companyvihva.vihva.Alarme.CriaAlarme
import com.companyvihva.vihva.R

class NotifiqConfig : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Obter o nome do solicitante da Intent
        val nomeSolicitante = intent.getStringExtra("nomeSolicitante") ?: "Desconhecido"

        // Mostrar a notificação
        mostrarNotificacao(context, nomeSolicitante)
    }

    private fun mostrarNotificacao(context: Context, nomeSolicitante: String) {
        // Verifique a permissão antes de tentar criar a notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissão não concedida, não faça nada ou log um erro
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(context)
        val channelId = "alarm_channel"

        // Criar o canal de notificação para Android 8.0 e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Solicitações de Amizade",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para solicitações de amizade"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir a Activity quando a notificação for clicada
        val notificationIntent = Intent(context, CriaAlarme::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construção da notificação
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarme)
            .setContentTitle("Nova Solicitação de Amizade")
            .setContentText("Você recebeu uma solicitação de amizade do Dr(a) $nomeSolicitante")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)

        // Notificar
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
