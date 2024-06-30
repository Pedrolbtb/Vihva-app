package com.companyvihva.vihva.Alarme

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_alarme)

        // Inicializando as variáveis de intent dentro do onCreate
        val frequencia = intent?.getStringExtra("frequencia")
        val horas = intent?.getIntExtra("horaemhora", 0)
        val duracao = intent?.getStringExtra("duracao")
        val data = intent?.getStringExtra("data")
        val estoque = intent?.getStringExtra("estoque")
        val lembreme = intent?.getStringExtra("lembreme")
        val tipomed = intent?.getStringExtra("tipomed")

        nome = intent.getStringExtra("remedioId")

        val editnomeAlarme = findViewById<TextView>(R.id.layout_nome_alarme)
        nome?.let {
            editnomeAlarme.text = it
        }

        val descProgramacao = findViewById<TextView>(R.id.descprogramacao)
        if (frequencia != null && horas != null && duracao != null && data != null) {
            descProgramacao.text = "$frequencia - $horas - $duracao - $data"
        }

        val descEstoque = findViewById<TextView>(R.id.descEstoque)
        if (estoque != null && tipomed != null) {
            descEstoque.text = "$estoque $tipomed"
        }

        findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            irParaConfigFrequencia()
        }

        findViewById<View>(R.id.container_estoque).setOnClickListener {
            irParaConfigEstoque(data, duracao, frequencia, horas, lembreme, tipomed, estoque)
        }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun irParaConfigFrequencia() {
        val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java)
        startActivity(telaConfigFrequencia)
    }

    private fun irParaConfigEstoque(data: String?, duracao: String?, frequencia: String?, horas: Int?, lembreme: String?, tipomed: String?, estoque: String?) {
        val telaConfigEstoque = Intent(this, ConfigEstoque::class.java).apply {
            putExtra("data", data)
            putExtra("duracao", duracao)
            putExtra("frequencia", frequencia)
            putExtra("horaemhora", horas)
            putExtra("lembreme", lembreme)
            putExtra("tipomed", tipomed)
            putExtra("estoque", estoque)
        }
        startActivity(telaConfigEstoque)
    }
}
