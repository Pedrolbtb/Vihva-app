package com.companyvihva.vihva.Alarme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.companyvihva.vihva.R

class CriaAlarme : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_alarme)

        val container_ProgramacaoRemedio = findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            irParaConfigFrequencia()
        }

    }

    private fun irParaConfigFrequencia() {
        val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java)
        startActivity(telaConfigFrequencia)

    }
}
