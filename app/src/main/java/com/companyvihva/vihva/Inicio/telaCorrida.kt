package com.companyvihva.vihva.Inicio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Configurações.Config_List
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class telaCorrida : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_corrida)

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarWatch)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, Config_List::class.java)
            startActivity(intent)
        }

        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            // Pega referência ao documento do usuário no Firestore
            val userDocRef = db.collection("clientes").document(currentUserUid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtém os dados do documento
                    val batimentos = document.getLong("batimentos")?.toString() ?: "0"
                    val distancia = document.getDouble("distancia")?.let { dist ->
                        String.format("%.3f", dist) // Limita a distância a 3 casas decimais
                    } ?: "0.0"
                    val passos = document.getLong("passos")?.toString() ?: "0"

                    // Atualiza as TextViews com os dados recuperados
                    findViewById<TextView>(R.id.text_batimentos).text = "Batimentos: $batimentos BPM"
                    findViewById<TextView>(R.id.text_distancia).text = "Distância: $distancia km"
                    findViewById<TextView>(R.id.text_passos).text = "Passos: $passos"
                } else {
                    // Documento não existe ou não foi encontrado
                    findViewById<TextView>(R.id.text_batimentos).text = "Batimentos: Não disponível"
                    findViewById<TextView>(R.id.text_distancia).text = "Distância: Não disponível"
                    findViewById<TextView>(R.id.text_passos).text = "Passos: Não disponível"
                }
            }.addOnFailureListener { e ->
                // Lida com falha na obtenção dos dados
                findViewById<TextView>(R.id.text_batimentos).text = "Erro ao carregar dados"
                findViewById<TextView>(R.id.text_distancia).text = "Erro ao carregar dados"
                findViewById<TextView>(R.id.text_passos).text = "Erro ao carregar dados"
                e.printStackTrace()
            }
        }
    }
}
