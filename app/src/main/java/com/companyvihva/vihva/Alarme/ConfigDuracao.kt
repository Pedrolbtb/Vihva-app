package com.companyvihva.vihva.Alarme

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R

class ConfigDuracao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_duracao)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish() // Fecha a atividade atual e retorna para a anterior
        }
    }
}
