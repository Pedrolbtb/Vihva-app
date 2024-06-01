package com.companyvihva.vihva

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.model.OnRemedioSelectedListener
import com.companyvihva.vihva.model.Remedio2
import com.google.firebase.firestore.FirebaseFirestore


class PopupRemedio : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private var remedio: Remedio2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio)

        firestore = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("remedioId")

        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        nomeTextView = findViewById(R.id.nomere)
        descricaoTextView = findViewById(R.id.descricao1)

        documentId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        val btnAdd = findViewById<Button>(R.id.btn_add_remédio)
        btnAdd.setOnClickListener {
            remedio?.let {
                (application as? OnRemedioSelectedListener)?.onRemedioSelected(it)
                finish()
            }
        }
    }

    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val foto = document.getString("foto")
                    remedio = Remedio2(foto ?: "", nome ?: "")
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao
                } else {
                    Log.d("PopupRemedio", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupRemedio", "Erro ao obter documento", e)
            }
    }
}
