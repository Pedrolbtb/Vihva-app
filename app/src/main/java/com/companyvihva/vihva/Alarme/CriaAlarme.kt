package com.companyvihva.vihva.Alarme

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import android.Manifest
import android.content.Context
import java.util.*


class CriaAlarme : AppCompatActivity() {

    private var nome: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_alarme)

        // Inicializando as variáveis de intent dentro do onCreate
        val frequencia = intent.getStringExtra("frequencia")
        val horas = intent.getStringExtra("horaemhora")
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")
        val horaDiariamente = intent.getStringExtra("horaDiariamente")
        val estoque = intent.getStringExtra("estoque")
        val lembreme = intent.getStringExtra("lembreme")
        val tipomed = intent.getStringExtra("tipomed")
        val switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        val obsAlarme = intent.getStringExtra("OBS")
        nome = intent.getStringExtra("remedioId")

        val editnomeAlarme = findViewById<TextView>(R.id.layout_nome_alarme)
        nome?.let {
            editnomeAlarme.text = it
        }

        val editDescAlarme = findViewById<EditText>(R.id.edit_descAlarme)

        val descProgramacao = findViewById<TextView>(R.id.descprogramacao)
        if (frequencia != null && horas != null && duracao != null && data != null) {
            descProgramacao.text = "$frequencia - $horas - $duracao - $data"
        }

        val descEstoque = findViewById<TextView>(R.id.descEstoque)
        if (estoque != null || tipomed != null) {
            descEstoque.text = "$estoque $tipomed"
        }

        findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java).apply {
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("data", data)
                putExtra("duracao", duracao)
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horas)
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
                putExtra("horaemhora", horas)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("estoque", estoque)
                putExtra("remedioId", nome)
                putExtra("switchEstoque", switchEstoqueChecked)
            }
            startActivity(telaConfigEstoque)
        }

        findViewById<Button>(R.id.btn_salvarAlarme).setOnClickListener {
            agendarAlarme()
        }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val telaEscolhaRemedio = Intent(this, EscolhaRemedio::class.java).apply {}
            startActivity(telaEscolhaRemedio)
        }
    }

    private val PERMISSION_REQUEST_CODE = 100
    private val NOTIFICATION_ID = 1

    private fun agendarAlarme() {
        Log.d("AgendarAlarme", "Iniciando agendamento de alarme")

        val segundos = 5 // Definir o intervalo em segundos

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmeToque::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Verifica se é possível agendar alarmes exatos
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicita a permissão se não estiver concedida
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        try {
            // Configurar o horário inicial do alarme
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.SECOND, segundos) // Adiciona os segundos ao horário atual

            // Agendar o alarme que se repete
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, // Usa setExact para precisão exata
                calendar.timeInMillis,
                pendingIntent
            )

            // Criar e exibir a notificação quando o alarme tocar
            exibirNotificacao()

            Toast.makeText(this, "Alarme agendado para tocar em 5 segundos.", Toast.LENGTH_SHORT).show()

            Log.d("AgendarAlarme", "Alarme agendado com sucesso!")
        } catch (e: SecurityException) {
            // Handle SecurityException when trying to schedule exact alarms
            Log.e("AgendarAlarme", "SecurityException: ${e.message}")
            Toast.makeText(this, "Erro ao agendar o alarme: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exibirNotificacao() {
        // Criar e exibir a notificação quando o alarme tocar
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, AlarmeToque::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Alarme Disparado!")
            .setContentText("Seu alarme está tocando.")
            .setSmallIcon(R.drawable.ic_alarme) // Ícone da notificação
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true) // Fechar a notificação quando clicar nela
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder)
    }

    /*private fun adicionarIntentsAoRemedio(remedioId: String) {
        // Obter o UID do usuário logado
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            // Referência ao documento do cliente no Firestore usando o UID do usuário
            val clientRef = FirebaseFirestore.getInstance().collection("clientes").document(uid)

            // Obter os dados das intents
            val frequencia = intent.getStringExtra("frequencia")
            val horas = intent.getStringExtra("horaemhora")
            val duracao = intent.getStringExtra("duracao")
            val data = intent.getStringExtra("data")
            val horaDiariamente = intent.getStringExtra("horaDiariamente")
            val estoque = intent.getStringExtra("estoque")
            val lembreme = intent.getStringExtra("lembreme")
            val tipomed = intent.getStringExtra("tipomed")
            val switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
            val obsAlarme = intent.getStringExtra("OBS")

            // Incluir a ID do remédio no nome das intents
            val nomeIntent = "$remedioId - $frequencia - $horas - $duracao - $data"

            // Criar um mapa com os dados das intents
            val dadosIntents = hashMapOf(
                "nome" to nomeIntent,
                "frequencia" to frequencia,
                "horas" to horas,
                "duracao" to duracao,
                "data" to data,
                "horaDiariamente" to horaDiariamente,
                "estoque" to estoque,
                "lembreme" to lembreme,
                "tipomed" to tipomed,
                "switchEstoqueChecked" to switchEstoqueChecked,
                "obsAlarme" to obsAlarme
            )

            // Atualizar a lista de intents do remédio do cliente
            clientRef.update("remedios.$remedioId", FieldValue.arrayUnion(remedioId))
                .addOnSuccessListener {
                    // Exibe uma mensagem de sucesso
                    Toast.makeText(this, "Intents adicionadas com sucesso", Toast.LENGTH_SHORT).show()
                    // Fecha a Activity de criação de alarme
                    finish()
                }
                .addOnFailureListener { e ->
                    // Trata falhas na atualização do banco de dados
                    Log.w("CriaAlarme", "Erro ao adicionar intents", e)
                    Toast.makeText(this, "Erro ao adicionar intents: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Se não conseguir obter o UID do usuário logado
            Toast.makeText(this, "Erro: usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }*/

}
