package com.companyvihva.vihva.Inicio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_amigo_descrição
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class tela_perfil_medico: AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: tipo_amigo_descrição? = null
    private var amigoid: String? = null
    private lateinit var centroMedicoView: TextView
    private lateinit var crmView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_perfil_medico)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtem o UID do usuario atualmente cadastrado
        amigoid = intent.getStringExtra("amigoId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.close)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        //Referências para as TextViews que irão receber as informações da doença
        nomeTextView = findViewById(R.id.text_nome)
        descricaoTextView = findViewById(R.id.text_genero)
        urlImageView = findViewById(R.id.img_save_perfil)
       centroMedicoView = findViewById(R.id.centro_medico)
       crmView = findViewById(R.id.crm)
        //Busca os dados do Firebase usando o Id da doença
        amigoid?.let { id ->
            fetchDadosDoFirebase(id)
        }





    }

    private fun fetchDadosDoFirebase(docId: String){
        val docRef = firestore.collection("medicos").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()){
                    val nome = document.getString("nome")
                    val especializacao = document.getString("especializacao")
                    val imageUrl = document.getString("imageUrl")
                    val centroMedico = document.getString("centroMedico")
                    val crm = document.getString("crm")
                    //Carrega a Url de imagem usando o Picasso
                    imageUrl.let {
                        Picasso.get().load(it).into(urlImageView)
                    }

                    //Cria um objeto Tipo_Classe com os dados obtidos no Firestore
                    doenca = tipo_amigo_descrição(
                        imageUrl ?: "",
                        nome ?: "",
                        especializacao ?: "",
                        centroMedico ?:"",
                        crm ?:"",
                        "")

                    //Atualiza as TextViews com os dados de nome e descrição
                    nomeTextView.text = nome
                    descricaoTextView.text = especializacao
                    centroMedicoView.text = centroMedico
                    crmView.text = crm
                }else {
                    Log.d("PopupDoenca", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupRemedio", "Erro ao obter documento", e)
            }
    }


}




