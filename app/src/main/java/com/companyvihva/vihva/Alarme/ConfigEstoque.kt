package com.companyvihva.vihva.Alarme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R
import com.google.android.material.switchmaterial.SwitchMaterial

class ConfigEstoque : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_estoque)

        val frequencia = intent?.getStringExtra("frequencia")
        val horas = intent?.getIntExtra("horaemhora", 0)
        val duracao = intent?.getStringExtra("duracao")
        val data = intent?.getStringExtra("data")
        val estoque = intent?.getStringExtra("estoque")
        val lembreme = intent?.getStringExtra("lembreme")
        val tipomed = intent?.getStringExtra("tipomed")

        val switchEstoque = findViewById<SwitchMaterial>(R.id.switchEstoque)
        val containerEstoque = findViewById<View>(R.id.container_estoque)
        val textviewQtdEstoque = findViewById<View>(R.id.textview_qtdestoque)
        val editTextEstoqueAtual = findViewById<EditText>(R.id.editTextEstoqueAtual)
        val containerEstoqueAtual = findViewById<View>(R.id.container_estoqueAtual)
        val textviewQtdEstoqueAlarme = findViewById<View>(R.id.textview_qtdestoqueAlarme)
        val editTextLembreme = findViewById<EditText>(R.id.editTextLembreme)

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, CriaAlarme::class.java).apply {
                putExtra("data", data)
                putExtra("duracao", duracao)
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horas)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("estoque", estoque)
            }
            startActivity(intent)
            finish() // Finaliza a atividade atual para retornar à tela anterior
        }


        val spinnerTipoMed = findViewById<Spinner>(R.id.spinnerTipoMed)

        // Deixar spinner bonito
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.TipoMed,
            R.layout.spinner_estoque
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoMed.adapter = adapter

        // Selecionar o item correto no spinner, se tipomed não for nulo
        tipomed?.let {
            val position = adapter.getPosition(it)
            spinnerTipoMed.setSelection(position)
        }

        // Configurar o comportamento do switch
        switchEstoque.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            containerEstoque.visibility = visibility
            textviewQtdEstoque.visibility = visibility
            editTextEstoqueAtual.visibility = visibility
            containerEstoqueAtual.visibility = visibility
            textviewQtdEstoqueAlarme.visibility = visibility
            editTextLembreme.visibility = visibility
        }

        // Definir todos como visíveis se o switch estiver marcado
        if (switchEstoque.isChecked) {
            containerEstoque.visibility = View.VISIBLE
            textviewQtdEstoque.visibility = View.VISIBLE
            editTextEstoqueAtual.visibility = View.VISIBLE
            containerEstoqueAtual.visibility = View.VISIBLE
            textviewQtdEstoqueAlarme.visibility = View.VISIBLE
            editTextLembreme.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.btn_salvarEstoque).setOnClickListener {
            val intent = Intent(this, CriaAlarme::class.java)
            intent.putExtra("frequencia", frequencia)
            intent.putExtra("horaemhora", horas)
            intent.putExtra("duracao", duracao)
            intent.putExtra("data", data)

            val tipoMed = spinnerTipoMed.selectedItem.toString()
            intent.putExtra("tipomed", tipoMed)
            if (switchEstoque.isChecked) {
                val estoque = editTextEstoqueAtual.text.toString()
                val lembreme = editTextLembreme.text.toString()
                intent.putExtra("estoque", estoque)
                intent.putExtra("lembreme", lembreme)

            }
            startActivity(intent)
            finish()

        }
    }
}