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
        private lateinit var horaApartirTextView: TextView

class ConfigFrequencia : AppCompatActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_frequencia)

        val frequencia = intent.getStringExtra("frequencia")
        val horas = intent.getStringExtra("horaemhora")
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")

        var container_duraacao = findViewById<View>(R.id.container_DuracaoAlarme).setOnClickListener {
            irParaDuracao()
        }

        val container_frequencia = findViewById<View>(R.id.container_Frequencia).setOnClickListener {
            irParaFrequencia()
        }

        val container_apartir = findViewById<View>(R.id.container_apartir).setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(10)
                    .build()

            horaApartirTextView = findViewById(R.id.hora_apartir)

            picker.addOnPositiveButtonClickListener {
                // Armazena a hora e minuto selecionados em SharedPreferences
                val sharedPref = getSharedPreferences("TimePickerPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("selected_hour", picker.hour)
                    putInt("selected_minute", picker.minute)
                    apply()
                }
                displayStoredTime()
            }

                     picker.show(supportFragmentManager, "MaterialTimePicker")
        }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish() // Fecha a atividade atual e retorna para a anterior
        }

        var descfrequencia = findViewById<TextView>(R.id.descFrequencia)
        var descduracao = findViewById<TextView>(R.id.descduracao)

        if (frequencia != null) {
            descfrequencia.setText(frequencia)
        }
        if (duracao != null){
            descduracao.setText(duracao + " " + data)
        }
        }



    private fun irParaFrequencia() {
        val telaFrequencia = Intent(this, EscolhaFrequencia::class.java)
        startActivity(telaFrequencia)
    }

    private fun irParaDuracao() {
        val telaDuracao = Intent(this, ConfigDuracao::class.java)
        startActivity(telaDuracao)
    }

    private fun displayStoredTime() {
        val sharedPref = getSharedPreferences("TimePickerPrefs", Context.MODE_PRIVATE)
        val hour = sharedPref.getInt("selected_hour", 0)
        val minute = sharedPref.getInt("selected_minute", 0)
        horaApartirTextView.text = String.format("%02d:%02d", hour, minute)
    }
}