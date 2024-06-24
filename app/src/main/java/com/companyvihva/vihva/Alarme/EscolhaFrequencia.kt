package com.companyvihva.vihva.Alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.companyvihva.vihva.R

class EscolhaFrequencia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha_frequencia)

        val parentLayout = findViewById<LinearLayout>(R.id.layout_Opcoes)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup_frequencia)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            var frequencia = when (checkedId) {
                R.id.radio_diariamente -> "Diariamente"
                R.id.radio_nada -> "Sem Alarme"
                R.id.radio_intervalo -> "Intervalo"
                R.id.radio_dias -> "Somente em certos dias"
                else -> null
            }

            if (frequencia == "Intervalo") {
                Log.d("EscolhaFrequencia", "Frequência selecionada: $frequencia")

                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.intervalo_opcoes, null)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                layoutToAdd.layoutParams = params

                params.setMargins(0, 0, 20, 0)

                parentLayout.addView(layoutToAdd)
            } else {
                parentLayout.removeAllViews()
            }

            if(frequencia == "Somente em certos dias"){
                val layoutToAdd = LayoutInflater.from(this)
                    .inflate(R.layout.dias_opcoes, null)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                layoutToAdd.layoutParams = params

                params.setMargins(0, 0, 20, 0)

                parentLayout.addView(layoutToAdd)
            }
        }
    }
}


