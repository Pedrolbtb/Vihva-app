package com.companyvihva.vihva.Inicio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Alarme.EscolhaRemedio
import com.companyvihva.vihva.R
import com.companyvihva.vihva.Adapter_alarme
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_alarme

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Alarme : Fragment() {

    private lateinit var preferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter_alarme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_alarme, container, false)

        preferences = requireActivity().getSharedPreferences("vihva", Context.MODE_PRIVATE)
        recyclerView = rootView.findViewById(R.id.Lista_alarme)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Passando o contexto para o Adapter
        adapter = Adapter_alarme(emptyList(), requireContext())
        recyclerView.adapter = adapter

        fetchAlarmes()

        val addFoto = rootView.findViewById<ImageButton>(R.id.add_foto)
        addFoto.setOnClickListener {
            irParaEscolheRemedio()
        }

        loadPreference()

        return rootView
    }
    private fun fetchAlarmes() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("clientes").document(userId).collection("Alarmes")
            .get()
            .addOnSuccessListener { result ->
                val listaAlarmes = mutableListOf<tipo_alarme>()
                for (document in result) {
                    val descricao = document.getString("descricao") ?: "Descrição não disponível"
                    val nomeRemedio = document.getString("remedioId") ?: "Nome do remédio não disponivel"
                    val imageRemedio = document.getString("remedioId") ?: "Imagem do remédio não disponivel"
                    val frequencia = document.getString("frequencia") ?: "frequencia não disponivel"
                    listaAlarmes.add(tipo_alarme(descricao,nomeRemedio,frequencia ))


                }
                // Atualizando o adapter com a lista de alarmes
                adapter = Adapter_alarme(listaAlarmes, requireContext())
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                // Tratar falha de carregamento
            }
    }

    private fun saveData(hour: Int, minute: Int) {
        val dadosCliente = hashMapOf("hora" to hour, "minuto" to minute)
        val db = FirebaseFirestore.getInstance()
        db.collection("alarme").add(dadosCliente)
            .addOnSuccessListener { documentReference ->
                // Toast.makeText(requireContext(), "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Toast.makeText(requireContext(), "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPreference() {
        val hour = preferences.getInt("ddi", 0)
        val minutes = preferences.getLong("phone", 0)
        val mensagem = preferences.getString("text_msg_padrao", "")
        // Use esses valores conforme necessário
    }

    private fun irParaEscolheRemedio() {
        val telaCriaAlarme = Intent(requireActivity(), EscolhaRemedio::class.java)
        startActivity(telaCriaAlarme)
    }
}
