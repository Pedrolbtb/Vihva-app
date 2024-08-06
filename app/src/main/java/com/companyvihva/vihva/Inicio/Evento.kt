package com.companyvihva.vihva.Inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtenha a data selecionada passada pela Intent
        val dateStr = intent.getStringExtra("selectedDate")
        selectedDate = parseDate(dateStr)

        val titleEditText = findViewById<EditText>(R.id.ev)
        val descriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<Button>(R.id.btnVoltar)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            // Cria um mapa com os dados do evento
            val event = hashMapOf(
                "title" to title,
                "description" to description,
                "date" to selectedDate
            )

            // Salva o evento diretamente no documento do usuário
            db.collection("clientes")
                .document(userId)
                .update("events", FieldValue.arrayUnion(event))
                .addOnSuccessListener {
                    val resultIntent = Intent().apply {
                        putExtra("event", "Evento salvo com sucesso!")
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener {
                    // Tratar falha ao salvar
                }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun parseDate(dateStr: String?): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            format.parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }
}