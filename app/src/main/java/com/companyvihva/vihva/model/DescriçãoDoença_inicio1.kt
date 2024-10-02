package com.companyvihva.vihva.com.companyvihva.vihva.model

import MedicoAdapter
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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

class DescriçãoDoença_inicio1 : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeTextView: TextView
    private lateinit var urlImageView: ImageView
    private lateinit var medicoSpinner: Spinner
    private var listaMedicos: MutableList<medico_spinner> = mutableListOf()
    private var medicoMap: MutableMap<String, String> = mutableMapOf()
    private lateinit var textViewCalendario: TextView
    private lateinit var observacaoTextView: TextView
    private var doencaId: String? = null
    private var formattedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_doenca_inicio1)

        // Inicializa Firestore e Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inicializa as views
        nomeTextView = findViewById(R.id.nomeDoenca)
        urlImageView = findViewById(R.id.foto_Doenca)
        medicoSpinner = findViewById(R.id.medicoSpinner)
        textViewCalendario = findViewById(R.id.textView_receitado)
        observacaoTextView = findViewById(R.id.edit_descAlarme)

        // Obtém o ID da doença passado pelo Intent
        doencaId = intent.getStringExtra("doencaId")

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarDO)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        val medicosList = mutableListOf("Nenhum médico selecionado") // Opção padrão
        val adapter = ArrayAdapter(this, R.layout.spinner, medicosList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicoSpinner.adapter = adapter

        // Configura o botão de excluir
        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_doencas)
        btnExcluir.setOnClickListener {
            doencaId?.let { id ->
                showConfirmDeleteDialog(id)
            }
        }

        // Carrega os dados da doença a partir do Firebase
        doencaId?.let { id ->
            fetchDadosDoFirebase(id)
        }

        // Configura o DatePicker para selecionar a data de prescrição
        textViewCalendario.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione quando esta doença foi diagnosticada")
                .build()
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDate = sdf.format(Date(selectedDate))
                textViewCalendario.text = formattedDate
            }
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Configura o botão de salvar informações da doença
        val btnSalvar = findViewById<AppCompatButton>(R.id.btn_salvar)
        btnSalvar.setOnClickListener {
            salvarInformacoesDoenca()
        }

        // Carrega a lista de médicos do Firebase
        carregarMedicos()
    }

    // Método para carregar os dados da doença do Firestore
    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("doencas").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val url = document.getString("url")
                    val nome = document.getString("nome")
                    // Carrega a imagem usando Picasso se a URL estiver disponível
                    url?.let {
                        Picasso.get().load(it).into(urlImageView)
                    }
                    // Atualiza TextViews com os dados da doença
                    nomeTextView.text = nome
                } else {
                    Log.d("DescricaoDoencaInicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoDoencaInicio1", "Erro ao obter documento", e)
            }
    }

    // Método para mostrar o diálogo de confirmação de exclusão
    private fun showConfirmDeleteDialog(doencaId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir esta doença? Você pode adicioná-la novamente na lista de doenças.")
            setPositiveButton("Sim") { _, _ ->
                deleteDoencaArray(doencaId)
            }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir a doença do array no Firestore
    private fun deleteDoencaArray(doencaId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("doencas", FieldValue.arrayRemove(doencaId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Doença excluída do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescricaoDoencaInicio1", "Doença removida do array com sucesso")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescricaoDoencaInicio1", "Erro ao remover doença do array", e)
                }
        }
    }

    // Método para salvar as informações da doença
    private fun salvarInformacoesDoenca() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nomeDoenca = nomeTextView.text.toString()
        val observacao = observacaoTextView.text.toString()
        val prescritoPor = medicoMap[medicoSpinner.selectedItem.toString()] ?: ""
        val dataPrescricao = formattedDate ?: ""

        // Cria o mapa de dados da doença
        val doencaMap = hashMapOf(
            "nome" to nomeDoenca,
            "observacao" to observacao,
            "prescritoPor" to prescritoPor,
            "dataPrescricao" to dataPrescricao
        )

        // Salva os dados no Firestore
        val clienteDocRef = firestore.collection("clientes").document(user.uid)
        clienteDocRef.update("doencas", FieldValue.arrayUnion(doencaMap))
            .addOnSuccessListener {
                Toast.makeText(this, "Informações salvas com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescricaoDoencaInicio1", "Sucesso ao salvar informações da doença")
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoDoencaInicio1", "Erro ao salvar informações da doença", e)
            }
    }

    // Método para carregar médicos do Firestore
    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("clientes").document(userId).get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>

                // Adiciona a opção "Nenhum médico selecionado" primeiro
                val medicoNenhum = medico_spinner("Nenhum médico selecionado", "")
                listaMedicos.add(medicoNenhum)

                if (medicosArray != null) {
                    medicosArray.forEach { medicoUid ->
                        firestore.collection("medicos").document(medicoUid).get()
                            .addOnSuccessListener { medicoDoc ->
                                val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                                val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                                val medico = medico_spinner(nomeMedico, imageUrl)
                                listaMedicos.add(medico)
                                medicoMap[medicoUid] = nomeMedico

                                // Atualiza o adapter do Spinner
                                val adapter = MedicoAdapter(this, listaMedicos)
                                medicoSpinner.adapter = adapter
                            }
                            .addOnFailureListener { e ->
                                Log.w("DescricaoDoencaInicio1", "Erro ao buscar médico $medicoUid", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoDoencaInicio1", "Erro ao buscar médicos", e)
            }
    }
}
