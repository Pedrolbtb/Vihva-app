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
import com.companyvihva.vihva.NotificationReceiver
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.medico_spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Evento : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedDate: Date
    private lateinit var medicoSpinner: Spinner
    private var listaMedicos: MutableList<medico_spinner> = mutableListOf()
    private var medicoMap: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.evento_calender)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        medicoSpinner = findViewById(R.id.medicoSpinner)

        carregarMedicos()

        val dateStr = intent.getStringExtra("selectedDate")
        selectedDate = parseDate(dateStr)

        val titleEditText = findViewById<EditText>(R.id.eventEditText)
        val descriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val medicoNomeSelecionado = medicoSpinner.selectedItem as? medico_spinner
            val medicoUidSelecionado = if (medicoNomeSelecionado?.nome == "Não colocar médico") {
                null
            } else {
                medicoMap.entries.find { it.value == medicoNomeSelecionado?.nome }?.key
            }

            val userId = auth.currentUser?.uid ?: return@setOnClickListener
            val event = hashMapOf(
                "titulo" to title,
                "descricao" to description,
                "data" to selectedDate,
                "medicoNome" to medicoNomeSelecionado?.nome,
                "medicoUid" to medicoUidSelecionado
            )

            db.collection("clientes")
                .document(userId)
                .collection("eventos")
                .add(event)
                .addOnSuccessListener {
                    Log.d("Evento", "Event saved successfully")
                    scheduleNotification(title, description, selectedDate)

                    val resultIntent = Intent().apply {
                        putExtra("evento", "Evento salvo com sucesso!")
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("Evento", "Error saving event", e)
                }
        }

        backButton.setOnClickListener {
            Log.d("Evento", "Back button clicked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                finishAfterTransition()
            } else {
                finish()
            }
        }

        val btnCodigo = findViewById<ImageButton>(R.id.interrogacao)
        btnCodigo.setOnClickListener {
            val currentUserUid = auth.currentUser?.uid

            currentUserUid?.let { uid ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UID", uid)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Código do usuário copiado para área de transferência", Toast.LENGTH_SHORT).show()
                mostrarpopupcodigo(uid)
            } ?: run {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("clientes")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>
                val medicosList = mutableListOf<medico_spinner>()

                // Adicionar a opção "Não colocar médico"
                medicosList.add(medico_spinner("Não colocar médico", ""))

                if (medicosArray != null) {
                    medicosArray.forEach { medicoUid ->
                        db.collection("medicos").document(medicoUid).get().addOnSuccessListener { medicoDoc ->
                            val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                            val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                            val medico = medico_spinner(nomeMedico, imageUrl)
                            medicosList.add(medico)
                            medicoMap[medicoUid] = nomeMedico

                            // Atualiza o adapter uma única vez após carregar todos os médicos
                            if (medicosList.size == medicosArray.size + 1) {
                                val adapter = MedicoAdapter(this, medicosList)
                                medicoSpinner.adapter = adapter
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Error loading doctors", e)
            }
    }

    private fun parseDate(dateStr: String?): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            format.parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Log.e("Evento", "Error parsing date", e)
            Date()
        }
    }

    private fun scheduleNotification(title: String, description: String, date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 9)
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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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
