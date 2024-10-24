package com.companyvihva.vihva.Inicio.Perfil_medico

import android.app.ActivityOptions
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_amigo_descrição
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Perfil_medico : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var descricaoTextView: TextView
    private lateinit var urlImageView: ImageView
    private var doenca: tipo_amigo_descrição? = null
    private var amigoId: String? = null
    private lateinit var centroMedicoView: TextView
    private lateinit var crmView: TextView
    private lateinit var nomeCompletoView: TextView
    private lateinit var fotoUmImageView:ImageView
    private lateinit var fotoDoisImageView: ImageView
    private lateinit var fotoTresImageView: ImageView
    private lateinit var detalhesClinicaView: TextView

    // Atualizado para a TextView que exibirá o nome completo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_perfil_medico)

        // Inicializa o Firestore e o FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtém o ID do amigo passado pelo Intent
        amigoId = intent.getStringExtra("amigoId")

        // Inicializa as views
        nomeTextView = findViewById(R.id.text_nome)
        descricaoTextView = findViewById(R.id.text_genero)
        urlImageView = findViewById(R.id.img_save_perfil)
        centroMedicoView = findViewById(R.id.textView5)
        crmView = findViewById(R.id.crm)
        nomeCompletoView = findViewById(R.id.text_nome) // Corrigido para a TextView que exibirá o nome completo
        fotoUmImageView = findViewById(R.id.foto1)
        fotoDoisImageView = findViewById(R.id.foto2)
        fotoTresImageView = findViewById(R.id.foto3)
        detalhesClinicaView = findViewById(R.id.textView6)
        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.close)
        btnVoltar.setOnClickListener {
            // Adiciona animação ao voltar para a tela anterior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(
                    this, R.anim.fade_in, R.anim.fade_out
                )
                finishAfterTransition()
            } else {
                finish()
            }
        }

        // Configura o botão de excluir amigo
        val btnExcluir = findViewById<AppCompatButton>(R.id.exclui_medico)
        btnExcluir.setOnClickListener {
            amigoId?.let { id ->
                showConfirmDeleteDialog(id)
            }
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
                    val centroMedico = document.getString("localiza")
                    val crm = document.getString("crm")
                    val fotoUm = document.getString("fotoUm")
                    val fotoDois = document.getString("fotoDois")
                    val fotoTres = document.getString("fotoTres")
                    val detalhesClinica = document.getString("detalhesClinica")

                    // Carrega a URL de imagem usando o Picasso
                    imageUrl?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    fotoUm?.let {
                        Picasso.get().load(it).into(fotoUmImageView)
                    }
                    fotoDois?.let {
                        Picasso.get().load(it).into(fotoDoisImageView)
                    }
                    fotoTres?.let {
                        Picasso.get().load(it).into(fotoTresImageView)
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
                        fotoUm ?: "",
                        fotoDois ?: "",
                        fotoTres ?: "",
                        detalhesClinica ?:""

                    )

                    // Atualiza as TextViews com os dados
                    val nomeCompleto = "${nome ?: ""} ${sobrenome ?: ""}".trim() // Combina nome e sobrenome
                    nomeCompletoView.text = nomeCompleto
                    descricaoTextView.text = especializacao
                    centroMedicoView.text = "Centro Médico: $centroMedico"
                    crmView.text = "CRM: $crm"
                    detalhesClinicaView.text = "Detalhes da cliníca: $detalhesClinica"
                } else {
                    Log.d("Perfil_medico", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Perfil_medico", "Erro ao obter documento", e)
            }
    }

    // Método para exibir o AlertDialog de confirmação de exclusão
    private fun showConfirmDeleteDialog(amigoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja desfazer seu vínculo com esse profissional? Esta ação não é reversível.")
            setPositiveButton("Sim") { _, _ ->
                deleteMedicoArray(amigoId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o médico do array do cliente no Firebase
    private fun deleteMedicoArray(amigoId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("medicos", FieldValue.arrayRemove(amigoId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Profissional excluído com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Perfil_medico", "Profissional excluído com sucesso")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("Perfil_medico", "Erro ao excluir perfil do médico", e)
                }
        }
    }
}
