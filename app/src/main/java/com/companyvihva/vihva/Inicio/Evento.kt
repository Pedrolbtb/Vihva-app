package com.companyvihva.vihva.Evento

import MedicoAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.companyvihva.vihva.NotificationReceiver
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.medico_spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Evento : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedDate: Date
    private lateinit var medicoSpinner: Spinner
    private var listaMedicos: MutableList<medico_spinner> = mutableListOf()
    private var medicoMap: MutableMap<String, String> = mutableMapOf() // Map para armazenar UIDs e nomes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.evento_calender)

        // Inicializa o Firestore e FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inicializa o Spinner
        medicoSpinner = findViewById(R.id.medicoSpinner)

        // Carrega a lista de médicos do Firebase
        carregarMedicos()

        // Obtém a data selecionada da Intent
        val dateStr = intent.getStringExtra("selectedDate")
        selectedDate = parseDate(dateStr)

        // Encontra os componentes da interface do usuário
        val titleEditText = findViewById<EditText>(R.id.eventEditText)
        val descriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)

        // Define o comportamento do botão de salvar
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val medicoNomeSelecionado = medicoSpinner.selectedItem as? medico_spinner
            val medicoUidSelecionado = medicoMap.entries.find { it.value == medicoNomeSelecionado?.nome }?.key

            if (medicoUidSelecionado != null) {
                // Obtém o ID do usuário atual
                val userId = auth.currentUser?.uid ?: return@setOnClickListener

                // Cria um objeto de evento com os dados fornecidos
                val event = hashMapOf(
                    "titulo" to title,
                    "descricao" to description,
                    "data" to selectedDate,
                    "medicoNome" to medicoNomeSelecionado?.nome, // Salva o nome do médico selecionado
                    "medicoUid" to medicoUidSelecionado // Salva o UID do médico selecionado
                )

                // Salva o evento na coleção de eventos do usuário no Firestore
                db.collection("clientes")
                    .document(userId)
                    .collection("eventos")
                    .add(event)
                    .addOnSuccessListener {
                        Log.d("Evento", "Event saved successfully")

                        // Agenda uma notificação para o evento salvo
                        scheduleNotification(title, description, selectedDate)

                        // Cria um Intent para retornar um resultado de sucesso
                        val resultIntent = Intent().apply {
                            putExtra("evento", "Evento salvo com sucesso!")
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish() // Finaliza a atividade atual
                    }
                    .addOnFailureListener { e ->
                        Log.e("Evento", "Error saving event", e)
                        // Aqui você pode adicionar lógica para tratar falhas ao salvar
                    }
            } else {
                Log.e("Evento", "Selected doctor UID is null")
                // Tratamento para caso o médico não seja encontrado
            }
        }

        // Define o comportamento do botão de voltar
        backButton.setOnClickListener {
            Log.d("Evento", "Back button clicked")

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

        var currentUserUid: String? = "exemplo_uid"
        val btnCodigo = findViewById<ImageButton>(R.id.interrogacao) // Remova ImageButton
        btnCodigo.setOnClickListener {
            currentUserUid?.let { uid ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UID", uid)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    this, // Use apenas this
                    "Código do usuário copiado para área de transferência",
                    Toast.LENGTH_SHORT
                ).show()
                mostrarpopupcodigo(uid)
            }
        }
    }

    // Carrega a lista de médicos do Firebase
    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("clientes")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>
                if (medicosArray != null) {
                    val medicosList = mutableListOf<medico_spinner>()
                    medicosArray.forEach { medicoUid ->
                        db.collection("medicos").document(medicoUid).get().addOnSuccessListener { medicoDoc ->
                            val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                            val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                            val medico = medico_spinner(nomeMedico, imageUrl)
                            medicosList.add(medico)
                            medicoMap[medicoUid] = nomeMedico // Adiciona o UID e nome ao mapa

                            // Atualiza o adapter do Spinner
                            val adapter = MedicoAdapter(this, medicosList)
                            medicoSpinner.adapter = adapter
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Error loading doctors", e)
                // Trate o erro aqui, por exemplo, mostre uma mensagem de erro ao usuário
            }
    }

    // Converte uma string de data para um objeto Date
    private fun parseDate(dateStr: String?): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            format.parse(dateStr) ?: Date() // Retorna a data convertida ou a data atual se falhar
        } catch (e: Exception) {
            Log.e("Evento", "Error parsing date", e)
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

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Adiciona FLAG_IMMUTABLE
        )

        // Agenda a notificação para ser disparada na data e hora especificadas
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    @SuppressLint("MissingInflatedId")
    private fun mostrarpopupcodigo(uid: String) {
        val inflater = LayoutInflater.from(this)
        val builder = AlertDialog.Builder(this)
        val popupView = inflater.inflate(R.layout.popup_codigo, null)
        val btnCancelar = popupView.findViewById<ImageButton>(R.id.btnCancelar)
        val textViewCodigo = popupView.findViewById<TextView>(R.id.textView_codigo)

        textViewCodigo.text = uid

        builder.setView(popupView)
        val alertDialog = builder.create()
        alertDialog.show()

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}
