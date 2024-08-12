package com.companyvihva.vihva.Alarme

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CriaAlarme : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private val PERMISSION_REQUEST_CODE = 100
    private val NOTIFICATION_ID = 1

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_alarme)

        // Inicializando as variáveis de intent
        val frequencia = intent.getStringExtra("frequencia")
        val horaemhora = intent.getStringExtra("horaemhora")
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")
        val horaDiariamente = intent.getStringExtra("horaDiariamente")
        val estoque = intent.getStringExtra("estoque")
        val lembreme = intent.getStringExtra("lembreme")
        val tipomed = intent.getStringExtra("tipomed")
        val switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        val obsAlarme = intent.getStringExtra("OBS")
        val nome = intent.getStringExtra("remedioId")

        val editnomeAlarme = findViewById<TextView>(R.id.layout_nome_alarme)
        nome?.let {
            editnomeAlarme.text = it
        }

        val editDescAlarme = findViewById<EditText>(R.id.edit_descAlarme)

        val descProgramacao = findViewById<TextView>(R.id.descprogramacao)
        if (frequencia != null && horaemhora != null && duracao != null && data != null) {
            descProgramacao.text = "$frequencia - $horaemhora - $duracao - $data"
        }

        val descEstoque = findViewById<TextView>(R.id.descEstoque)
        if (estoque != null || tipomed != null) {
            descEstoque.text = "$estoque $tipomed"
        }

        // Configurando os listeners
        findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java).apply {
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("data", data)
                putExtra("duracao", duracao)
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horaemhora)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("estoque", estoque)
                putExtra("remedioId", nome)
                putExtra("switchEstoque", switchEstoqueChecked)
            }
            startActivity(telaConfigFrequencia)
        }

        findViewById<View>(R.id.container_estoque).setOnClickListener {
            val telaConfigEstoque = Intent(this, ConfigEstoque::class.java).apply {
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("data", data)
                putExtra("duracao", duracao)
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horaemhora)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("estoque", estoque)
                putExtra("remedioId", nome)
                putExtra("switchEstoque", switchEstoqueChecked)
            }
            startActivity(telaConfigEstoque)
        }

        findViewById<Button>(R.id.btn_salvarAlarme).setOnClickListener {
            requestAlarmPermissionsAndSchedule()
        }

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val telaEscolhaRemedio = Intent(this, EscolhaRemedio::class.java)
            startActivity(telaEscolhaRemedio)
        }
    }

    private fun Bdsave(index: Int) {
        firestore = FirebaseFirestore.getInstance()
        val documentos = listOf<String>() // Suposição: preencha com seus IDs de documentos
        if (index < documentos.size) {
            val docId = documentos[index]
            firestore.collection("doenca").document(docId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nome = document.getString("nome")
                        val url = document.getString("Url")
                        val tipo = document.getString("tipo")
                        val tipoRemedios = Tipo_Remedios(url ?: "", nome ?: "", tipo ?: "", docId)
                        // Faça algo com o objeto tipoRemedios aqui
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestAlarmPermissionsAndSchedule() {
        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions, PERMISSION_REQUEST_CODE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = getSystemService(AlarmManager::class.java)
                if (!alarmManager.canScheduleExactAlarms()) {
                    startActivity(Intent().apply {
                        action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    })
                    return
                }
            }
            agendarAlarme()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun agendarAlarme() {
        Log.d("AgendarAlarme", "Iniciando agendamento de alarme")

        val horaDiariamente = intent.getStringExtra("horaDiariamente")
        val horas = intent.getStringExtra("horaemhora")?.toIntOrNull() ?: 24 // Default para 24 horas

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Alarme que dispara o toque
        val alarmIntent = Intent(this, AlarmeToque::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            // Definindo a hora do alarme baseada na programação passada
            calendar.set(Calendar.HOUR_OF_DAY, horas)
            calendar.set(Calendar.MINUTE, 0)  // Configura minutos se necessário

            // Agendando o alarme que dispara o toque
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            // Adicionando a nova função: Abrir a Activity para desligar o alarme
            val desligaAlarmeIntent = Intent(this, DesligarAlarme::class.java)
            val desligaAlarmePendingIntent = PendingIntent.getActivity(
                this,
                1,  // Um requestCode diferente para distinguir este PendingIntent
                desligaAlarmeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Agendando a Activity que permitirá desligar o alarme
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                desligaAlarmePendingIntent
            )

            Toast.makeText(this, "Alarme agendado com sucesso!", Toast.LENGTH_SHORT).show()
            Log.d("AgendarAlarme", "Alarme agendado com sucesso!")
        } catch (e: SecurityException) {
            Log.e("AgendarAlarme", "SecurityException: ${e.message}")
            Toast.makeText(this, "Erro ao agendar o alarme: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                agendarAlarme()
            } else {
                Toast.makeText(this, "Permissões necessárias não foram concedidas.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
