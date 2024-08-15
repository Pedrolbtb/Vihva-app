package com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Descrição_alarme : AppCompatActivity() {

    private lateinit var descricaoTextView: TextView

    // Criação das instâncias do FirebaseAuth e FirebaseFirestore
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_alarme)

        // Inicializa a View
        descricaoTextView = findViewById(R.id.descricao) // Certifique-se de que esse ID exista no seu layout XML

        // Recupera o ID do alarme passado via Intent
        val alarmeId = intent.getStringExtra("ALARME_ID")

        // Verifica se o ID foi passado corretamente
        if (alarmeId != null) {
            buscarAlarme(alarmeId)
        } else {
            // Lidar com a ausência do ID
            descricaoTextView.text = "Erro: ID do alarme não encontrado."
        }

        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_alarme)
        btnExcluir.setOnClickListener {
            alarmeId?.let { id ->
                showConfirmDeleteDialog(id)
            }
        }
    }

    private fun buscarAlarme(alarmeId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("Alarmes")
            .document(alarmeId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val descricao = document.getString("descricao")
                descricaoTextView.text = descricao ?: "Descrição não disponível"
            } else {
                // Lidar com o caso em que o documento não existe
                descricaoTextView.text = "Documento não encontrado."
            }
        }.addOnFailureListener { exception ->
            // Lidar com erros ao acessar o Firestore
            descricaoTextView.text = "Erro ao carregar o alarme."
        }
    }

    private fun showConfirmDeleteDialog(alarmeId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este alarme? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ ->
                deleteAlarme(alarmeId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o alarme individualmente
    private fun deleteAlarme(alarmeId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("Alarmes")
            .document(alarmeId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Alarme excluído com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescriçãoAlarme", "Alarme excluído com sucesso")
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoAlarme", "Erro ao excluir o alarme", e)
                Toast.makeText(this, "Erro ao excluir o alarme", Toast.LENGTH_SHORT).show()
            }
    }
}
