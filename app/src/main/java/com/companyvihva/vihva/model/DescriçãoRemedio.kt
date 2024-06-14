package com.companyvihva.vihva.model.PopupRemedio

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DescriçãoRemedio : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var remedio: Tipo_Classe? = null
    private var remedioId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio)

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

        // Configura o botão de adicionar remédio
        val btnAdd = findViewById<Button>(R.id.btn_add_remédio)
        btnAdd.setOnClickListener {
            // Adicionar remédio ao banco de dados do cliente autenticado
            remedioId?.let { id ->
                adicionarRemedioAoCliente(id)
            }
        }
    }

    // Método para buscar os dados do remédio no Firebase
    private fun fetchDadosDoFirebase(docId: String) {
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

                    // Cria um objeto Tipo_Classe com os dados obtidos do Firebase
                    remedio = Tipo_Classe(url ?: "", nome ?: "", descricao ?: "")

                    // Atualiza os TextViews com o nome e a descrição do remédio
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao
                } else {
                    Log.d("PopupRemedio", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupRemedio", "Erro ao obter documento", e)
            }
    }

    // Método para adicionar o ID do remédio ao banco de dados do cliente autenticado
    private fun adicionarRemedioAoCliente(remedioId: String) {
        // Obter o UID do usuário logado
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            // Referência ao documento do cliente no Firestore usando o UID do usuário
            val clientRef = firestore.collection("clientes").document(uid)

            // Atualizar a lista de IDs de remédios do cliente
            clientRef.update("remedios", FieldValue.arrayUnion(remedioId))
                .addOnSuccessListener {
                    // Exibe uma mensagem de sucesso
                    Toast.makeText(this, "Remédio adicionado com sucesso", Toast.LENGTH_SHORT).show()
                    // Fecha a Activity de popup
                    finish()
                }
                .addOnFailureListener { e ->
                    // Trata falhas na atualização do banco de dados
                    Log.w("DescriçãoRemedio", "Erro ao adicionar remédio", e)
                    Toast.makeText(this, "Erro ao adicionar remédio: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Se não conseguir obter o UID do usuário logado
            Toast.makeText(this, "Erro: usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}
