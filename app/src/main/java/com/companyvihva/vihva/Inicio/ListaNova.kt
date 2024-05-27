package com.companyvihva.vihva

import AdapterLista
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.model.Listanew
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class ListaNova : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterListaNova: AdapterLista
    private val dadosListaNova: MutableList<Listanew> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_nova_lista)

        firestore = FirebaseFirestore.getInstance()

        val remedioId = intent.getStringExtra("remedioId")

        val recyclerViewListaNova = findViewById<RecyclerView>(R.id.recyclerview_nova_lista)
        recyclerViewListaNova.layoutManager = LinearLayoutManager(this)
        recyclerViewListaNova.setHasFixedSize(true)

        adapterListaNova = AdapterLista(this, dadosListaNova)
        recyclerViewListaNova.adapter = adapterListaNova

        // Busca todos os dados e organiza conforme necessário
        remedioId?.let {
            fetchSubList(it)
        }
    }

    private fun fetchSubList(remedioId: String) {
        val subDocumentos = when (remedioId) {
            "Insulina" -> listOf("humalog", "novolog", "lantus", "levemir")
            "Metformina" -> listOf("glifage", "glucophage")
            "sulfonilureias" -> listOf("glibenclamida", "glipizida", "glimepirida")
            "inibidoresdpp4" -> listOf("sitagliptina","saxagliptina", "glinagliptina")
            "inibidoresdeSGLT2" -> listOf("daplaglifozina", "enpagueliflozina", "canaglifozina")
            "agonistasdoGLP1" -> listOf("exenatida", "Liraglutida", "dulaglutida")
            "tiazolidinedionas" -> listOf("pioglitazona", "rosiglitazona")
            "Inibidoresdaalfaglicosidase" -> listOf("acarbose", "Miglitol")

            // Adicione mais listas conforme necessário
            else -> emptyList()
        }

        for (docId in subDocumentos) {
            fetchRemedio(docId)
        }
    }

    private fun fetchRemedio(docId: String) {
        firestore.collection("remedios").document(docId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nome = document.getString("nome")
                    val url = document.getString("Url")
                    val tipo = document.getString("tipo")
                    val listanew = Listanew(url ?: "", nome ?: "", tipo?:"")

                    dadosListaNova.add(listanew)
                    adapterListaNova.notifyDataSetChanged()
                    Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }
}
