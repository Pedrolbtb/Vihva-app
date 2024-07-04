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

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
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
        val switchEstoqueChecked = intent?.getBooleanExtra("switchEstoque", false) ?: false
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
        if (estoque != null || tipomed != null) {
            descEstoque.text = "$estoque $tipomed"
        }

        findViewById<View>(R.id.container_programacaoRemedio).setOnClickListener {
            val telaConfigFrequencia = Intent(this, ConfigFrequencia::class.java)

               telaConfigFrequencia.putExtra("data", data)
               telaConfigFrequencia.putExtra("duracao", duracao)
               telaConfigFrequencia.putExtra("frequencia", frequencia)
               telaConfigFrequencia.putExtra("horaemhora", horas)
               telaConfigFrequencia.putExtra("lembreme", lembreme)
               telaConfigFrequencia.putExtra("tipomed", tipomed)
               telaConfigFrequencia.putExtra("estoque", estoque)
               telaConfigFrequencia.putExtra("remedioId", nome)
               telaConfigFrequencia.putExtra("switchEstoque", switchEstoqueChecked)

            startActivity(telaConfigFrequencia)
        }

        findViewById<View>(R.id.container_estoque).setOnClickListener {
            val telaConfigEstoque = Intent(this, ConfigEstoque::class.java)
               telaConfigEstoque.putExtra("data", data)
               telaConfigEstoque.putExtra("duracao", duracao)
               telaConfigEstoque.putExtra("frequencia", frequencia)
               telaConfigEstoque.putExtra("horaemhora", horas)
               telaConfigEstoque.putExtra("lembreme", lembreme)
               telaConfigEstoque.putExtra("tipomed", tipomed)
               telaConfigEstoque.putExtra("estoque", estoque)
               telaConfigEstoque.putExtra("remedioId", nome)
               telaConfigEstoque.putExtra("switchEstoque", switchEstoqueChecked)
            startActivity(telaConfigEstoque)
            }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val telaEscolhaRemedio = Intent(this, EscolhaRemedio::class.java).apply {
            }
            startActivity(telaEscolhaRemedio)
        }
    }
}
