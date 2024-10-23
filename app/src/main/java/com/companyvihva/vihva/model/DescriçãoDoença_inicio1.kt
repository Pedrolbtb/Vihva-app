package com.companyvihva.vihva.com.companyvihva.vihva.model

import MedicoAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Classe
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
    private lateinit var urlImageView: ImageView
    private lateinit var medicoSpinner: Spinner
    private var listaMedicos: MutableList<medico_spinner> = mutableListOf()
    private var doenca: Tipo_Classe? = null
    private var doencaid: String? = null
    private var medicoMap: MutableMap<String, String> = mutableMapOf()
    private var formattedDate: String? = null
    private lateinit var nomeTextView: TextView
    private lateinit var textViewCalendario: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_desc_doenca_inicio1)

        // Inicializa o Firestore e o Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inicializa as Views
        medicoSpinner = findViewById(R.id.medicoSpinner)
        urlImageView = findViewById(R.id.foto_Doenca)
        textViewCalendario = findViewById(R.id.textView_receitado)
        nomeTextView = findViewById(R.id.nomeDoenca)

        // Botão para salvar informações
        val btnSalvar = findViewById<Button>(R.id.btn_salvar) // Certifique-se de que o ID do botão esteja correto
        btnSalvar.setOnClickListener {
            salvarInformacoesDoenca()
        }

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
            doencaid?.let { id -> showConfirmDeleteDialog(id) }
        }

        // Carrega a lista de médicos do Firebase
        carregarMedicos()

        // Busca os dados da doença no Firebase
        doencaid?.let { id -> fetchDadosDoFirebase(id) }

        // Configura o DatePicker para selecionar a data
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
    }

    private fun fetchDadosDoFirebase(docId: String) {
        val docRef = firestore.collection("doenca").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome") ?: "Nome não disponível"
                    val descricao = document.getString("descricao") ?: "Descrição não disponível"
                    val url = document.getString("Url") ?: ""  // Use uma string vazia se o URL não estiver disponível
                    val foto = document.getString("foto") ?: ""  // Se você precisar da foto  //eu precisei e nao funcionava!

                    // Carrega a Url de imagem usando o Picasso
                    if (url.isNotEmpty()) {
                        Picasso.get().load(url).into(urlImageView)
                    } else {
                        Log.e("PopupDoenca", "URL não encontrada no documento")
                    }

                    nomeTextView.text = nome

                    // Cria um objeto Tipo_Classe com os dados obtidos no Firestore
                    doenca = Tipo_Classe(foto, nome, descricao) // Adiciona a URL aqui
                } else {
                    Log.d("PopupDoenca", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("PopupDoenca", "Erro ao obter documento", e)
                Toast.makeText(this, "Erro ao carregar dados da doença. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
    }



    // Método para o Alert Dialog da doença
    private fun showConfirmDeleteDialog(doencaid: String) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_confirmar, null)

        val btnCancelar = popupView.findViewById<Button>(R.id.btnRejeitar)
        val btnAceitar = popupView.findViewById<Button>(R.id.btnAceitar)

        val dialog = AlertDialog.Builder(this, R.style.CustomDialog).apply {
            setView(popupView)
        }.create()

        btnAceitar.setOnClickListener {
            deleteDoencaArray(doencaid)
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    // Método para excluir a doença individualmente
    private fun deleteDoencaArray(doencaId: String) {
        val user = auth.currentUser
        user?.let {
            val userDocRef = firestore.collection("clientes").document(it.uid)
            userDocRef.update("doenca", FieldValue.arrayRemove(doencaId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Doença excluída do seu perfil com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("DescriçãoDoença_Inicio1", "Sucesso ao remover doença do Array")
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.w("DescriçãoDoença_Inicio1", "Erro ao excluir doença do array", e)
                    Toast.makeText(this, "Erro ao excluir doença. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun carregarMedicos() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("clientes").document(userId).get()
            .addOnSuccessListener { document ->
                val medicosArray = document.get("medicos") as? List<String>
                medicosArray?.let { loadMedicos(it) }
            }
            .addOnFailureListener { e ->
                Log.e("Evento", "Erro ao carregar médicos", e)
                Toast.makeText(this, "Erro ao carregar médicos. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMedicos(medicosArray: List<String>) {
        val medicosList = mutableListOf<medico_spinner>()
        medicosArray.forEach { medicoUid ->
            firestore.collection("medicos").document(medicoUid).get().addOnSuccessListener { medicoDoc ->
                val nomeMedico = medicoDoc.getString("nome") ?: medicoUid
                val imageUrl = medicoDoc.getString("imageUrl") ?: ""
                val medico = medico_spinner(nomeMedico, imageUrl)
                medicosList.add(medico)
                medicoMap[medicoUid] = nomeMedico

                // Atualiza o adapter do Spinner
                val adapter = MedicoAdapter(this, medicosList)
                medicoSpinner.adapter = adapter
            }.addOnFailureListener { e ->
                Log.w("Evento", "Erro ao buscar médico $medicoUid", e)
                Toast.makeText(this, "Erro ao carregar médico: $medicoUid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarInformacoesDoenca() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nomeDoenca = nomeTextView.text.toString()
        val medicoSelecionado = medicoSpinner.selectedItem as? medico_spinner
        val prescritoPorUid = medicoSelecionado?.let { medicoMap.keys.firstOrNull { key -> medicoMap[key] == it.nome } } ?: ""

        // Validações
        if (nomeDoenca.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o nome da doença", Toast.LENGTH_SHORT).show()
            return
        }

        if (formattedDate == null) {
            Toast.makeText(this, "Por favor, selecione uma data", Toast.LENGTH_SHORT).show()
            return
        }

        // Adicione a URL da imagem da doença, se disponível
        val urlImagem = doenca?.foto // Obtém a URL da imagem da doença

        // Cria o mapa de dados da doença
        val doencaMap = hashMapOf(
            "nome" to nomeDoenca,
            "dataPrescricao" to formattedDate!!,
            "prescritoPor" to prescritoPorUid,
            "remedioId" to doencaid,
            "urlImagem" to (urlImagem ?: "") // Adiciona a URL da imagem ao mapa
        )

        // Salva os dados no Firestore na coleção "clientes" sob o array "prescrições"
        val clienteDocRef = firestore.collection("clientes").document(user.uid)
        clienteDocRef.update("prescriçõesDoença", FieldValue.arrayUnion(doencaMap))
            .addOnSuccessListener {
                Toast.makeText(this, "Informações salvas com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescriçãoDoença_Inicio1", "Sucesso ao salvar informações da doença")
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoDoença_Inicio1", "Erro ao salvar informações da doença", e)
                Toast.makeText(this, "Erro ao salvar informações. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
    }

}
