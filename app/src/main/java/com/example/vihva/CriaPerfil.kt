package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CriaPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        findViewById<Button>(R.id.btn_proximo).setOnClickListener {
            irParaTelaCriaPerfil2()
        }
    }

    private fun irParaTelaCriaPerfil2() {

        val telaL = Intent(this, CriaPerfil2::class.java)
        startActivity(telaL)
    }
}