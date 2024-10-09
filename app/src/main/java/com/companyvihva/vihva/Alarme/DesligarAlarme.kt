package com.companyvihva.vihva.Alarme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class DesligarAlarme : AppCompatActivity() {

    private lateinit var alarmTimeTextView: TextView
    private lateinit var alarmDateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desligar_alarme)

        // Inicializa os TextViews para exibir a hora e a data
        alarmTimeTextView = findViewById(R.id.alarm_time)
        alarmDateTextView = findViewById(R.id.alarm_date)

        // Formata a hora e a data atuais
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = Date()
        val currentTime = timeFormat.format(now)
        val currentDate = dateFormat.format(now)

        // Atualiza os TextViews com a hora e a data atuais
        alarmTimeTextView.text = currentTime
        alarmDateTextView.text = currentDate

        // Recebe o nome do remédio
        val remedioNome = intent.getStringExtra("remedioNome") ?: "Remédio"

        // Atualiza o TextView da mensagem do alarme com o nome do remédio
        val messageTextView = findViewById<TextView>(R.id.alarm_message)
        messageTextView.text = "Alarme disparado! Remédio: $remedioNome"

        val messageTextView2 = findViewById<TextView>(R.id.alarm_message2)
        messageTextView2.text = "Clique no botão abaixo para desligá-lo."

        // Configura o botão para desligar o alarme
        val btnDesligarAlarme = findViewById<Button>(R.id.btn_desligar_alarme)
        btnDesligarAlarme.setOnClickListener {
            desligarAlarme()
        }

        // Começar a tocar o alarme (opcional, dependendo da lógica desejada)
        AlarmeToque.mediaPlayer?.start()
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
        pendingIntent.cancel()

        // Parar o som do alarme tocado pelo AlarmeToque
        AlarmeToque().pararSomDeAlarme()

        Toast.makeText(this, "Alarme desligado", Toast.LENGTH_SHORT).show()
        finish()
    }

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
