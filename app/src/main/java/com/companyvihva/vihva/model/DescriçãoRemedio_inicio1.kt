package com.companyvihva.vihva.com.companyvihva.vihva.model

import MedicoAdapter
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.companyvihva.vihva.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DescriçãoRemedio_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var urlImageView: ImageView
    private lateinit var medicoSpinner: Spinner
    private var listaMedicos: MutableList<medico_spinner> = mutableListOf()
    private var medicoMap: MutableMap<String, String> = mutableMapOf()
    private lateinit var textViewCalendario: TextView
    private lateinit var observacaoTextView: TextView
    private var remedioId: String? = null
    private var formattedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_remedio_inicio1)

        // Inicializa Firestore e Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inicializa as views
        nomeTextView = findViewById(R.id.nomere)
        urlImageView = findViewById(R.id.foto_Remedio)
        medicoSpinner = findViewById(R.id.medicoSpinner)
        textViewCalendario = findViewById(R.id.textView_receitado)
        observacaoTextView = findViewById(R.id.edit_descAlarme)

        // Obtém o ID do remédio passado pelo Intent
        remedioId = intent.getStringExtra("remedioId")

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

        // Carrega os dados do remédio a partir do Firebase
        remedioId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Configura o DatePicker para selecionar a data de prescrição
        textViewCalendario.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione quando este remédio foi prescrito")
                .build()
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDate = sdf.format(Date(selectedDate))
                textViewCalendario.text = formattedDate
            }
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Configura o botão de adicionar/remédio (salvar)
        val btnSalvar = findViewById<AppCompatButton>(R.id.btn_add_remédio)
        btnSalvar.setOnClickListener {
            salvarInformacoesRemedio()
        }

        // Carrega a lista de médicos do Firebase
        carregarMedicos()
    }

    // Método para carregar os dados do remédio do Firestore
    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("remedios").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val url = document.getString("url")
                    val nome = document.getString("nome")
                    // Carrega a imagem usando Picasso se a URL estiver disponível
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    // Atualiza TextViews com os dados do remédio
                    nomeTextView.text = nome
                } else {
                    Log.d("DescricaoRemedioInicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoRemedioInicio1", "Erro ao obter documento", e)
            }
    }

    // Método para mostrar o diálogo de confirmação de exclusão
    private fun showConfirmDeleteDialog(remedioId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir este remédio? Você pode adicioná-lo novamente na lista de remédios.")
            setPositiveButton("Sim") { _, _ ->
                deleteRemedioArray(remedioId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o remédio do array no Firestore
    private fun deleteRemedioArray(remedioId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("remedios", FieldValue.arrayRemove(remedioId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Remédio excluído do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescricaoRemedioInicio1", "Remédio removido do array com sucesso")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescricaoRemedioInicio1", "Erro ao remover remédio do array", e)
                }
        }
    }

    // Método para salvar as informações do remédio
    private fun salvarInformacoesRemedio() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nomeRemedio = nomeTextView.text.toString()
        val observacao = observacaoTextView.text.toString()
        val prescritoPor = medicoMap[medicoSpinner.selectedItem.toString()] ?: ""
        val dataPrescricao = formattedDate ?: ""

        // Cria o mapa de dados do remédio
        val remedioMap = hashMapOf(
            "nome" to nomeRemedio,
            "observacao" to observacao,
            "prescritoPor" to prescritoPor,
            "dataPrescricao" to dataPrescricao,

        )

        // Salva os dados no Firestore
        val clienteDocRef = firestore.collection("clientes").document(user.uid)
        clienteDocRef.update("remedios", FieldValue.arrayUnion(remedioMap))
            .addOnSuccessListener {
                Toast.makeText(this, "Informações salvas com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescricaoRemedioInicio1", "Sucesso ao salvar informações do remédio")
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoRemedioInicio1", "Erro ao salvar informações do remédio", e)
            }
    }

    // Método para carregar médicos do Firestore
    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("clientes").document(userId).get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>
                if (medicosArray != null) {
                    val medicosList = mutableListOf<medico_spinner>()
                    medicosArray.forEach { medicoUid ->
                        firestore.collection("medicos").document(medicoUid).get()
                            .addOnSuccessListener { medicoDoc ->
                                val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                                val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                                val medico = medico_spinner(nomeMedico, imageUrl)
                                medicosList.add(medico)
                                medicoMap[medicoUid] = nomeMedico

                                // Atualiza o adapter do Spinner
                                val adapter = MedicoAdapter(this, medicosList)
                                medicoSpinner.adapter = adapter
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Erro ao carregar médicos", e)
            }
    }
}
