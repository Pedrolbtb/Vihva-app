package com.companyvihva.vihva

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.firestore.FirebaseFirestore

class Lista_Remedios : AppCompatActivity(), AdapterListanova.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterListanovaNova: AdapterListanova
    private val dadosListaNova: MutableList<Tipo_Remedios> = mutableListOf()

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

        adapterListanovaNova = AdapterListanova(this, dadosListaNova, this)
        recyclerViewListaNova.adapter = adapterListanovaNova

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

                    val tipoRemedios = Tipo_Remedios(url ?: "", nome ?: "", tipo ?: "", docId)

                    dadosListaNova.add(tipoRemedios)
                    adapterListanovaNova.notifyDataSetChanged()

                    Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }

    override fun onItemClick(remedioId: String) {

    }

}
