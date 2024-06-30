package com.companyvihva.vihva.Alarme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.CheckBox
import android.widget.EditText
import com.companyvihva.vihva.R

class EscolhaFrequencia : AppCompatActivity() {

    private var frequencia: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha_frequencia)

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Opcoes)
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")
        var frequencia = intent.getStringExtra("frequencia")
        val horas = intent.getIntExtra("horaemhora", 0)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        if (frequencia != null){
            when(frequencia){
                "Diariamente" -> radioGroup.check(R.id.radio_diariamente)
                "Sem Alarme" -> radioGroup.check(R.id.radio_nada)
                "Intervalo" -> radioGroup.check(R.id.radio_intervalo)
                "Somente em certos dias" -> radioGroup.check(R.id.radio_dias)
            }
            if (frequencia == "Intervalo"){

            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            frequencia = when (checkedId) {
                R.id.radio_diariamente -> "Diariamente"
                R.id.radio_nada -> "Sem Alarme"
                R.id.radio_intervalo -> "Intervalo"
                R.id.radio_dias -> "Somente em certos dias"
                else -> null
            }

            // Remove todas as views antes de adicionar novas
            parentLayout.removeAllViews()

            if (frequencia == "Intervalo") {
                // Adiciona layout de intervalo
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.intervalo_opcoes, null)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 20, 0)
                layoutToAdd.layoutParams = params
                parentLayout.addView(layoutToAdd)

            } else if (frequencia == "Somente em certos dias") {
                // Adiciona layout de dias opções desativado
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.dias_opcoes_desativado, null)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(23, 0, 0, 0)
                layoutToAdd.layoutParams = params
                parentLayout.addView(layoutToAdd)

                // Configura estilo das CheckBox baseado no estado
                val checkBoxIds = listOf(
                    R.id.checkbox_domingo,
                    R.id.checkbox_segunda,
                    R.id.checkbox_terca,
                    R.id.checkbox_quarta,
                    R.id.checkbox_quinta,
                    R.id.checkbox_sexta,
                    R.id.checkbox_sabado
                )

                checkBoxIds.forEach { checkBoxId ->
                    val checkBox = findViewById<CheckBox>(checkBoxId)
                    checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            checkBox.setBackgroundResource(R.drawable.checkbox_dias)
                        } else {
                            checkBox.setBackgroundResource(R.drawable.checkbox_dias_desativado)
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.btn_salvarFrequencia).setOnClickListener {
            val dados = if (frequencia == "Somente em certos dias") {
                prepararDadosParaEnvio()
            } else {
                Bundle()
            }
            val frequenciaSelecionada = frequencia
            val horaemhora: String? = if (frequencia == "Intervalo") {
                val layoutIntervalo = parentLayout.findViewById<LinearLayout>(R.id.layout_intervalo)
                val editTextHora = layoutIntervalo.findViewById<EditText>(R.id.editTextHoraemHora)
                editTextHora.text.toString()
            } else {
                null
            }

            // Cria um Intent para iniciar a próxima atividade
            val intent = Intent(this, ConfigFrequencia::class.java)
            intent.putExtras(dados)
            intent.putExtra("frequencia", frequenciaSelecionada)
            intent.putExtra("horaemhora", horaemhora)
            intent.putExtra("data", data)
            intent.putExtra("duracao", duracao)

            // Inicia a próxima atividade
            startActivity(intent)
        }
    }

    private fun prepararDadosParaEnvio(): Bundle {
        // Aqui você prepara os dados com base nas seleções feitas pelo usuário
        val dias = Bundle()
        val checkBoxIds = listOf(
            R.id.checkbox_domingo,
            R.id.checkbox_segunda,
            R.id.checkbox_terca,
            R.id.checkbox_quarta,
            R.id.checkbox_quinta,
            R.id.checkbox_sexta,
            R.id.checkbox_sabado
        )

        checkBoxIds.forEach { checkBoxId ->
            val checkBox = findViewById<CheckBox>(checkBoxId)
            checkBox?.let {
                dias.putBoolean(checkBox.text.toString(), checkBox.isChecked)
            }
        }
        return dias
    }
}
