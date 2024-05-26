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
            "Insulina" -> listOf("tiazolidinedionas", "Inibidoresdaalfaglicosidase")
            "Metformina" -> listOf("metformina", "insulina")
            "sulfonilureias" -> listOf("insulina", "metformina", "insulina")
            "inibidoresdpp4" -> listOf("metformina", "insulina")
            "inibidoresdeSGLT2" -> listOf("insulina", "metformina")
            "agonistasdoGLP1" -> listOf("insulina", "metformina")
            "tiazolidinedionas" -> listOf("metformina", "insulina")
            "Inibidoresdaalfaglicosidase" -> listOf("insulina", "metformina")
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
                    val listanew = Listanew(url ?: "", nome ?: "")
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
