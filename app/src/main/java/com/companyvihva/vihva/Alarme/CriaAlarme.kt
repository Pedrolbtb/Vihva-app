package com.companyvihva.vihva.Alarme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import org.w3c.dom.Text

class CriaAlarme : AppCompatActivity() {

    private var nome: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_alarme)

        nome = intent.getStringExtra("remedioId")

        val editnomeAlarme = findViewById<TextView>(R.id.layout_nome_alarme)
        nome?.let {
            editnomeAlarme.setText(it)
        }

        val container_ProgramacaoRemedio = findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            irParaConfigFrequencia()
        }

        val container_Estoque = findViewById<View>(R.id.container_estoque).setOnClickListener {
            irParaConfigEstoque()
        }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish() // Fecha a atividade atual e retorna para a anterior
        }

    }

    private fun irParaConfigFrequencia() {
        val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java)
        startActivity(telaConfigFrequencia)

    }

    private fun irParaConfigEstoque() {
        val telaConfigEstoque = Intent(this, ConfigEstoque::class.java)
        startActivity(telaConfigEstoque)

    }
}
