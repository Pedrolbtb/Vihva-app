package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        findViewById<Button>(R.id.btn_pac).setOnClickListener {

            irParaTelaLoginP()

        }

    }

    //função do botao paciente ir pra tela de login
    private fun irParaTelaLoginP() {

        val telaL = Intent(this, CadastroPac::class.java)
        startActivity(telaL)
    }
}