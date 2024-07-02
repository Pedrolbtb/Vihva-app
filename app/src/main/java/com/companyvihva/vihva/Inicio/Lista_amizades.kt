package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterListaAmigos
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Lista_amizades : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListaAmigos: AdapterListaAmigos
    private val listaAmizades: MutableList<Amigos> = mutableListOf()
    private lateinit var recyclerViewAmigos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_amizades)

        recyclerViewAmigos = findViewById(R.id.Lista_amigos)
        setupRecyclerView()

        db = FirebaseFirestore.getInstance()
        fetchAmigosDoUsuario()

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarAmizade)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        recyclerViewAmigos.layoutManager = LinearLayoutManager(this)
        recyclerViewAmigos.setHasFixedSize(true)
        adapterListaAmigos = AdapterListaAmigos(this, listaAmizades)
        recyclerViewAmigos.adapter = adapterListaAmigos
    }

    private fun fetchAmigosDoUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val amigosIds = document.get("medicos") as? List<String>
                        amigosIds?.forEach { amigoId ->
                            fetchDadosDoFirebase(amigoId)
                        }
                    } else {
                        Log.d("Lista_amizade", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Lista_amizades", "Erro ao obter documento do cliente", e)
                }
        }
    }

    private fun fetchDadosDoFirebase(amigoId: String) {
        if (amigoId.isNotEmpty()) {
            val docRef = db.collection("medicos").document(amigoId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val url = document.getString("imageUrl") ?: ""
                        val nome = document.getString("nome") ?: "Nome não disponível"
                        val amigo = Amigos(url, nome, amigoId)
                        atualizarListaAmigos(amigo)
                    } else {
                        Log.d("Lista_amizades", "Documento do amigo não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Lista_amizades", "Erro ao obter documento do amigo", e)
                }
        }
    }

    private fun atualizarListaAmigos(amigo: Amigos) {
        listaAmizades.add(amigo)
        adapterListaAmigos.notifyDataSetChanged()
    }
}
