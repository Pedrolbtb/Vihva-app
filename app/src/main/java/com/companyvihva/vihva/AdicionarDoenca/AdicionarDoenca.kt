package com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterDoenca
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.firestore.FirebaseFirestore

class AdicionarDoenca : AppCompatActivity() {
    private lateinit var adapterAdicionar: AdapterDoenca
    private val listaAdicionarRemedio: MutableList<Tipo_Remedios> = mutableListOf()
    private lateinit var firestore: FirebaseFirestore
    private val documentos = listOf(
        "diabetes",
        "diabetes_tipo_dois"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_doenca)

        // Inicializando o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurando RecyclerView
        val recyclerviewAdicionar = findViewById<RecyclerView>(R.id.recyclerview_adicionar)
        recyclerviewAdicionar.layoutManager = LinearLayoutManager(this)
        recyclerviewAdicionar.setHasFixedSize(true)

        // Inicializando o adapter
        adapterAdicionar = AdapterDoenca(this, listaAdicionarRemedio)
        recyclerviewAdicionar.adapter = adapterAdicionar

        // Botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltarAddDoenca)
        btnVoltar.setOnClickListener {
            finish()
        }

        // Chamando a função para buscar os remédios
        fetchRemedio(0)
    }

    private fun fetchRemedio(index: Int) {
        if (index < documentos.size) {
            val docId = documentos[index]
            firestore.collection("doenca").document(docId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtendo os detalhes do remédio do documento Firestore
                        val nome = document.getString("nome")
                        val url = document.getString("Url")
                        val tipo = document.getString("tipo")

                        // Criando um objeto Tipo_Remedios com os detalhes do remédio
                        val tipoRemedios = Tipo_Remedios(url ?: "", nome ?: "", tipo ?: "", docId)

                        // Adicionando o remédio à lista e notificando o Adapter sobre a mudança
                        listaAdicionarRemedio.add(tipoRemedios)
                        adapterAdicionar.notifyDataSetChanged()

                        // Log para verificar se os dados estão sendo recebidos corretamente
                        Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                    }
                }
                .addOnFailureListener { e ->
                    // Tratamento de falha na obtenção do documento Firestore
                    Log.w("Firestore", "Error getting document", e)
                }
                .addOnCompleteListener {
                    // Chamar a função recursivamente para o próximo documento
                    fetchRemedio(index + 1)
                }
        }
    }
}