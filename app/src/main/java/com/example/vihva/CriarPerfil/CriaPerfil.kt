package com.example.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import com.example.vihva.Login.Login
import com.example.vihva.R

class CriaPerfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        val editidade = findViewById<EditText>(R.id.edit_idade)
        val btnProximo = findViewById<Button>(R.id.btn_proximo)

        btnProximo.setOnClickListener {
            val idade = editidade.text.toString().toIntOrNull()

            if (idade != null && idade in 13..100) {
                irParaTelaCriaPerfil2()
            } else {
                // Chamando o toast personalizado
                showToast("insira uma idade válida")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Login::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_direita, // a animação para a tela de onde você está voltando
            R.anim.slide_esquerda // a animação para a tela para onde você está voltando
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun irParaTelaCriaPerfil2() {
        val telaL = Intent(this, CriaPerfil2::class.java)
        startActivity(telaL)
    }

    // Função para exibir um Toast personalizado
    private fun showToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast))

        val text = layout.findViewById<TextView>(R.id.toast)
        text.text = message

        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}
