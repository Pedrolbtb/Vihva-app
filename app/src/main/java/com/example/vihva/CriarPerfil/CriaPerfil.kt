package com.example.vihva.CriarPerfil

import android.content.Intent
import android.os.Handler
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

        // Referenciando os EditTexts e o botão
        val editNome = findViewById<EditText>(R.id.edit_nome)
        val editSobrenome = findViewById<EditText>(R.id.edit_sobrenome)
        val editIdade = findViewById<EditText>(R.id.edit_idade)
        val btnProximo = findViewById<Button>(R.id.btn_proximo)
        val breadDados = findViewById<TextView>(R.id.breadDados)

        // Definindo o evento de clique para o botão
        btnProximo.setOnClickListener {

            // Obtendo os textos digitados pelo usuário
            val nome = editNome.text.toString()
            val sobrenome = editSobrenome.text.toString()
            val idadeText = editIdade.text.toString()

            // Verificando se todos os campos estão preenchidos
            if (nome.isNotEmpty() && sobrenome.isNotEmpty() && idadeText.isNotEmpty()) {
                val idade = idadeText.toIntOrNull()

                // Verificando se a idade é válida (entre 13 e 100 anos)
                if (idade != null && idade in 13..100) {

                    // Se todos os campos estiverem preenchidos e a idade for válida, navegar para a próxima tela
                    irParaTelaCriaPerfil2(nome, sobrenome, idade)

                    // Adicionando um atraso para a troca de cor do TextView breadDados
                    val cor = resources.getColor(R.color.telaPassada)
                    Handler().postDelayed({
                        breadDados.setOnClickListener {
                            irParaTelaCriaPerfil2(nome, sobrenome, idade)
                        }
                        breadDados.setTextColor(cor)
                    }, 1000)
                } else {
                    // Se a idade não for válida, exibir um Toast informando o usuário
                    showToast("Insira uma idade válida (entre 13 e 100 anos)")
                }
            } else {
                // Se algum campo estiver vazio, exibir um Toast informando o usuário
                showToast("Preencha todos os campos")
            }
        }

    }

    // Função para navegar para a tela CriaPerfil2
    private fun irParaTelaCriaPerfil2(nome: String, sobrenome: String, idade: Int) {
        val intent = Intent(this, CriaPerfil2::class.java)
        val altura = intent.getIntExtra("altura",0)
        val peso = intent.getIntExtra("peso",0)
        val genero = intent.getStringExtra("genero")
        intent.putExtra("nome", nome)
        intent.putExtra("sobrenome", sobrenome)
        intent.putExtra("idade", idade)
        intent.putExtra("altura", altura)
        intent.putExtra("peso", peso)
        intent.putExtra("genero", genero)
        startActivity(intent)
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
