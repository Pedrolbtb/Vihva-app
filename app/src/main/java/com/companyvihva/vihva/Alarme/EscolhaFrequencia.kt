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
import android.widget.TextView
import com.companyvihva.vihva.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.w3c.dom.Text

class EscolhaFrequencia : AppCompatActivity() {

    private var frequencia: String? = null
    private var horasSelecionadas: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha_frequencia)

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Opcoes)
        frequencia = intent?.getStringExtra("frequencia")
        val horas = intent?.getIntExtra("horaemhora", 0)
        val duracao = intent?.getStringExtra("duracao")
        val data = intent?.getStringExtra("data")
        val estoque = intent?.getStringExtra("estoque")
        val lembreme = intent?.getStringExtra("lembreme")
        val tipomed = intent?.getStringExtra("tipomed")
        val switchEstoqueChecked = intent?.getBooleanExtra("switchEstoque", false) ?: false
        val nome = intent.getStringExtra("remedioId")



        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, ConfigFrequencia::class.java).apply {
                putExtra("duracao", duracao)
                putExtra("data", data)
                putExtra("estoque", estoque)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("remedioId", nome)
                putExtra("switchEstoque", switchEstoqueChecked)
            }
            startActivity(intent)
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

            if(frequencia == "Diariamente"){
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.diariamente_opcoes, null)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 20, 0)
                layoutToAdd.layoutParams = params
                parentLayout.addView(layoutToAdd)

                var textViewDiariamente = findViewById<TextView>(R.id.textView_diariamenteAlarme).setOnClickListener {
                    val picker =
                        MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(12)
                            .setMinute(10)
                            .setTitleText("Selecione uma hora para tocar")
                            .build()

                    picker.addOnPositiveButtonClickListener {
                        var textViewDiariamente = findViewById<TextView>(R.id.textView_diariamenteAlarme)
                        horasSelecionadas = picker.hour // Captura a hora selecionada
                        val formattedHour = String.format("%02d", picker.hour)
                        val formattedMinute = String.format("%02d", picker.minute)
                        val selectedTime = "$formattedHour:$formattedMinute"
                        textViewDiariamente.text = selectedTime
                    }

                    picker.show(supportFragmentManager, "TAG_TIME_PICKER")
                }
            }

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
            intent.putExtra("data", data)
            intent.putExtra("duracao", duracao)
            intent.putExtra("frequencia", frequenciaSelecionada)
            intent.putExtra("horaemhora", horaemhora)
            intent.putExtra("lembreme", lembreme)
            intent.putExtra("tipomed", tipomed)
            intent.putExtra("estoque", estoque)
            intent.putExtra("remedioId", nome)
            intent.putExtra("switchEstoque", switchEstoqueChecked)

            // Inicia a próxima atividade
            startActivity(intent)
            finish()
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