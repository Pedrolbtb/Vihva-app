package com.companyvihva.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.google.firebase.firestore.FirebaseFirestore

class CriaPerfil : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        val altura = intent.getIntExtra("altura",0)
        val peso = intent.getIntExtra("peso",0)
        val genero = intent.getStringExtra("genero")
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade",0)

        // Referenciando os EditTexts e o botão
        val editNome = findViewById<EditText>(R.id.edit_nome)
        val editSobrenome = findViewById<EditText>(R.id.edit_sobrenome)
        val editIdade = findViewById<EditText>(R.id.edit_idade)
        val btnProximo = findViewById<Button>(R.id.btn_proximo)
        val breadDados = findViewById<TextView>(R.id.breadDados)

        if (nome != null){

            editNome.setText("$nome")
            editSobrenome.setText("$sobrenome")
            editIdade.setText("$idade")

            breadDados.setTextColor(resources.getColor(R.color.telaPassada))

            breadDados.setOnClickListener {
                val intent = Intent(this, CriaPerfil2::class.java)
                intent.putExtra("altura", altura)
                intent.putExtra("peso", peso)
                intent.putExtra("genero", genero)
                intent.putExtra("nome", nome)
                intent.putExtra("sobrenome", sobrenome)
                intent.putExtra("idade", idade)

                startActivity(intent)
            }
        }

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
                    val intent = Intent(this, CriaPerfil2::class.java)
                    intent.putExtra("nome", nome)
                    intent.putExtra("sobrenome", sobrenome)
                    intent.putExtra("idade", idade)
                    intent.putExtra("altura", altura)
                    intent.putExtra("peso", peso)
                    intent.putExtra("genero", genero)
                    startActivity(intent)

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

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
