package com.companyvihva.vihva

import AdapterLista
import Inicio1
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.model.Listanew
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.ImageButton
import com.companyvihva.vihva.Inicio.Calendario
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.model.Remedio2
import kotlin.collections.emptyList
import kotlin.collections.List

class ListaNova : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterListaNova: AdapterLista
    private val dadosListaNova: MutableList<Listanew> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_nova_lista)

        firestore = FirebaseFirestore.getInstance()

        val remedioId = intent.getStringExtra("remedioId")
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarListaRe)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

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
            "Insulina" -> listOf("humalog", "novolog", "lantus", "levemir1")
            "Metformina" -> listOf("glifage", "glucophage")
            "sulfonilureias" -> listOf("glibenclamida", "glipizida", "glimepirida")
            "Inibidores Dpp4" -> listOf("sitagliptina", "saxagliptina1", "glinagliptina")
            "Inibidores de SGLT2" -> listOf("dapagliflozina1", "empagliflozina ", "canagliflozina ")
            "Agonistas do GLP1" -> listOf("exenatida", "liraglutida1", "dulaglutida1")
            "Tiazolidinedionas" -> listOf("pioglitazona1", "rosiglitazona1")
            "Inibidores de alfa glicosidase" -> listOf("acarbose1", "miglitol1")
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
                    val listanew = Listanew(url ?: "", nome ?: "", tipo ?: "", docId)

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