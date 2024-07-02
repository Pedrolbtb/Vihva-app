package com.companyvihva.vihva.Configuracoes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.SolicitacaoAmizade
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.SolicitacaoAmizadeAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ConfigNotificacoes : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SolicitacaoAmizadeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.rvSolicitacoesAmizade)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SolicitacaoAmizadeAdapter { solicitacao, aceito ->
            gerenciarSolicitacaoAmizade(solicitacao, aceito)
        }
        recyclerView.adapter = adapter

        carregarSolicitacoesAmizade()
    }

    private fun carregarSolicitacoesAmizade() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection(COLLECTION_SOLICITACOES)
            .whereEqualTo(FIELD_PACIENTE_ID, currentUserUid)
            .whereEqualTo(FIELD_STATUS, STATUS_PENDENTE)
            .get()
            .addOnSuccessListener { documents ->
                val solicitacoes = documents.map {
                    val solicitacao = it.toObject(SolicitacaoAmizade::class.java)
                    solicitacao.id = it.id
                    solicitacao.medicoId = it.getString(FIELD_MEDICO_ID) ?: ""
                    solicitacao.nomeSolicitante = it.getString("nome") ?: ""
                    Log.d("CarregarSolicitacoes", "ID: ${solicitacao.id}, Médico ID: ${solicitacao.medicoId}, Para: ${solicitacao.para}")
                    solicitacao
                }
                adapter.submitList(solicitacoes)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Error loading solicitations", e)
                Toast.makeText(this, "Erro ao carregar solicitações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gerenciarSolicitacaoAmizade(solicitacao: SolicitacaoAmizade, aceito: Boolean) {
        val docRef = db.collection(COLLECTION_SOLICITACOES).document(solicitacao.id)
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (aceito) {
            db.runTransaction { transaction ->
                // Atualiza o status da solicitação para "aceita"
                transaction.update(docRef, FIELD_STATUS, STATUS_ACEITA)

                // Verifique o valor de 'medicoId'
                val medicoId = solicitacao.medicoId
                if (medicoId.isNullOrEmpty()) {
                    throw IllegalStateException("Médico ID é nulo ou vazio: $medicoId")
                }
                Log.d("gerenciarSolicitacaoAmizade", "Médico ID: $medicoId")

                // Adiciona o médico ID ao array de médicos do usuário autenticado
                val userDocRef = db.collection(COLLECTION_CLIENTES).document(currentUserUid)
                transaction.update(userDocRef, FIELD_MEDICOS, FieldValue.arrayUnion(medicoId))

                // Cria uma nova amizade (se necessário)
                val amizade = mapOf(
                    "medico" to medicoId,
                    "paciente" to solicitacao.para
                )
                db.collection("amizades").add(amizade)
            }.addOnSuccessListener {
                atualizarLista()
            }.addOnFailureListener { e ->
                Log.e("FirebaseError", "Error accepting solicitation", e)
                Toast.makeText(this, "Erro ao aceitar a solicitação", Toast.LENGTH_SHORT).show()
            }
        } else {
            docRef.update(FIELD_STATUS, STATUS_REJEITADA)
                .addOnSuccessListener {
                    atualizarLista()
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseError", "Error rejecting solicitation", e)
                    Toast.makeText(this, "Erro ao rejeitar a solicitação", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun atualizarLista() {
        carregarSolicitacoesAmizade()
    }

    companion object {
        const val COLLECTION_SOLICITACOES = "solicitacoesAmizade"
        const val COLLECTION_CLIENTES = "clientes" // Nome da coleção de clientes
        const val FIELD_PACIENTE_ID = "pacienteId"
        const val FIELD_MEDICO_ID = "medicoId"
        const val FIELD_STATUS = "status"
        const val FIELD_MEDICOS = "medicos" // Novo campo para o array de médicos
        const val STATUS_PENDENTE = "pendente"
        const val STATUS_ACEITA = "aceita"
        const val STATUS_REJEITADA = "rejeitada"
    }
}
