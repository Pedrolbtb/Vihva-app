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
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DescriçãoDoença_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: Tipo_Classe? = null
    private var doencaid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_doenca_inicio1)

        // Inicializa o Firestore e o auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtém o ID da doença passada pelo Intent
        doencaid = intent.getStringExtra("doencaId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarDO)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Configura o botão de excluir
        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_doencas)
        btnExcluir.setOnClickListener {
            doencaid?.let { id ->
               showConfirmDeleteDialog(id)
            }
        }

        // Referências para as TextViews que irão receber as informações da doença
        nomeTextView = findViewById(R.id.nomeDoenca)
        descricaoTextView = findViewById(R.id.descricao2)
        urlImageView = findViewById(R.id.foto_Doenca)

        // Busca os dados do Firebase usando o Id da doença
        doencaid?.let { id ->
            fetchDadosDoFirebase(id)
        }
    }

    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("doenca").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    // Carrega a Url de imagem usando o Picasso
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }

                    // Cria um objeto Tipo_Classe com os dados obtidos no Firestore
                    doenca = Tipo_Classe(url ?: "", nome ?: "", descricao ?: "")

                    // Atualiza as TextViews com os dados de nome e descrição
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao
                } else {
                    Log.d("PopupDoenca", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupDoenca", "Erro ao obter documento", e)
            }
    }

    //Método para o Alert Dialog da doença
    private fun showConfirmDeleteDialog(doencaId: String){
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir esta doença? Você pode adiciona-la novamente na lista de doenças")
            setPositiveButton("Sim") { _, _ ->
                deleteDoencaArray(doencaId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    //Método para excluir a doença individualmente
    private fun deleteDoencaArray(doencaId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("doenca", FieldValue.arrayRemove(doencaId))
                .addOnSuccessListener {
                    Toast.makeText(this,"Doença excluida do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescriçãoDoença_Inicio1", "Sucesso ao remover doença do Array")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescriçãoDoença_Inicio1", "Erro ao excluir doença do array", e)
                }
        }
    }
}
