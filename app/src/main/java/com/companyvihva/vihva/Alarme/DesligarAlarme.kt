package com.companyvihva.vihva.Alarme

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R

class DesligarAlarme : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desligar_alarme)

        // Mensagem que informa o usuário que o alarme está tocando
        val textView = findViewById<TextView>(R.id.alarm_message)
        textView.text = "Alarme disparado! Clique no botão abaixo para desligá-lo."

        // Botão para desligar o alarme
        val btnDesligarAlarme = findViewById<Button>(R.id.btn_desligar_alarme)
        btnDesligarAlarme.setOnClickListener {
            desligarAlarme()
        }
    }

    private fun desligarAlarme() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmeToque::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this, "Alarme desligado", Toast.LENGTH_SHORT).show()
        finish()
    }
}
