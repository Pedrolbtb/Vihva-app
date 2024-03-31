package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text_tela_cadastro).setOnClickListener {

            irParaTelaCadastro()
        }
    }

    private fun irParaTelaCadastro() {

        val telaL = Intent(this, CadastroPac::class.java)
        startActivity(telaL)
    }
}