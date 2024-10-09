package com.companyvihva.vihva.Inicio.Perfil_medico

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_amigo_descrição
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Descrição_addmedico : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: tipo_amigo_descrição? = null
    private var amigoId: String? = null
    private lateinit var centroMedicoView: TextView
    private lateinit var crmView: TextView
    private lateinit var nomeCompletoView: TextView // Atualizado para a TextView que exibirá o nome completo
  private lateinit var  detalhesClinicaView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_addmedico)

        // Inicializa o Firestore e o FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtém o ID do amigo passado pelo Intent
        amigoId = intent.getStringExtra("amigoId")

        // Inicializa as views
        nomeTextView = findViewById(R.id.text_nome)
        descricaoTextView = findViewById(R.id.text_genero)
        urlImageView = findViewById(R.id.img_save_perfil)
        centroMedicoView = findViewById(R.id.centro_medico)
        crmView = findViewById(R.id.crm)
        nomeCompletoView = findViewById(R.id.text_nome) // Corrigido para a TextView que exibirá o nome completo
        detalhesClinicaView = findViewById(R.id.detalhes_clinica)
        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.close)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }



        // Busca os dados do Firebase usando o ID do amigo
        amigoId?.let { id ->
            fetchDadosDoFirebase(id)
        }
    }

    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("medicos").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val sobrenome = document.getString("sobrenome")
                    val especializacao = document.getString("especializacao")
                    val imageUrl = document.getString("imageUrl")
                    val centroMedico = document.getString("centroMedico")
                    val crm = document.getString("crm")
                    val foto1 = document.getString("fotoUm")
                    val foto2 = document.getString("fotoDois")
                    val foto3 = document.getString("fotoTres")
                    val detalhesClinica = document.getString("detalhesClinica")
                    // Carrega a URL de imagem usando o Picasso
                    imageUrl?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }

                    // Cria um objeto Tipo_Classe com os dados obtidos no Firestore
                    doenca = tipo_amigo_descrição(
                        imageUrl ?: "",
                        nome ?: "",
                        especializacao ?: "",
                        centroMedico ?: "",
                        crm ?: "",
                        sobrenome ?: "",
                        "",
                        foto1 ?: "",
                        foto2 ?: "",
                        foto3 ?: "",
                        detalhesClinica ?: ""
                    )

                    // Atualiza as TextViews com os dados
                    val nomeCompleto = "${nome ?: ""} ${sobrenome ?: ""}".trim() // Combina nome e sobrenome
                    nomeCompletoView.text = nomeCompleto
                    descricaoTextView.text = especializacao
                    centroMedicoView.text = centroMedico
                    crmView.text = crm
                    detalhesClinicaView.text = detalhesClinica
                } else {
                    Log.d("Perfil_medico", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Perfil_medico", "Erro ao obter documento", e)
            }
    }


    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


}
