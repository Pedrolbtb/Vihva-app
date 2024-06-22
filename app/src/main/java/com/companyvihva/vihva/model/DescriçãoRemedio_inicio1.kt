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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio_inicio1)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtém o ID do remédio do Intent
        remedioId = intent.getStringExtra("remedioId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Referências para os TextViews que exibirão o nome e a descrição do remédio
        nomeTextView = findViewById(R.id.nomere)
        descricaoTextView = findViewById(R.id.descricao1)
        urlImageView = findViewById(R.id.foto_Remedio)

        // Busca os dados do Firebase usando o ID do remédio
        remedioId?.let { id ->
            fetchDadosDoFirebase(id)
        }
    }

    // Método para buscar os dados do remédio no Firebase
    private fun fetchDadosDoFirebase(docId: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            // Primeiro, obtemos os IDs dos remédios associados ao usuário
            val clientRef = firestore.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val remediosIds = document.get("remedios") as? List<String>
                        remediosIds?.let { ids ->
                            // Verifica se o ID do remédio buscado está presente na lista de remédios do usuário
                            if (ids.contains(docId)) {
                                // Busca os detalhes do remédio na coleção "remedios"
                                val docRef = firestore.collection("remedios").document(docId)
                                docRef.get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val nome = document.getString("nome")
                                            val descricao = document.getString("descricao")
                                            val url = document.getString("Url")

                                            // Carrega a imagem usando o Picasso se a URL estiver disponível
                                            url?.let {
                                                Picasso.get().load(it).into(urlImageView)
                                            }

                                            // Atualiza os TextViews com o nome e a descrição do remédio
                                            nomeTextView.text = nome
                                            descricaoTextView.text = descricao
                                        } else {
                                            Log.d("DescriçãoRemedio_inicio1", "Documento do remédio não encontrado")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("DescriçãoRemedio_inicio1", "Erro ao obter documento do remédio", e)
                                    }
                            } else {
                                Log.d("DescriçãoRemedio_inicio1", "O remédio com ID $docId não está associado ao usuário")
                            }
                        }
                    } else {
                        Log.d("DescriçãoRemedio_inicio1", "Documento do usuário não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("DescriçãoRemedio_inicio1", "Erro ao obter documento do usuário", e)
                }
            }
        }
}