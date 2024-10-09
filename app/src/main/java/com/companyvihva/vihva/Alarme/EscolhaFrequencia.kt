package com.companyvihva.vihva.Alarme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class EscolhaFrequencia : AppCompatActivity() {

    private var frequencia: String? = null
    private var horaDiariamente: String? = null
    private var horaemhora: String? = null
    private var duracao: String? = null
    private var data: String? = null
    private var estoque: String? = null
    private var lembreme: String? = null
    private var tipomed: String? = null
    private var switchEstoqueChecked: Boolean = false
    private var nome: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha_frequencia)

        intent?.let {
            frequencia = it.getStringExtra("frequencia")
            horaDiariamente = it.getStringExtra("horaDiariamente")
            horaemhora = it.getStringExtra("horaemhora")
            duracao = it.getStringExtra("duracao")
            data = it.getStringExtra("data")
            estoque = it.getStringExtra("estoque")
            lembreme = it.getStringExtra("lembreme")
            tipomed = it.getStringExtra("tipomed")
            nome = it.getStringExtra("remedioId")
            switchEstoqueChecked = it.getBooleanExtra("switchEstoque", false)
        }

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Opcoes)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        val btnProx: Button = findViewById(R.id.btn_proximo)

        btnProx.setOnClickListener {
            avancarParaConfigDuracao()
        }

        btnVoltar.setOnClickListener {
            avancarParaEscolhaRemedio()
        }

        frequencia?.let {
            when (it) {
                "Diariamente" -> radioGroup.check(R.id.radio_diariamente)
                "Sem Alarme" -> radioGroup.check(R.id.radio_nada)
                "Intervalo" -> radioGroup.check(R.id.radio_intervalo)
                "Somente em certos dias" -> radioGroup.check(R.id.radio_dias)
            }
            atualizarLayoutFrequencia(parentLayout)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            frequencia = when (checkedId) {
                R.id.radio_diariamente -> "Diariamente"
                R.id.radio_nada -> "Sem Alarme"
                R.id.radio_intervalo -> "Intervalo"
                R.id.radio_dias -> "Somente em certos dias"
                else -> null
            }
            atualizarLayoutFrequencia(parentLayout)
        }
    }

    private fun atualizarLayoutFrequencia(parentLayout: LinearLayout) {
        parentLayout.removeAllViews()

        when (frequencia) {
            "Diariamente" -> {
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.diariamente_opcoes, parentLayout, false)
                parentLayout.addView(layoutToAdd)

                layoutToAdd.findViewById<TextView>(R.id.textView_calendarioAlarme)
                    .setOnClickListener {
                        mostrarTimePicker(layoutToAdd)
                    }
            }

            "Intervalo" -> {
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.intervalo_opcoes, parentLayout, false)
                parentLayout.addView(layoutToAdd)
            }

            "Somente em certos dias" -> {
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.dias_opcoes_desativado, parentLayout, false)
                parentLayout.addView(layoutToAdd)

                configurarCheckBoxes(layoutToAdd)
            }
        }
    }

    private fun mostrarTimePicker(layoutToAdd: View) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Selecione uma hora para tocar")
            .build()

        picker.addOnPositiveButtonClickListener {
            val formattedHour = String.format("%02d", picker.hour)
            val formattedMinute = String.format("%02d", picker.minute)
            val selectedTime = "$formattedHour:$formattedMinute"
            layoutToAdd.findViewById<TextView>(R.id.textView_calendarioAlarme).text = selectedTime
            horaDiariamente = selectedTime
        }

        picker.show(supportFragmentManager, "TAG_TIME_PICKER")
    }

    private fun configurarCheckBoxes(layoutToAdd: View) {
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
            val checkBox = layoutToAdd.findViewById<CheckBox>(checkBoxId)
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                val backgroundRes = if (isChecked) {
                    R.drawable.checkbox_dias
                } else {
                    R.drawable.checkbox_dias_desativado
                }
                checkBox.setBackgroundResource(backgroundRes)
            }
        }
    }

    private fun avancarParaConfigDuracao() {
        val intent = Intent(this, ConfigDuracao::class.java).apply {
            putExtra("horaDiariamente", horaDiariamente)
            putExtra("data", data)
            putExtra("duracao", duracao)
            putExtra("frequencia", frequencia)
            putExtra("horaemhora", horaemhora)
            putExtra("lembreme", lembreme)
            putExtra("tipomed", tipomed)
            putExtra("estoque", estoque)
            putExtra("remedioId", nome)
            putExtra("switchEstoque", switchEstoqueChecked)
        }
        startActivity(intent)
    }

    private fun avancarParaEscolhaRemedio() {
        val dados = if (frequencia == "Somente em certos dias") {
            prepararDadosParaEnvio()
        } else {
            Bundle()
        }

        val intent = Intent(this, EscolhaRemedio::class.java).apply {
            putExtras(dados)
            putExtra("frequencia", frequencia)
            putExtra("horaemhora", horaemhora)
            putExtra("duracao", duracao)
            putExtra("data", data)
            putExtra("estoque", estoque)
            putExtra("lembreme", lembreme)
            putExtra("tipomed", tipomed)
            putExtra("remedioId", nome)
            putExtra("switchEstoque", switchEstoqueChecked)
            putExtra("horaDiariamente", horaDiariamente)
        }
        startActivity(intent)
    }

    private fun prepararDadosParaEnvio(): Bundle {
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

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
