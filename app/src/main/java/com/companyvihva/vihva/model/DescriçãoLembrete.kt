package com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterListaAmigos
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DescriçãoLembrete : AppCompatActivity() {

    private lateinit var tituloTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var recyclerViewMedicos: RecyclerView
    private lateinit var adapterListaAmigos: AdapterListaAmigos

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val listaMedicos: MutableList<Amigos> = mutableListOf() // Lista para médicos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_lembrete)

        tituloTextView = findViewById(R.id.titulo)
        descricaoTextView = findViewById(R.id.descricao_lembrete)
        recyclerViewMedicos = findViewById(R.id.lista_medicos)

        // Configura o RecyclerView
        recyclerViewMedicos.layoutManager = LinearLayoutManager(this)
        recyclerViewMedicos.setHasFixedSize(true)

        adapterListaAmigos = AdapterListaAmigos(this, listaMedicos)
        recyclerViewMedicos.adapter = adapterListaAmigos

        // Botão de voltar
        findViewById<ImageButton>(R.id.btn_voltarDO).setOnClickListener {
            finish()
        }

        // Recupera o ID do lembrete passado via Intent
        val eventoId = intent.getStringExtra("eventoId")

        eventoId?.let { buscarLembrete(it) } ?: run {
            tituloTextView.text = "Erro: ID do lembrete não encontrado."
            descricaoTextView.text = ""
        }

        // Botão de excluir
        findViewById<ImageButton>(R.id.lixeira_doencas).setOnClickListener {
            eventoId?.let { id -> showConfirmDeleteDialog(id) }
        }
    }

    private fun buscarLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("eventos")
            .document(eventoId)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val titulo = document.getString("titulo") ?: "Título não disponível"
                val descricao = document.getString("descricao") ?: "Descrição não disponível"
                val medicosIds = document.get("medicosUid") as? List<String> ?: emptyList()

                tituloTextView.text = titulo
                descricaoTextView.text = descricao

                buscarMedicos(medicosIds)
            } else {
                tituloTextView.text = "Documento não encontrado."
                descricaoTextView.text = ""
            }
        }.addOnFailureListener { exception ->
            tituloTextView.text = "Erro ao carregar o lembrete."
            descricaoTextView.text = ""
            Log.w("DescriçãoLembrete", "Erro ao acessar o Firestore", exception)
        }
    }

    private fun buscarMedicos(medicosIds: List<String>) {
        listaMedicos.clear() // Limpa a lista antes de adicionar novos médicos
        adapterListaAmigos.notifyDataSetChanged()

        if (medicosIds.isEmpty()) {
            // Caso não haja médicos para exibir
            return
        }

        // Faz a busca dos documentos dos médicos em paralelo
        val batchSize = 10 // Define o tamanho máximo do batch
        val batches = medicosIds.chunked(batchSize)

        batches.forEach { batch ->
            val batchRef = firestore.batch()
            val batchRequests = batch.map { medicoId ->
                firestore.collection("medicos").document(medicoId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val nome = document.getString("nome") ?: "Nome não disponível"
                            val crm = document.getString("crm") ?: "CRM não disponível"
                            val foto = document.getString("imageUrl") ?: ""
                            val medico = Amigos(foto, nome, medicoId, crm)
                            listaMedicos.add(medico)
                            adapterListaAmigos.notifyDataSetChanged()
                        } else {
                            Log.d("DescriçãoLembrete", "Documento do médico não encontrado")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("DescriçãoLembrete", "Erro ao buscar médico", e)
                    }
            }

            // Execute todos os requests do batch
            batchRequests.forEach { it }
        }
    }

    private fun showConfirmDeleteDialog(eventoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este lembrete? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ -> deleteLembrete(eventoId) }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    private fun deleteLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("eventos")
            .document(eventoId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Lembrete excluído com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescriçãoLembrete", "Lembrete excluído com sucesso")
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoLembrete", "Erro ao excluir o lembrete", e)
                Toast.makeText(this, "Erro ao excluir o lembrete", Toast.LENGTH_SHORT).show()
            }
    }
}
