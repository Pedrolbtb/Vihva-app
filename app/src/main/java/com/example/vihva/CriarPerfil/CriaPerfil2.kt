package com.example.vihva.CriarPerfil

import android.R.attr.name
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vihva.R


class CriaPerfil2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil2)

        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade",0)
        val altura = intent.getIntExtra("altura",0)
        val peso = intent.getIntExtra("peso",0)
        val genero = intent.getStringExtra("genero")



        // Inicializando as variáveis com referências aos elementos da UI
        val edit_altura: EditText = findViewById<EditText>(R.id.edit_altura)
        val edit_peso = findViewById<EditText>(R.id.edit_peso)
        val radio_group = findViewById<RadioGroup>(R.id.radioGroup)


        if (altura > 0) {

            edit_altura.setText("$altura")
            edit_peso.setText("$peso")
            when (genero) {
                "Feminino" -> radio_group.check(R.id.radio_fem)
                "Masculino" -> radio_group.check(R.id.radio_masc)
                "Prefiro não dizer" -> radio_group.check(R.id.radio_gen)
            }
        }

        findViewById<TextView>(R.id.breadNome).setOnClickListener {
            irParaCriaPerfil1()
        }


        // Configurando o clique no botão de "Próximo"
        findViewById<Button>(R.id.btn_proximo).setOnClickListener {

            // Obtendo os valores de altura, peso e gênero
            val altura = edit_altura.text.toString().toIntOrNull()
            val peso = edit_peso.text.toString().toIntOrNull()
            val genero = when (findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId){
                R.id.radio_fem -> "Feminino"
                R.id.radio_masc -> "Masculino"
                R.id.radio_gen -> "Prefiro não dizer"
                else -> null
            }

            // Validando os dados inseridos pelo usuário
            if (altura != null && altura >= 100 && altura <= 300 && peso != null && genero != null){
                // Criando a Intent e iniciando a próxima atividade
                val intent = Intent(this, FotoBio::class.java)
                intent.putExtra("altura", altura)
                intent.putExtra("peso", peso)
                intent.putExtra("genero", genero)
                intent.putExtra("nome", nome)
                intent.putExtra("sobrenome", sobrenome)
                intent.putExtra("idade", idade)// Adicionando o valor do gênero à Intent
                startActivity(intent)
                finish() // Finalizando a atividade atual
            } else {
                // Exibindo um Toast caso os dados não sejam válidos
                showToast("Digite um valor de altura entre 100cm e 300cm, insira um peso e selecione um gênero")
            }
        }
    }

    // Função para navegar para a tela anterior de criação de perfil
    private fun irParaCriaPerfil1() {
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade", 0)
        val altura = intent.getIntExtra("altura", 0)
        val peso = intent.getIntExtra("peso", 0)

        val genero = when (findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
            R.id.radio_fem -> "Feminino"
            R.id.radio_masc -> "Masculino"
            R.id.radio_gen -> "Sem genero"
            else -> null
        }

        val telaL = Intent(this, CriaPerfil::class.java)
        telaL.putExtra("nome", nome)
        telaL.putExtra("sobrenome", sobrenome)
        telaL.putExtra("idade", idade)
        telaL.putExtra("altura", altura)
        telaL.putExtra("peso", peso)
        telaL.putExtra("genero", genero)

        setResult(RESULT_OK, telaL) // Define os dados de resultado e o código de resultado
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