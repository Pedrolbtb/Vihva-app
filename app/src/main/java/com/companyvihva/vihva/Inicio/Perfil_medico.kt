package com.companyvihva.vihva.Inicio.Perfil_medico

import android.app.ActivityOptions
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
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
    private lateinit var fotoUmImageView: ImageView
    private lateinit var fotoDoisImageView: ImageView
    private lateinit var fotoTresImageView: ImageView
    private lateinit var detalhesClinicaView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_perfil_medico)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        amigoId = intent.getStringExtra("amigoId")

        nomeTextView = findViewById(R.id.text_nome)
        descricaoTextView = findViewById(R.id.text_genero)
        urlImageView = findViewById(R.id.img_save_perfil)
        centroMedicoView = findViewById(R.id.textView5)
        crmView = findViewById(R.id.crm)
        nomeCompletoView = findViewById(R.id.text_nome)
        fotoUmImageView = findViewById(R.id.foto1)
        fotoDoisImageView = findViewById(R.id.foto2)
        fotoTresImageView = findViewById(R.id.foto3)
        detalhesClinicaView = findViewById(R.id.textView6)

        val btnVoltar = findViewById<ImageButton>(R.id.close)
        btnVoltar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(
                    this, R.anim.fade_in, R.anim.fade_out
                )
                finishAfterTransition()
            } else {
                finish()
            }
        }

        val btnExcluir = findViewById<AppCompatButton>(R.id.exclui_medico)
        btnExcluir.setOnClickListener {
            amigoId?.let { id -> showConfirmDeleteDialog(id) }
        }

        amigoId?.let { id -> fetchDadosDoFirebase(id) }
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
                        detalhesClinica ?: ""
                    )

                    val nomeCompleto = "${nome ?: ""} ${sobrenome ?: ""}".trim()
                    nomeCompletoView.text = nomeCompleto
                    descricaoTextView.text = especializacao

                    // Aplicar cor somente ao texto dinâmico
                    val centroMedicoSpannable = SpannableString("Centro Médico: $centroMedico")
                    centroMedicoSpannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#807e7a")),
                        14, // Começa após "Centro Médico: "
                        centroMedicoSpannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    centroMedicoView.text = centroMedicoSpannable

                    crmView.text = "CRM: $crm"

                    val detalhesClinicaSpannable = SpannableString("Detalhes da clínica: $detalhesClinica")
                    detalhesClinicaSpannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#807e7a")),
                        21, // Começa após "Detalhes da clínica: "
                        detalhesClinicaSpannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    detalhesClinicaView.text = detalhesClinicaSpannable
                } else {
                    Log.d("Perfil_medico", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Perfil_medico", "Erro ao obter documento", e)
            }
    }

    private fun showConfirmDeleteDialog(amigoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja desfazer seu vínculo com esse profissional? Esta ação não é reversível.")
            setPositiveButton("Sim") { _, _ -> deleteMedicoArray(amigoId) }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    private fun deleteMedicoArray(amigoId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("medicos", FieldValue.arrayRemove(amigoId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Profissional excluído com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("Perfil_medico", "Profissional excluído com sucesso")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("Perfil_medico", "Erro ao excluir perfil do médico", e)
                }
        }
    }
}
