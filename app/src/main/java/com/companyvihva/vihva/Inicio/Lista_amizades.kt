package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterListaAmigos
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView
import androidx.core.text.isDigitsOnly

class Lista_amizades : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListaAmigos: AdapterListaAmigos
    private val listaAmizades: MutableList<Amigos> = mutableListOf()
    private val listaAmizadesOriginal: MutableList<Amigos> = mutableListOf() // Lista original sem filtro
    private lateinit var recyclerViewAmigos: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_amizades)

        recyclerViewAmigos = findViewById(R.id.Lista_amigos)
        searchView = findViewById(R.id.SearchView)
        setupRecyclerView()

        db = FirebaseFirestore.getInstance()

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarAmizade)
        btnVoltar.setOnClickListener {
            finish()
        }

        setupSearchView()
    }

    override fun onResume() {
        super.onResume()
        fetchAmigosDoUsuario()
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

        listaAmizades.clear() // Limpa a lista antes de adicionar novos itens
        listaAmizadesOriginal.clear()
        adapterListaAmigos.notifyDataSetChanged()

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
                        val crm = document.getString("crm") ?: "CRM não disponível"
                        val amigo = Amigos(url, nome, amigoId, crm)
                        listaAmizades.add(amigo)
                        listaAmizadesOriginal.add(amigo) // Adiciona à lista original também
                        adapterListaAmigos.notifyDataSetChanged()
                    } else {
                        Log.d("Lista_amizades", "Documento do amigo não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Lista_amizades", "Erro ao obter documento do amigo", e)
                }
        }
    }

    private fun atualizarListaAmigos(lista: List<Amigos>) {
        listaAmizades.clear()
        listaAmizades.addAll(lista)
        adapterListaAmigos.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.isEmpty()) {
                        // Restaurar lista original quando o texto é apagado
                        atualizarListaAmigos(listaAmizadesOriginal)
                    } else {
                        searchAmigo(newText)
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            // Restaurar lista original quando o texto é fechado
            atualizarListaAmigos(listaAmizadesOriginal)
            true
        }
    }

    private fun searchAmigo(query: String) {
        val searchResult = mutableListOf<Amigos>()

        // Verifica se o texto digitado é numérico (considerando CRM)
        if (query.isDigitsOnly()) {
            searchAmigoPorCrm(query)
        } else {
            searchAmigoPorNome(query)
        }
    }

    private fun searchAmigoPorCrm(crm: String) {
        db.collection("medicos")
            .whereEqualTo("crm", crm)
            .get()
            .addOnSuccessListener { documents ->
                val amigos = mutableListOf<Amigos>()
                for (document in documents) {
                    val url = document.getString("imageUrl") ?: ""
                    val amigoNome = document.getString("nome") ?: "Nome não disponível"
                    val amigoId = document.id
                    val crm = document.getString("crm") ?: "CRM não disponível"
                    val amigo = Amigos(url, amigoNome, amigoId, crm)
                    amigos.add(amigo)
                }
                atualizarListaAmigos(amigos)
            }
            .addOnFailureListener { e ->
                Log.w("Lista_amizades", "Erro ao buscar amigo por CRM", e)
                Toast.makeText(this, "Erro ao buscar amigo por CRM", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchAmigoPorNome(nome: String) {
        db.collection("medicos")
            .orderBy("nome")
            .startAt(nome)
            .endAt(nome + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val amigos = mutableListOf<Amigos>()
                for (document in documents) {
                    val url = document.getString("imageUrl") ?: ""
                    val amigoNome = document.getString("nome") ?: "Nome não disponível"
                    val amigoId = document.id
                    val crm = document.getString("crm") ?: "CRM não disponível"
                    val amigo = Amigos(url, amigoNome, amigoId, crm)
                    amigos.add(amigo)
                }
                atualizarListaAmigos(amigos)
            }
            .addOnFailureListener { e ->
                Log.w("Lista_amizades", "Erro ao buscar amigos por nome", e)
                Toast.makeText(this, "Erro ao buscar amigos por nome", Toast.LENGTH_SHORT).show()
            }
    }
}