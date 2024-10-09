package com.companyvihva.vihva.Configuracoes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Configurações.Config_List
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.SolicitacaoAmizadeAdapter
import com.companyvihva.vihva.com.companyvihva.vihva.model.SolicitacaoAmizade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ConfigNotificacoes : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SolicitacaoAmizadeAdapter
    private val notifiedSolicitations = mutableSetOf<String>()

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

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarNotifiq)
            .setOnClickListener {
                val intent = Intent(this, Config_List::class.java).apply {
                }
                startActivity(intent)
        }
        carregarSolicitacoesAmizade()
        monitorarSolicitacoesDeAmizade()

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

    override fun onResume() {
        super.onResume()
        // Recarregue as solicitações quando a tela for visível
        carregarSolicitacoesAmizade()
    }

    private fun monitorarSolicitacoesDeAmizade() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection(COLLECTION_SOLICITACOES)
            .whereEqualTo(FIELD_PACIENTE_ID, currentUserUid)
            .whereEqualTo(FIELD_STATUS, STATUS_PENDENTE)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirestoreError", "Error listening for solicitacoes", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val solicitacoes = snapshot.documents.mapNotNull { document ->
                        val solicitacao = document.toObject(SolicitacaoAmizade::class.java)
                        solicitacao?.id = document.id
                        solicitacao
                    }

                    for (solicitacao in solicitacoes) {
                        if (solicitacao.id !in notifiedSolicitations) {
                            notificacaoAmizade(solicitacao)
                            notifiedSolicitations.add(solicitacao.id)
                        }
                    }

                    // Atualize o RecyclerView com novas solicitações
                    adapter.submitList(solicitacoes)
                } else {
                    Log.d("Snapshot", "Nenhuma solicitação encontrada.")
                }
            }
    }

    private fun notificacaoAmizade(solicitacao: SolicitacaoAmizade) {
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
        val channelId = "friend_request_channel_id"

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarNotifiq)
        btnVoltar.setOnClickListener {
            finish()
        }

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
        val notificationIntent = Intent(this, ConfigNotificacoes::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // URL da foto do solicitante
        val photoUrl = solicitacao.fotoSolicitante ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val photoBitmap = getBitmapFromURL(photoUrl)
            withContext(Dispatchers.Main) {
                // Construção da notificação
                val notificationBuilder = NotificationCompat.Builder(this@ConfigNotificacoes, channelId)
                    .setSmallIcon(R.drawable.sininho)
                    .setContentTitle("Nova Solicitação de Amizade")
                    .setContentText("Você recebeu uma solicitação de amizade de ${solicitacao.nomeSolicitante ?: "alguém"}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(notificationPendingIntent)
                    .setAutoCancel(true)
                    .apply {
                        photoBitmap?.let {
                            setLargeIcon(it)
                            setStyle(NotificationCompat.BigPictureStyle().bigPicture(it))
                        }
                    }
                // Utilize um ID único para a notificação
                val uniqueNotificationId = solicitacao.id.hashCode()
                notificationManager.notify(uniqueNotificationId, notificationBuilder.build())
            }
        }
    }

    private fun getBitmapFromURL(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Permissão", "Permissão concedida.")
            } else {
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show()
            }
        }
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
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}