
package com.example.vihva.CriarPerfil

// Importação das classes necessárias
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.vihva.R

// Definição da classe de atividade CriaPerfil
class CriaPerfil : AppCompatActivity() {

    // Sobrescreve o método onCreate da classe pai. Este método é chamado quando a atividade é criada.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o layout XML activity_cria_perfil como o layout da atividade. Este layout contém a interface do usuário da atividade.
        setContentView(R.layout.activity_cria_perfil)

        // Obtém uma referência para o EditText com o ID edit_idade.
        val editidade = findViewById<EditText>(R.id.edit_idade)
        // Obtém uma referência para o Button com o ID btn_proximo.
        val btnProximo = findViewById<Button>(R.id.btn_proximo)

        // Define um ouvinte de clique para o botão btn_proximo.
        btnProximo.setOnClickListener {
            // Obtém o texto digitado no EditText edit_nascimento e tenta converter para Int.
            val idade = editidade.text.toString().toIntOrNull()

            // Verifica se o valor digitado está entre 1 e 100.
            if (idade != null && idade in 1..100) {
                // Chama a função irParaTelaCriaPerfil2() se o valor estiver entre 1 e 100.
                irParaTelaCriaPerfil2()
            } else { // Caso contrário...
                // Exibe um Toast indicando que o usuário deve digitar um valor entre 1 e 100.
                Toast.makeText(this, "Digite um valor entre 1 e 100", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Define uma função privada chamada irParaTelaCriaPerfil2.
    private fun irParaTelaCriaPerfil2() {
        // Cria uma nova Intent para iniciar a atividade CriaPerfil2.
        val telaL = Intent(this, CriaPerfil2::class.java)
        // Inicia a atividade CriaPerfil2.
        startActivity(telaL)
    }
}
