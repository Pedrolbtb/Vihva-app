package com.companyvihva.vihva.Alarme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.companyvihva.vihva.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ConfigFrequencia : AppCompatActivity() {

    private lateinit var horaApartirTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_frequencia)

        val frequencia = intent.getStringExtra("frequencia")
        val horas = intent.getIntExtra("horaemhora", 0)
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")

        val container_duracao = findViewById<View>(R.id.container_DuracaoAlarme).setOnClickListener {
            val telaDuracao = Intent(this, ConfigDuracao::class.java)
            telaDuracao.putExtra("data", data)
            telaDuracao.putExtra("duracao", duracao)
            telaDuracao.putExtra("frequencia", frequencia)
            telaDuracao.putExtra("horaemhora", horas)
            startActivity(telaDuracao)
        }

        val container_frequencia = findViewById<View>(R.id.container_Frequencia).setOnClickListener {
            val telaDuracao = Intent(this, EscolhaFrequencia::class.java)
            telaDuracao.putExtra("data", data)
            telaDuracao.putExtra("duracao", duracao)
            telaDuracao.putExtra("frequencia", frequencia)
            telaDuracao.putExtra("horaemhora", horas)
            startActivity(telaDuracao)
        }


        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val telaDuracao = Intent(this, CriaAlarme::class.java)
            telaDuracao.putExtra("data", data)
            telaDuracao.putExtra("duracao", duracao)
            telaDuracao.putExtra("frequencia", frequencia)
            telaDuracao.putExtra("horaemhora", horas)
            startActivity(telaDuracao)
        }

        val descFrequencia = findViewById<TextView>(R.id.descFrequencia)
        val descDuracao = findViewById<TextView>(R.id.descduracao)

        if (frequencia != null) {
            descFrequencia.text = frequencia
        }
        if (duracao != null && data != null) {
            descDuracao.text = "$duracao $data"
        }
    }
}
