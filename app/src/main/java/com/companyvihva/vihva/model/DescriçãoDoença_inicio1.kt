package com.companyvihva.vihva.com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.squareup.picasso.Picasso

class DescriçãoDoença_inicio1: AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: Tipo_Classe? = null
    private var doencaid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_doenca_inicio1)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtem o UID do usuario atualmente cadastrado
        doencaid = intent.getStringExtra("doencaId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarDO)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        //Referências para as TextViews que irão receber as informações da doença
        nomeTextView = findViewById(R.id.nomeDoenca)
        descricaoTextView = findViewById(R.id.descricao2)
        urlImageView = findViewById(R.id.foto_Doenca)

        //Busca os dados do Firebase usando o Id da doença
        doencaid?.let { id ->
            fetchDadosDoFirebase(id)
        }





    }

    private fun fetchDadosDoFirebase(docId: String){
        val docRef = firestore.collection("doenca").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()){
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    //Carrega a Url de imagem usando o Picasso
                    url.let {
                        Picasso.get().load(it).into(urlImageView)
                    }

                    //Cria um objeto Tipo_Classe com os dados obtidos no Firestore
                    doenca = Tipo_Classe(url ?: "", nome ?: "", descricao ?: "")

                    //Atualiza as TextViews com os dados de nome e descrição
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao
                }else {
                    Log.d("PopupDoenca", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupRemedio", "Erro ao obter documento", e)
            }
    }


}




