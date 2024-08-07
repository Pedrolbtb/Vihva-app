package com.companyvihva.vihva.Alarme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore

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
    private var intervalo = intent.getStringExtra("intervalo") ?: ""
    private var dias = intent.getStringExtra("dias") ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_duracao)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        // Recuperando dados da intent
        frequencia = intent.getStringExtra("frequencia") ?: ""
        horaemhora = intent.getStringExtra("horaemhora") ?: ""
        intervalo = intent.getStringExtra("intervalo") ?: ""
        dias = intent.getStringExtra("dias") ?: ""
        duracao = intent.getStringExtra("duracao")
        data = intent.getStringExtra("data") ?: ""
        horaDiariamente = intent.getStringExtra("horaDiariamente") ?: ""
        estoque = intent.getStringExtra("estoque") ?: ""
        lembreme = intent.getStringExtra("lembreme") ?: ""
        tipomed = intent.getStringExtra("tipomed") ?: ""
        switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        nome = intent.getStringExtra("remedioId") ?: ""

        // Referência para o layout pai onde os novos layouts serão adicionados dinamicamente
        val parentLayout = findViewById<LinearLayout>(R.id.layout_Duracao)

        // Referência para o RadioGroup de seleção de frequência
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)

        // Verificando e marcando a opção correta com base no valor passado pela intent
        if (duracao != null) {
            when (duracao) {
                "Sem data para acabar" -> radioGroup.check(R.id.radio_intervalo)
                "Até" -> radioGroup.check(R.id.radio_dias)
            }
            if (duracao == "Até") {
                parentLayout.removeAllViews()
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.duracao_opcoes, parentLayout, false)

                // Configurando parâmetros de layout
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 25, 0, 0)
                layoutToAdd.layoutParams = params

                // Adicionando o layout ao parentLayout
                parentLayout.addView(layoutToAdd)
                val textViewCalendario =
                    layoutToAdd.findViewById<TextView>(R.id.textView_diariamenteAlarme)
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

            // Remove todas as views antes de adicionar novas
            parentLayout.removeAllViews()

            // Verifica se a opção selecionada é "Até" para adicionar o layout de opções
            if (duracao == "Até") {
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.duracao_opcoes, parentLayout, false)

                // Configurando parâmetros de layout
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 25, 0, 0)
                layoutToAdd.layoutParams = params

                // Adicionando o layout ao parentLayout
                parentLayout.addView(layoutToAdd)

                // Configurando clique para abrir o DatePicker
                val textViewCalendario =
                    layoutToAdd.findViewById<TextView>(R.id.textView_diariamenteAlarme)
                textViewCalendario.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecione até quando vão os avisos")
                        .build()

                    datePicker.addOnPositiveButtonClickListener { selectedDate ->
                        // Formatando a data selecionada
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formattedDate = sdf.format(Date(selectedDate))

                        // Definindo a data formatada na TextView
                        textViewCalendario.text = formattedDate
                    }

                    datePicker.show(supportFragmentManager, "DatePicker")
                }

                // Se data foi passada pelo Intent, mostrar no campo
                if (data.isNotEmpty()) {
                    textViewCalendario.text = data
                }
            }
        }

        // Configurando o listener para o botão de voltar
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

    fun SalvaeAgenda() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val intent = Intent(this, ConfigDuracao::class.java).apply {
            putExtra("frequencia", frequencia)
            when (frequencia) {
                "Diariamente" -> {
                    val eventFrequencia = hashMapOf(
                        "frequencia" to horaDiariamente,
                        "tipomed" to tipomed,
                        "lembreme" to lembreme,
                        "remedioId" to nome,
                        "data" to formattedDate,

                        when (duracao) {
                            "Sem data para acabar" -> {
                                val eventDuracao = hashMapOf(

                                    "duracao" to formattedDate }
                            "Intervalo" -> {
                                "frequencia" to horaemhora)
                            }

                        }
                }

                "Intervalo" -> {
                    val eventFrequencia = hashMapOf(
                    "frequencia" to horaemhora,
                        "tipomed" to tipomed,
                        "lembreme" to lembreme,
                        "remedioId" to nome,
                        "data" to formattedDate
                    )
                }
            }



            startActivity(intent)
        }
    }
}

