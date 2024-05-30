package com.companyvihva.vihva

import android.content.Intent
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

        val view = inflater.inflate(R.layout.remedio2, container, false)
        firestore = FirebaseFirestore.getInstance()

        val recyclerViewRemedios = view.findViewById<RecyclerView>(R.id.recyclerView_remedios)
        recyclerViewRemedios.layoutManager = LinearLayoutManager(context)
        recyclerViewRemedios.setHasFixedSize(true)

        adapterRemedio = AdapterRemedio(requireContext(), listaRemedios) { remedio ->
            val intent = Intent(requireContext(), ListaNova::class.java)
            intent.putExtra("remedioId", remedio.nome) // Passe o ID ou nome do remédio como extra
            startActivity(intent)
        }
        recyclerViewRemedios.adapter = adapterRemedio

        fetchRemedio(0)
        return view
        return inflater.inflate(R.layout.fragment_remedio, container, false)
    }

    private fun fetchRemedio(index: Int) {
        if (index < documentos.size) {
            val docId = documentos[index]
            firestore.collection("remedios").document(docId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nome = document.getString("nome")
                        val url = document.getString("Url")
                        val remedio = Remedio2(url ?: "", nome ?: "")
                        listaRemedios.add(remedio)
                        adapterRemedio.notifyDataSetChanged()
                        fetchRemedio(index + 1)
                    }
                }
                .addOnFailureListener {
                    fetchRemedio(index + 1)
                }
        }
    }
}
