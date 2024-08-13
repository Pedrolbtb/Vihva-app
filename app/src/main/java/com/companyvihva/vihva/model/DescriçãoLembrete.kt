package com.companyvihva.vihva.model.PopupRemedio

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

class DescriçãoLembrete : AppCompatActivity() {

    private lateinit var tituloTextView: TextView
    private lateinit var descricaoTextView: TextView

    // Criação das instâncias do FirebaseAuth e FirebaseFirestore
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_lembrete)

        // Inicializa as Views
        tituloTextView = findViewById(R.id.titulo)
        descricaoTextView = findViewById(R.id.descricao_lembrete)

        // Recupera o ID do lembrete passado via Intent
        val eventoId = intent.getStringExtra("eventoId")

        // Verifica se o ID foi passado corretamente
        if (eventoId != null) {
            buscarLembrete(eventoId)
        } else {
            // Lidar com a ausência do ID
            tituloTextView.text = "Erro: ID do lembrete não encontrado."
            descricaoTextView.text = ""
        }

        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_doencas)
        btnExcluir.setOnClickListener {
            eventoId?.let { id ->
                showConfirmDeleteDialog(id)
            }
        }
    }

    private fun buscarLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("eventos")
            .document(eventoId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val titulo = document.getString("titulo")
                val descricao = document.getString("descricao")

                // Atualiza as Views com os dados do lembrete
                tituloTextView.text = titulo ?: "Título não disponível"
                descricaoTextView.text = descricao ?: "Descrição não disponível"
            } else {
                // Lidar com o caso em que o documento não existe
                tituloTextView.text = "Documento não encontrado."
                descricaoTextView.text = ""
            }
        }.addOnFailureListener { exception ->
            // Lidar com erros ao acessar o Firestore
            tituloTextView.text = "Erro ao carregar o lembrete."
            descricaoTextView.text = ""
        }
    }

    private fun showConfirmDeleteDialog(eventoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este lembrete? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ ->
                deleteLembrete(eventoId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o lembrete individualmente
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
