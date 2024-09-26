package com.companyvihva.vihva.com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DescriçãoRemedio_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var remedioId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio_inicio1)

        // Inicializa o Firestore e o FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtém o ID do remédio passado pelo Intent
        remedioId = intent.getStringExtra("remedioId")

        // Inicializa as views
        nomeTextView = findViewById(R.id.nomere)
        urlImageView = findViewById(R.id.foto_Remedio)

        // Busca os dados do remédio no Firebase usando o ID
        remedioId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Configura o botão de excluir
        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_remedios)
        btnExcluir.setOnClickListener {
            remedioId?.let { id ->
                showConfirmDeleteDialog(id)
            }
        }
    }

    // Método para buscar os dados do remédio no Firebase
    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val url = document.getString("Url")
                    val nome = document.getString("nome")
                    // Carrega a imagem usando o Picasso se a URL estiver disponível
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    // Atualiza os TextViews com os dados do remédio
                    nomeTextView.text = nome
                } else {
                    Log.d("DescriçãoRemedio_inicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoRemedio_inicio1", "Erro ao obter documento", e)
            }
    }

    //Método para o Alert Dialog do Remédio
    private fun showConfirmDeleteDialog(remedioId: String){
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir este remédio? Você pode adiciona-lo novamente na lista de remédios")
            setPositiveButton("Sim") {_, _ ->
                deleteRemedioArray(remedioId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o remédio do array do cliente no Firebase
    private fun deleteRemedioArray(remedioId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("remedios", FieldValue.arrayRemove(remedioId))
                .addOnSuccessListener {
                    Toast.makeText(this,"Rememédio excluido do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescriçãoRemedio_inicio1", "Remédio removido do array com sucesso")
                    // Voltar para a atividade anterior após a exclusão
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescriçãoRemedio_inicio1", "Erro ao remover remédio do array", e)
                }
        }
    }
}
