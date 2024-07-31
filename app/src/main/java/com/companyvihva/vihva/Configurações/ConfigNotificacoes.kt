package com.companyvihva.vihva.Configuracoes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.SolicitacaoAmizadeAdapter
import com.companyvihva.vihva.com.companyvihva.vihva.model.SolicitacaoAmizade
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

        // Solicitar permissão de notificação se necessário
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Permissão","Chegou")
                // Permissão concedida
                notificacaoAmizade()
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarSolicitacoesAmizade() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection(COLLECTION_SOLICITACOES)
            .whereEqualTo(FIELD_PACIENTE_ID, currentUserUid)
            .whereEqualTo(FIELD_STATUS, STATUS_PENDENTE)
            .get()
            .addOnSuccessListener { documents ->
                val solicitacoes = documents.map { document ->
                    val solicitacao = document.toObject(SolicitacaoAmizade::class.java)
                    solicitacao.id = document.id
                    solicitacao.medicoId = document.getString(FIELD_MEDICO_ID) ?: ""

                    // Obter nome, sobrenome e foto do solicitante (médico)
                    val medicoId = solicitacao.medicoId
                    db.collection("medicos").document(medicoId).get()
                        .addOnSuccessListener { medicoDoc ->
                            solicitacao.nomeSolicitante = medicoDoc.getString("nome") ?: ""
                            solicitacao.sobrenomeSolicitante = medicoDoc.getString("sobrenome") ?: ""
                            solicitacao.fotoSolicitante = medicoDoc.getString("imageUrl") ?: ""
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseError", "Error loading medico info", e)
                        }
                    solicitacao
                }
                adapter.submitList(solicitacoes)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Error loading solicitations", e)
                Toast.makeText(this, "Erro ao carregar solicitações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun notificacaoAmizade() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissão não concedida, não faça nada ou log um erro
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val channelId = "channel_id"

        // Criar o canal de notificação para Android 8.0 e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Solicitações de Amizade",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para solicitações de amizade"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir a Activity quando a notificação for clicada
        val notificationIntent = Intent(this, ConfigNotificacoes::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construção da notificação
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.sininho)
            .setContentTitle("Nova Solicitação de Amizade")
            .setContentText("Você recebeu uma solicitação de amizade")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
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
        private const val NOTIFICATION_ID = 1
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}
