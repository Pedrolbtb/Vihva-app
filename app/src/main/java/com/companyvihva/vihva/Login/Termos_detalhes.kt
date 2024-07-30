package com.companyvihva.vihva.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.companyvihva.vihva.R

class Termos_detalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termos_detalhes)


        val btnBack = findViewById<ImageButton>(R.id.btnVoltarTermos)
        btnBack.setOnClickListener {
            // Volta para a tela anterior
            finish()
        }
    }
}