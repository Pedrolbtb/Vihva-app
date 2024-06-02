package com.companyvihva.vihva.model.PopupRemedio

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.MyApplication
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.firestore.FirebaseFirestore

class DescriçãoRemedio : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private var remedio: Tipo_Classe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtém o ID do remédio do Intent
        val documentId = intent.getStringExtra("remedioId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Referências para os TextViews que exibirão o nome e a descrição do remédio
        nomeTextView = findViewById(R.id.nomere)
        descricaoTextView = findViewById(R.id.descricao1)

        // Busca os dados do Firebase usando o ID do remédio
        documentId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Configura o botão de adicionar remédio
        val btnAdd = findViewById<Button>(R.id.btn_add_remédio)
        btnAdd.setOnClickListener {
            // Verifica se o remédio não é nulo antes de adicioná-lo
            remedio?.let {
                // Adiciona o remédio selecionado usando o método no Application
                (application as MyApplication).onRemedioSelected(it)
                // Fecha a Activity de popup
                finish()
                // Exibe uma mensagem de sucesso
                Toast.makeText(this, "Remédio adicionado com sucesso", Toast.LENGTH_SHORT).show()
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
                    val foto = document.getString("foto")
                    // Cria um objeto Remedio2 com os dados obtidos do Firebase
                    remedio = Tipo_Classe(foto ?: "", nome ?: "")
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
}

