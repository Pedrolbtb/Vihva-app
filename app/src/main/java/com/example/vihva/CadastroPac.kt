package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CadastroPac : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_pac)

        //text view indo para tela de login
        findViewById<TextView>(R.id.text_tela_cadastro).setOnClickListener {

            irParaTelaLoginP()
        }

        //botao de cadastro indo para tela de criação de perfil
        findViewById<Button>(R.id.btn_cadastro).setOnClickListener {

            irParaTelaCriaPerfil()
        }


    }

    //função para ir para tela de login
    private fun irParaTelaLoginP() {

        val telaL = Intent(this, MainActivity::class.java)
        startActivity(telaL)
    }

    //função para ir para tela de criaçãode perfil
    private fun irParaTelaCriaPerfil() {

        val telaL = Intent(this, CriaPerfil::class.java)
        startActivity(telaL)
    }
}