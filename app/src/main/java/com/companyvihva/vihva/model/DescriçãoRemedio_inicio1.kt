package com.companyvihva.vihva.com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Classe
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso




class DescriçãoRemedio_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var remedioId: String? = null
    private var remedioNome: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio_inicio1)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtém o ID e o nome do remédio passados pelo Intent
        remedioId = intent.getStringExtra("remedioId")


        // Inicializa as views
        nomeTextView = findViewById(R.id.nomere)
        descricaoTextView = findViewById(R.id.descricao1)
        urlImageView = findViewById(R.id.foto_Remedio)

        // Exibe o nome do remédio


        // Busca os dados do remédio no Firebase usando o ID
        remedioId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }
    }

    // Método para buscar os dados do remédio no Firebase
    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")
                    val nome = document.getString("nome")
                    // Carrega a imagem usando o Picasso se a URL estiver disponível
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    //nome
                    nomeTextView.text = nome
                    // Atualiza o TextView com a descrição do remédio
                    descricaoTextView.text = descricao
                } else {
                    Log.d("DescriçãoRemedio_inicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoRemedio_inicio1", "Erro ao obter documento", e)
            }
    }
}