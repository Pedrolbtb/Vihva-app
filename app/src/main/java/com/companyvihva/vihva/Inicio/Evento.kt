package com.companyvihva.vihva.Inicio

import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.NotificationReceiver
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Evento : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.evento_calender)

        // Inicializa o Firestore e FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtém a data selecionada da Intent
        val dateStr = intent.getStringExtra("selectedDate")
        selectedDate = parseDate(dateStr)

        // Encontra os componentes da interface do usuário
        val titleEditText = findViewById<EditText>(R.id.eventEditText)
        val descriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)
      val addmedico = findViewById<Button>(R.id.addmedico)
        // Define o comportamento do botão de salvar
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            // Obtém o ID do usuário atual
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            // Cria um objeto de evento com os dados fornecidos
            val event = hashMapOf(
                "titulo" to title,
                "descricao" to description,
                "data" to selectedDate
            )

            // Salva o evento na coleção de eventos do usuário no Firestore
            db.collection("clientes")
                .document(userId)
                .collection("eventos")
                .add(event)
                .addOnSuccessListener {
                    // Agenda uma notificação para o evento salvo
                    scheduleNotification(title, description, selectedDate)

                    // Cria um Intent para retornar um resultado de sucesso
                    val resultIntent = Intent().apply {
                        putExtra("evento", "Evento salvo com sucesso!")
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish() // Finaliza a atividade atual
                }
                .addOnFailureListener {
                    // Aqui você pode adicionar lógica para tratar falhas ao salvar
                }
        }

        // Define o comportamento do botão de voltar
        backButton.setOnClickListener {
            // Adiciona animação ao voltar para a tela anterior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(
                    this, R.anim.fade_in, R.anim.fade_out
                )
                finishAfterTransition()
            } else {
                finish()
            }
        }
    }

    // Converte uma string de data para um objeto Date
    private fun parseDate(dateStr: String?): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            format.parse(dateStr) ?: Date() // Retorna a data convertida ou a data atual se falhar
        } catch (e: Exception) {
            Date() // Retorna a data atual em caso de exceção
        }
    }

    // Agenda uma notificação para o evento
    private fun scheduleNotification(title: String, description: String, date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 9) // Configura a hora da notificação para 09:00
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("description", description)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Agenda a notificação para ser disparada na data e hora especificadas
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
