package com.example.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.R

class CriaPerfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        val editidade = findViewById<EditText>(R.id.edit_idade)
        val btnProximo = findViewById<Button>(R.id.btn_proximo)

        btnProximo.setOnClickListener {
            val idade = editidade.text.toString().toIntOrNull()

            if (idade != null && idade in 1..100) {
                irParaTelaCriaPerfil2()
            } else {
                // Chamando o toast personalizado
                showToast("Digite um valor entre 1 e 100")
            }
        }
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
