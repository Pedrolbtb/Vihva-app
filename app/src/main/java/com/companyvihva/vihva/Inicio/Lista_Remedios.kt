package com.companyvihva.vihva

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class Lista_Remedios : AppCompatActivity() {

    // Declarando variáveis
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterListanovaNova: AdapterListanova
    private val dadosListaNova: MutableList<Tipo_Remedios> = mutableListOf()
    private val dadosListaNovaOriginal: MutableList<Tipo_Remedios> = mutableListOf() // Lista original sem filtro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_nova_lista)

        // Inicializando o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtendo o ID do remédio da intent
        val remedioId = intent.getStringExtra("remedioId")

        // Configurando o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarListaRe)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Encontrando o RecyclerView na view inflada e configurando-o
        val recyclerViewListaNova = findViewById<RecyclerView>(R.id.recyclerview_nova_lista)
        recyclerViewListaNova.layoutManager = LinearLayoutManager(this)
        recyclerViewListaNova.setHasFixedSize(true)

        // Inicializando e configurando o Adapter
        adapterListanovaNova = AdapterListanova(this, dadosListaNova)
        recyclerViewListaNova.adapter = adapterListanovaNova

        // Configurando o SearchView
        val searchView = findViewById<SearchView>(R.id.SearchRemedio2)
        setupSearchView(searchView)

        // Busca todos os dados e organiza conforme necessário
        remedioId?.let {
            fetchSubList(it)
        }
    }

    // Método para buscar sublistas de remédios com base no ID do remédio fornecido
    private fun fetchSubList(remedioId: String) {
        // Mapeamento do ID do remédio para seus subdocumentos correspondentes
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

        // Iterando sobre os subdocumentos para buscar os detalhes de cada remédio
        for (docId in subDocumentos) {
            fetchRemedio(docId)
        }
    }

    // Método para buscar detalhes de cada remédio com base no ID do documento
    private fun fetchRemedio(docId: String) {
        firestore.collection("remedios").document(docId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Obtendo os detalhes do remédio do documento Firestore
                    val nome = document.getString("nome")
                    val url = document.getString("Url")
                    val tipo = document.getString("tipo")

                    // Criando um objeto Tipo_Remedios com os detalhes do remédio
                    val tipoRemedios = Tipo_Remedios(url ?: "", nome ?: "", tipo ?: "", docId)

                    // Adicionando o remédio à lista e notificando o Adapter sobre a mudança
                    dadosListaNova.add(tipoRemedios)
                    dadosListaNovaOriginal.add(tipoRemedios) // Adiciona à lista original também
                    adapterListanovaNova.notifyDataSetChanged()

                    // Log para verificar se os dados estão sendo recebidos corretamente
                    Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                // Tratamento de falha na obtenção do documento Firestore
                Log.w("Firestore", "Error getting document", e)
            }
    }

    // Método para configurar o SearchView
    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.isEmpty()) {
                        // Restaurar lista original quando o texto é apagado
                        atualizarListaRemedios(dadosListaNovaOriginal)
                    } else {
                        searchRemedios(newText)
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            // Restaurar lista original quando o texto é fechado
            atualizarListaRemedios(dadosListaNovaOriginal)
            true
        }
    }

    // Método para filtrar os remédios com base na consulta do SearchView
    private fun searchRemedios(query: String) {
        val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
        val filteredList = dadosListaNovaOriginal.filter {
            it.nome.toLowerCase(Locale.ROOT).contains(lowerCaseQuery)
        }
        atualizarListaRemedios(filteredList)
    }

    // Método para atualizar a lista de remédios no Adapter
    private fun atualizarListaRemedios(lista: List<Tipo_Remedios>) {
        dadosListaNova.clear()
        dadosListaNova.addAll(lista)
        adapterListanovaNova.notifyDataSetChanged()
    }
}