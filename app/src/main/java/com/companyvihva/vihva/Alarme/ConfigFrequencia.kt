package com.companyvihva.vihva.Alarme

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import com.companyvihva.vihva.R
import com.companyvihva.vihva.alarme.CriaAlarme
import com.companyvihva.vihva.databinding.ActivityConfigFrequenciaBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.DecimalFormat
import java.util.Calendar

class ConfigFrequencia : AppCompatActivity() {

    private lateinit var select_hora:Button
    private lateinit var hora_apartir:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_frequencia)

        // Recuperando dados da intent
        val frequencia = intent.getStringExtra("frequencia")
        val horaemhora = intent.getStringExtra("horaemhora")
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")
        val estoque = intent.getStringExtra("estoque")
        var horaDiariamente = intent.getStringExtra("horaDiariamente")
        val lembreme = intent.getStringExtra("lembreme")
        val tipomed = intent.getStringExtra("tipomed")
        val switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        val nome = intent.getStringExtra("remedioId")

        // Configurando listener para abrir ConfigDuracao
        findViewById<View>(R.id.container_DuracaoAlarme).setOnClickListener {
            val intent = Intent(this, ConfigDuracao::class.java).apply {
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

        // Configurando listener para abrir EscolhaFrequencia
        findViewById<View>(R.id.container_Frequencia).setOnClickListener {
            val intent = Intent(this, EscolhaFrequencia::class.java).apply {
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horaemhora)
                putExtra("duracao", duracao)
                putExtra("data", data)
                putExtra("estoque", estoque)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("remedioId", nome)
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("switchEstoque", switchEstoqueChecked)
            }
            startActivity(intent)
        }

        // Configurando listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, CriaAlarme::class.java).apply {
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

        // Atualizando as descrições na UI, se os dados estiverem disponíveis
        val descFrequencia = findViewById<TextView>(R.id.descFrequencia)
        val descDuracao = findViewById<TextView>(R.id.descduracao)

        frequencia?.let { descFrequencia.text = it }
        if (duracao != null && data != null) {
            descDuracao.text = "$duracao $data"
        }
        select_hora = findViewById(R.id.select_hora)
        hora_apartir= findViewById(R.id.hora_apartir)

        select_hora.setOnClickListener{
            val horas = Calendar.HOUR
            val minutos =  Calendar.MINUTE
            val timePicker = TimePickerDialog( this,{ _ , horas , minutos ->
                hora_apartir.text ="$horas:$minutos"}, horas, minutos, true)
            timePicker.show()
        }
    }
}





