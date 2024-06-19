package com.companyvihva.vihva.model.PopupRemedio

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

class DescriçãoDoença: AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: Tipo_Classe? = null
    private var doencaid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_doenca)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtem o UID do usuario atualmente cadastrado
        doencaid = intent.getStringExtra("doencaId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarpop)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        //Referências para as TextViews que irão receber as informações da doença
        nomeTextView = findViewById(R.id.nomedo)
        descricaoTextView = findViewById(R.id.descricao1)
        urlImageView = findViewById(R.id.foto_Doenca)

        //Busca os dados do Firebase usando o Id da doença
        doencaid?.let { id ->
            fetchDadosDoFirebase(id)
        }

        //Configura o botão de adicionar doença da activity
        val btnAdd = findViewById<Button>(R.id.btn_add_doenca)
        btnAdd.setOnClickListener{
            //Adiciona o remédio ao banco de dados do cliente atualmente autenticado
            doencaid?.let { id ->
                adicionarDoencaAoCliente(id)
            }
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

    // Método para adicionar o ID da doença ao banco de dados do cliente autenticado
    private fun adicionarDoencaAoCliente(doencaId: String){
        //Obtem o UID do usuario
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null){
            //Referencia ao documento do Firestore usando UID do Usuario
            val clientRef = firestore.collection("clientes").document(uid)

            //Atualiza a lista de IDs da doença ao cliente
            clientRef.update("doenca",FieldValue.arrayUnion(doencaId))
                .addOnSuccessListener {
                    // Exibe uma mensagem de sucesso
                    Toast.makeText(this, "Remédio adicionado com sucesso", Toast.LENGTH_SHORT).show()
                    // Fecha a Activity de popup
                    finish()
                }
                .addOnFailureListener { e ->
                    // Trata falhas na atualização do banco de dados
                    Log.w("DescriçãoRemedio", "Erro ao adicionar remédio", e)
                    Toast.makeText(this, "Erro ao adicionar remédio: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Se não conseguir obter o UID do usuário logado
            Toast.makeText(this, "Erro: usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
        }
    }


