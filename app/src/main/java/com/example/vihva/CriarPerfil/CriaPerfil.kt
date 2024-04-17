package com.example.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.R
import com.example.vihva.databinding.ActivityFotoBioBinding

class CriaPerfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        // Referenciando o EditText e o botão
        val editNome = findViewById<EditText>(R.id.edit_nome)
        val editSobrenome = findViewById<EditText>(R.id.edit_sobrenome)
        val editidade = findViewById<EditText>(R.id.edit_idade)
        val btnProximo = findViewById<Button>(R.id.btn_proximo)

        // Definindo o evento de clique para o botão
        btnProximo.setOnClickListener {

            // Obtendo os textos digitados pelo usuário
            val nome = editNome.text.toString()
            val sobrenome = editSobrenome.text.toString()
            val idade = editidade.text.toString().toIntOrNull()

            //Criando a Intent e passando os dados como extras
            val intent = Intent(this, FotoBio::class.java)
            intent.putExtra("nome",nome)
            intent.putExtra("sobrenome",sobrenome)
            intent.putExtra("idade",idade)
            startActivity(intent)

            // Verificando se a idade é válida (entre 13 e 100 anos)
            if (idade != null && idade in 13..100) {
                // Se a idade for válida, navegar para a próxima tela
                irParaTelaCriaPerfil2()
            } else {
                // Se a idade não for válida, exibir um Toast informando o usuário
                showToast("Insira uma idade válida")
            }
        }
    }

    // Função para navegar para a tela CriaPerfil2
    private fun irParaTelaCriaPerfil2() {
        val telaL = Intent(this, CriaPerfil2::class.java)
        startActivity(telaL)
    }

    // Função para exibir um Toast personalizado
    private fun showToast(message: String) {
        // Inflando o layout do Toast
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast))

        // Configurando o texto do Toast
        val text = layout.findViewById<TextView>(R.id.toast)
        text.text = message

        // Exibindo o Toast personalizado
        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}
