package com.companyvihva.vihva.Alarme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ConfigDuracao : AppCompatActivity() {

    private var duracao: String? = null
    private var formattedDate: String? = null // Variável de classe para armazenar a data formatada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_duracao)

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Duracao)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            duracao = when (checkedId) {
                R.id.radio_intervalo -> "Sem data para acabar"
                R.id.radio_dias -> "Até"
                else -> null
            }

            // Remove todas as views antes de adicionar novas
            parentLayout.removeAllViews()

            if (duracao == "Até") {
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.duracao_opcoes, parentLayout, false)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 25, 0, 0)
                layoutToAdd.layoutParams = params
                parentLayout.addView(layoutToAdd)

                // Configurando clique para abrir o DatePicker
                val textViewCalendario = layoutToAdd.findViewById<TextView>(R.id.textView_calendarioAlarme)
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
            }
        }

        var btnSalvar = findViewById<Button>(R.id.btn_salvarFrequencia).setOnClickListener {
            val atexdata = formattedDate
            val duracaoSelecionada = duracao
            val intent = Intent(this, ConfigFrequencia::class.java)
            intent.putExtra("data", atexdata)
            intent.putExtra("duracao", duracaoSelecionada)

            startActivity(intent)
        }
    }
}
