package com.companyvihva.vihva.Alarme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ConfigDuracao : AppCompatActivity() {

    private var duracao: String? = null
    private var formattedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_duracao)

        // Recuperando dados da intent
        val frequencia = intent.getStringExtra("frequencia")
        val horas = intent.getIntExtra("horaemhora", 0)
        duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")

        // Referência para o layout pai onde os novos layouts serão adicionados dinamicamente
        val parentLayout = findViewById<LinearLayout>(R.id.layout_Duracao)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        // Referência para o RadioGroup de seleção de frequência
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)

        // Verificando e marcando a opção correta com base no valor passado pela intent
        if (duracao != null) {
            when (duracao) {
                "Sem data para acabar" -> radioGroup.check(R.id.radio_intervalo)
                "Até" -> radioGroup.check(R.id.radio_dias)
            }
            if (duracao == "Até"){
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
                    layoutToAdd.findViewById<TextView>(R.id.textView_calendarioAlarme)
                textViewCalendario.text = data
            }
        }

        // Configurando o listener para mudanças no RadioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
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
                    layoutToAdd.findViewById<TextView>(R.id.textView_calendarioAlarme)
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
                if (data != null) {
                    textViewCalendario.text = data
                }
            }
        }

        // Configurando o listener para o botão de salvar frequência
        findViewById<Button>(R.id.btn_salvarFrequencia).setOnClickListener {
            val atexdata = formattedDate
            val duracaoSelecionada = duracao

            // Criando intent para retornar os dados para a activity ConfigFrequencia
            val intent = Intent(this, ConfigFrequencia::class.java)
            intent.putExtra("data", atexdata)
            intent.putExtra("duracao", duracaoSelecionada)
            intent.putExtra("frequencia", frequencia)
            intent.putExtra("horaemhora", horas)
            startActivity(intent)
        }
    }

    // Método para exibir um Toast personalizado
    private fun showToast(message: String) {
        // Inflando o layout do Toast
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast))

        // Configurando o texto do Toast
        val text = layout.findViewById<TextView>(R.id.toast)
        text.text = message

        // Exibindo o Toast personalizado
        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}
