package com.companyvihva.vihva.Alarme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.companyvihva.vihva.alarme.CriaAlarme
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ConfigDuracao : AppCompatActivity() {

    private var duracao: String? = null
    private var formattedDate: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Variáveis para armazenar dados da intent
    private lateinit var frequencia: String
    private lateinit var horaemhora: String
    private lateinit var data: String
    private lateinit var horaDiariamente: String
    private lateinit var estoque: String
    private lateinit var lembreme: String
    private lateinit var tipomed: String
    private var switchEstoqueChecked: Boolean = false
    private lateinit var nome: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_duracao)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Recuperando dados da intent
        frequencia = intent.getStringExtra("frequencia") ?: ""
        horaemhora = intent.getStringExtra("horaemhora") ?: ""
        duracao = intent.getStringExtra("duracao")
        data = intent.getStringExtra("data") ?: ""
        horaDiariamente = intent.getStringExtra("horaDiariamente") ?: ""
        estoque = intent.getStringExtra("estoque") ?: ""
        lembreme = intent.getStringExtra("lembreme") ?: ""
        tipomed = intent.getStringExtra("tipomed") ?: ""
        switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        nome = intent.getStringExtra("remedioId") ?: ""

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Duracao)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)

        // Verificando e marcando a opção correta com base no valor passado pela intent
        duracao?.let {
            when (it) {
                "Sem data para acabar" -> radioGroup.check(R.id.radio_intervalo)
                "Até" -> radioGroup.check(R.id.radio_dias)
            }
            if (it == "Até") {
                parentLayout.removeAllViews()
                val layoutToAdd = layoutInflater
                    .inflate(R.layout.duracao_opcoes, parentLayout, false)
                parentLayout.addView(layoutToAdd)
                val textViewCalendario = layoutToAdd.findViewById<TextView>(R.id.textView_diariamenteAlarme)
                textViewCalendario.text = data
            }
        }

        // Configurando o listener para mudanças no RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            duracao = when (checkedId) {
                R.id.radio_intervalo -> "Sem data para acabar"
                R.id.radio_dias -> "Até"
                else -> null
            }
            parentLayout.removeAllViews()

            if (duracao == "Até") {
                val layoutToAdd = layoutInflater
                    .inflate(R.layout.duracao_opcoes, parentLayout, false)
                parentLayout.addView(layoutToAdd)
                val textViewCalendario = layoutToAdd.findViewById<TextView>(R.id.textView_diariamenteAlarme)
                textViewCalendario.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecione até quando vão os avisos")
                        .build()
                    datePicker.addOnPositiveButtonClickListener { selectedDate ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formattedDate = sdf.format(Date(selectedDate))
                        textViewCalendario.text = formattedDate
                    }
                    datePicker.show(supportFragmentManager, "DatePicker")
                }
                if (data.isNotEmpty()) {
                    textViewCalendario.text = data
                }
            }
        }

        val btnProximo: Button = findViewById(R.id.btn_proximo)
        btnProximo.setOnClickListener {
            SalvaeAgenda()
            agendarAlarme()
        }

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EscolhaFrequencia::class.java).apply {
            putExtra("data", formattedDate)
            putExtra("duracao", duracao)
            putExtra("frequencia", frequencia)
            putExtra("horaemhora", horaemhora)
            putExtra("horaDiariamente", horaDiariamente)
            putExtra("estoque", estoque)
            putExtra("lembreme", lembreme)
            putExtra("tipomed", tipomed)
            putExtra("switchEstoque", switchEstoqueChecked)
            putExtra("remedioId", nome)
        }
        startActivity(intent)
    }

    private fun SalvaeAgenda() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val eventFrequencia = hashMapOf(
            "tipomed" to tipomed,
            "lembreme" to lembreme,
            "remedioId" to nome,
            "data" to (formattedDate ?: "")
        )

        when (frequencia) {
            "Diariamente" -> {
                eventFrequencia["frequencia"] = horaDiariamente
            }
            "Intervalo" -> {
                eventFrequencia["frequencia"] = horaemhora
            }
        }

        when (duracao) {
            "Sem data para acabar" -> {
                eventFrequencia["duracao"] = "Sem data para acabar"
            }
            "Intervalo" -> {
                eventFrequencia["duracao"] = horaemhora
            }
        }

        bd(eventFrequencia)
    }

    private fun bd(eventFrequencia: HashMap<String, String>) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid

            val userDocRef = db.collection("clientes").document(userId)
            userDocRef.collection("Alarmes")
                .add(eventFrequencia)
                .addOnSuccessListener { documentReference ->
                    println("Alarme adicionado com sucesso.")
                    // Passa o ID do documento para a próxima Activity
                    val intent = Intent(this, ConfigEstoque::class.java).apply {
                        putExtra("documentId", documentReference.id)
                        // Passa os outros extras necessários
                        putExtra("frequencia", frequencia)
                        putExtra("horaemhora", horaemhora)
                        putExtra("duracao", duracao)
                        putExtra("data", formattedDate)
                        putExtra("horaDiariamente", horaDiariamente)
                        putExtra("estoque", estoque)
                        putExtra("lembreme", lembreme)
                        putExtra("tipomed", tipomed)
                        putExtra("switchEstoque", switchEstoqueChecked)
                        putExtra("remedioId", nome)
                    }
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun agendarAlarme() {
        val intent = Intent(this, CriaAlarme::class.java).apply {
            putExtra("frequencia", frequencia)
            putExtra("horaemhora", horaemhora)
            putExtra("duracao", duracao)
            putExtra("data", formattedDate)
            putExtra("horaDiariamente", horaDiariamente)
            putExtra("estoque", estoque)
            putExtra("lembreme", lembreme)
            putExtra("tipomed", tipomed)
            putExtra("switchEstoque", switchEstoqueChecked)
            putExtra("remedioId", nome)
        }
        startActivity(intent)
    }
}
