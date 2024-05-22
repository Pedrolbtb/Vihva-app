package com.companyvihva.vihva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.Remedio2
import com.google.firebase.firestore.FirebaseFirestore

class Remedio1 : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterRemedio: AdapterRemedio
    private val listaRemedios: MutableList<Remedio2> = mutableListOf()
    private val documentos = listOf(
        "insulina",
        "metformina",
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
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.remedio2, container, false)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configura o RecyclerView
        val recyclerViewRemedios = view.findViewById<RecyclerView>(R.id.recyclerView_remedios)
        recyclerViewRemedios.layoutManager = LinearLayoutManager(context)
        recyclerViewRemedios.setHasFixedSize(true)

        adapterRemedio = AdapterRemedio(requireContext(), listaRemedios)
        recyclerViewRemedios.adapter = adapterRemedio

        // Inicia a busca sequencial de documentos
        fetchRemedio(0) // Alteração feita: chamada do método fetchRemedio(0) para iniciar a busca dos remédios

        return view
    }

    // Novo método adicionado para buscar os remédios no Firestore
    private fun fetchRemedio(index: Int) {
        // Verifica se o índice fornecido está dentro dos limites do array documentos
        if (index < documentos.size) {
            // Obtém o ID do documento no índice especificado pelo parâmetro index do array documentos
            val docId = documentos[index]

            // Acessa a coleção "remedios" no Firestore e recupera o documento correspondente ao ID
            firestore.collection("remedios").document(docId).get()
                .addOnSuccessListener { document ->
                    // Verifica se o documento não é nulo
                    if (document != null) {
                        // Extrai o valor do campo "nome" do documento
                        val nome = document.getString("nome")

                        // Extrai o valor do campo "Url" do documento
                        val url = document.getString("Url")
                        // Cria um objeto Remedio2 com os dados obtidos
                        val remedio = Remedio2(
                            url ?: "", // Se a URL for nula, atribui uma string vazia
                            nome ?: "" // Se o nome for nulo, atribui uma string vazia
                        )

                        // Adiciona o remédio à lista
                        listaRemedios.add(remedio)

                        // Notifica o adaptador sobre as mudanças nos dados
                        adapterRemedio.notifyDataSetChanged()

                        // Busca o próximo remédio
                        fetchRemedio(index + 1)
                    }
                }
                .addOnFailureListener { exception ->
                    // Trata o erro, se houver
                    fetchRemedio(index + 1) // Tenta buscar o próximo remédio mesmo em caso de falha
                }
        }
    }
}
