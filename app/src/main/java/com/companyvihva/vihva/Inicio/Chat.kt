package com.companyvihva.vihva.Inicio.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Chat : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        firestore = FirebaseFirestore.getInstance()

        val userImage = findViewById<ImageView>(R.id.user_image)
        val userName = findViewById<TextView>(R.id.user_name)
        val messageInput = findViewById<EditText>(R.id.message_input)
        val sendButton = findViewById<ImageButton>(R.id.send_button)

        // Recebe os dados do Intent
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL")
        val nome = intent.getStringExtra("EXTRA_NOME")
        val pacienteId = intent.getStringExtra("EXTRA_PACIENTE_ID") ?: ""

        // Define o nome completo no TextView
        userName.text = nome

        // Carrega a imagem usando o Picasso
        imageUrl?.let {
            Picasso.get().load(it).into(userImage)
        }

        // Configura o botão de sair
        findViewById<ImageButton>(R.id.sair).setOnClickListener {
            onBackPressed()
        }

        // Configura o botão de enviar mensagem
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val messageData = hashMapOf(
                    "text" to message,
                    "timestamp" to timestamp
                )

                firestore.collection("chats").document(pacienteId).collection("messages")
                    .add(messageData)
                    .addOnSuccessListener {
                        messageInput.text.clear()
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }

        // Configura o listener para receber mensagens em tempo real
        listenerRegistration = firestore.collection("chats").document(pacienteId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // Atualize a UI com as novas mensagens
                    for (document in snapshot.documents) {
                        val text = document.getString("text")
                        val timestamp = document.getString("timestamp")
                        // Atualize a RecyclerView com as mensagens
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }
}
