package com.companyvihva.vihva

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.firestore.FirebaseFirestore

class Classes_Remedio : Fragment() {

    // Declarando variáveis
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterRemedio: AdapterRemedio
    private val listaRemedios: MutableList<Tipo_Classe> = mutableListOf()
    private val listaRemediosFiltrados: MutableList<Tipo_Classe> = mutableListOf()
    private lateinit var searchView: SearchView
    private val documentos = listOf(
        "insulina",
        "metformina",
        "Antidepressivos",
        "sulfonilureias",
        "inibidoresdpp4",
        "inibidoresdeSGLT2",
        "agonistasdoGLP1",
        "tiazolidinedionas",
        "Inibidoresdaalfaglicosidase"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflando o layout do fragmento
        val view = inflater.inflate(R.layout.remedio2, container, false)

        // Inicializando o Firestore
        firestore = FirebaseFirestore.getInstance()
        // Encontrando o RecyclerView na view inflada e configurando-o
        val recyclerViewRemedios = view.findViewById<RecyclerView>(R.id.recyclerView_remedios)
        recyclerViewRemedios.layoutManager = LinearLayoutManager(context)
        recyclerViewRemedios.setHasFixedSize(true)

        // Inicializando e configurando o Adapter
        adapterRemedio = AdapterRemedio(requireContext(), listaRemediosFiltrados)
        { remedio ->
            val intent = Intent(requireContext(), Lista_Remedios::class.java)
            intent.putExtra("remedioId", remedio.nome) // Passe o ID ou nome do remédio como extra
            startActivity(intent)
        }
        recyclerViewRemedios.adapter = adapterRemedio

        // Inicializando e configurando a SearchView
        searchView = view.findViewById(R.id.SearchRemedio)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listaRemediosFiltrados.clear()
                if (newText.isNullOrBlank()) {
                    listaRemediosFiltrados.addAll(listaRemedios)
                } else {
                    val searchText = newText.lowercase()
                    listaRemediosFiltrados.addAll(
                        listaRemedios.filter {
                            it.nome.lowercase().contains(searchText)
                        }
                    )
                }
                adapterRemedio.notifyDataSetChanged()
                return true
            }
        })

        // Chamando a função para buscar os remédios
        fetchRemedio(0)

        // Retornando a view inflada
        return view
    }

    // Função para buscar os remédios do Firestore
    private fun fetchRemedio(index: Int) {
        if (index < documentos.size) {
            val docId = documentos[index]
            firestore.collection("remedios").document(docId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtendo os detalhes do remédio do documento Firestore
                        val nome = document.getString("nome")
                        val url = document.getString("Url")
                        val remedio = Tipo_Classe(url ?: "", nome ?: "","")

                        // Adicionando o remédio à lista e notificando o Adapter sobre a mudança
                        listaRemedios.add(remedio)
                        listaRemediosFiltrados.add(remedio)
                        adapterRemedio.notifyDataSetChanged()

                        // Chamando recursivamente a função para buscar o próximo remédio
                        fetchRemedio(index + 1)
                    }
                }
                .addOnFailureListener {
                    // Em caso de falha, chamando recursivamente a função para buscar o próximo remédio
                    fetchRemedio(index + 1)
                }
        }
    }
}