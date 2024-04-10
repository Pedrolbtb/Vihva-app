
package com.example.vihva.CriarPerfil

// Importação das classes necessárias
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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

        criarBreadcump()


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



    // Lista de itens do breadcrumb
private fun criarBreadcump(){

        val breadcrumbItems = arrayOf("Identidade", "Informações", "Opcionais")
        val breadcrumbLayout: LinearLayout = findViewById(R.id.breadcrumbLayout)
        val pagAtual = "Identidade"

        // Adiciona os itens ao breadcrumb
        breadcrumbItems.forEachIndexed { index, item ->
        val textView = TextView(this)
        textView.text = item
        textView.setPadding(8, 8, 8, 8)

            if (item == pagAtual) {
                textView.setTypeface(null, Typeface.BOLD)
            }

        breadcrumbLayout.addView(textView)

        // Adiciona uma seta entre os itens (exceto o último)
        if (index != breadcrumbItems.size - 1) {
            val arrowView = TextView(this)
            arrowView.text = " > "
            arrowView.setPadding(8, 8, 8, 8)
            breadcrumbLayout.addView(arrowView)


            }
        }
    }
    }
    fun updateBreadcrumb(breadcrumbLayout: LinearLayout, currentScreen: String, previousScreens: List<String>) {
        breadcrumbLayout.removeAllViews()

        // Adiciona telas anteriores à breadcrumb
        previousScreens.forEachIndexed { index, item ->
            val textView = TextView(breadcrumbLayout.context)
            textView.text = item
            textView.setPadding(8, 8, 8, 8)
            breadcrumbLayout.addView(textView)

            // Adiciona uma seta entre os itens (exceto o último)
            if (index != previousScreens.size - 1) {
                val arrowView = TextView(breadcrumbLayout.context)
                arrowView.text = " > "
                arrowView.setPadding(8, 8, 8, 8)
                breadcrumbLayout.addView(arrowView)

            }
        }

        // Adiciona a tela atual à breadcrumb
        val currentTextView = TextView(breadcrumbLayout.context)
        currentTextView.text = currentScreen
        currentTextView.setPadding(8, 8, 8, 8)
    }

