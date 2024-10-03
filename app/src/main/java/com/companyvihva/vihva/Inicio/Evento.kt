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

        // Inicializa o Spinner
        medicoSpinner = findViewById(R.id.medicoSpinner)

        carregarMedicos()

        val dateStr = intent.getStringExtra("selectedDate")
        selectedDate = parseDate(dateStr)

        val titleEditText = findViewById<EditText>(R.id.eventEditText)
        val descriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)

        saveButton.setOnClickListener {
            salvarEvento(titleEditText, descriptionEditText)
        }

        // Define o comportamento do botão de voltar
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

    private fun salvarEvento(titleEditText: EditText, descriptionEditText: EditText) {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val medicoNomeSelecionado = medicoSpinner.selectedItem as? medico_spinner
        val medicoUidSelecionado = medicoMap.entries.find { it.value == medicoNomeSelecionado?.nome }?.key

        // Verifica se o título e a descrição não estão vazios
        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Título e descrição não podem estar vazios.", Toast.LENGTH_SHORT).show()
            return
        }

        val event = hashMapOf(
            "titulo" to title,
            "descricao" to description,
            "data" to selectedDate,
            "medicoNome" to when {
                medicoNomeSelecionado?.nome == "Não adicionar médico" -> null
                else -> medicoNomeSelecionado?.nome
            },
            "medicoUid" to if (medicoNomeSelecionado?.nome == "Não adicionar médico") null else medicoUidSelecionado
        )

        // Adiciona log para verificar os dados que serão salvos
        Log.d("Evento", "Dados do evento: $event")

        val userId = auth.currentUser?.uid ?: return

        db.collection("clientes")
            .document(userId)
            .collection("eventos")
            .add(event)
            .addOnSuccessListener {
                Log.d("Evento", "Evento salvo com sucesso")
                // Aqui você pode adicionar lógica para agendar uma notificação
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Erro ao salvar evento", e)
                Toast.makeText(this, "Erro ao salvar evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("clientes").document(userId).get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>

                // Adiciona a opção "Não adicionar médico" primeiro
                listaMedicos.add(medico_spinner("Não adicionar médico", ""))

                if (medicosArray != null) {
                    medicosArray.forEach { medicoUid ->
                        db.collection("medicos").document(medicoUid).get()
                            .addOnSuccessListener { medicoDoc ->
                                val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                                val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                                val medico = medico_spinner(nomeMedico, imageUrl)
                                listaMedicos.add(medico)
                                medicoMap[medicoUid] = nomeMedico
                            }
                            .addOnFailureListener { e ->
                                Log.e("Evento", "Erro ao buscar médico $medicoUid", e)
                            }
                    }
                }

                // Atualiza o adapter do Spinner após adicionar todos os médicos
                val adapter = MedicoAdapter(this, listaMedicos)
                medicoSpinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Erro ao buscar médicos", e)
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