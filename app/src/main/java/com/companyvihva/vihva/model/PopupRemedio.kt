package com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.List



class PopupRemedio : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtém o ID do documento do Firebase passado através do Intent
        val documentId = intent.getStringExtra("remedioId")

        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Referências aos TextViews no layout

        nomeTextView = findViewById(R.id.nomere)
        descricaoTextView = findViewById(R.id.descricao1)

        // Se houver o ID do documento, busca os dados no Firebase
        documentId?.let { id ->
            fetchDadosDoFirebase(id)
        }
    }

    private fun fetchDadosDoFirebase(docId: String) {
        // Referência ao documento no Firebase
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtém os dados do documento
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")




                    // Define os dados nos componentes da interface do usuário
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao
                } else {
                    // Trata o caso em que o documento não existe
                    Log.d("PopupRemedio", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                // Trata falhas na obtenção de dados do Firebase
                Log.w("PopupRemedio", "Erro ao obter documento", e)
            }
    }
}