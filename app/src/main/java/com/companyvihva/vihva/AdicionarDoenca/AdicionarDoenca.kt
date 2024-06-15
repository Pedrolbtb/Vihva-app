package com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.companyvihva.vihva.R

class AdicionarDoenca : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_doenca)

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltarAddDoenca)
        btnVoltar.setOnClickListener {
            finish()
        }
    }
}