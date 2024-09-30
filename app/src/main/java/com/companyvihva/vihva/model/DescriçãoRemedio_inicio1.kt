package com.companyvihva.vihva.com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DescriçãoRemedio_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var urlImageView: ImageView
    private lateinit var textViewCalendario: TextView // Initialize textViewCalendario
    private var remedioId: String? = null
    private var formattedDate: String? = null // To store selected date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio_inicio1)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get the medicine ID passed by the Intent
        remedioId = intent.getStringExtra("remedioId")

        // Initialize the views
        nomeTextView = findViewById(R.id.nomere)
        urlImageView = findViewById(R.id.foto_Remedio)
        textViewCalendario = findViewById(R.id.textView_receitado) // Initialize here

        // Fetch medicine data from Firebase using the ID
        remedioId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Set up the back button
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Set up the delete button
        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_remedios)
        btnExcluir.setOnClickListener {
            remedioId?.let { id ->
                showConfirmDeleteDialog(id)
            }
        }

        // Set up the date picker for prescription date
        textViewCalendario.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione quando este remédio foi prescrito")
                .build()
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDate = sdf.format(Date(selectedDate))
                textViewCalendario.text = formattedDate
            }
            datePicker.show(supportFragmentManager, "DatePicker")
        }
    }

    // Method to fetch medicine data from Firebase
    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val url = document.getString("Url")
                    val nome = document.getString("nome")
                    // Load the image using Picasso if the URL is available
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    // Update TextViews with medicine data
                    nomeTextView.text = nome
                } else {
                    Log.d("DescricaoRemedioInicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoRemedioInicio1", "Erro ao obter documento", e)
            }
    }

    // Method to show the confirmation dialog for deletion
    private fun showConfirmDeleteDialog(remedioId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir este remédio? Você pode adicioná-lo novamente na lista de remédios")
            setPositiveButton("Sim") { _, _ ->
                deleteRemedioArray(remedioId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Method to delete the medicine from the user's array in Firebase
    private fun deleteRemedioArray(remedioId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("remedios", FieldValue.arrayRemove(remedioId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Remédio excluído do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescricaoRemedioInicio1", "Remédio removido do array com sucesso")
                    // Go back to the previous activity after deletion
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescricaoRemedioInicio1", "Erro ao remover remédio do array", e)
                }
        }
    }
}
