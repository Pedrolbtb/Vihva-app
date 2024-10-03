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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConfigEstoque : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var documentId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_estoque)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Recuperando dados da intent
        val frequencia = intent.getStringExtra("frequencia")
        val horaemhora = intent.getStringExtra("horaemhora")
        val duracao = intent.getStringExtra("duracao")
        val data = intent.getStringExtra("data")
        val estoque = intent.getStringExtra("estoque")
        val lembreme = intent.getStringExtra("lembreme")
        val horaDiariamente = intent.getStringExtra("horaDiariamente")
        val tipomed = intent.getStringExtra("tipomed")
        val nome = intent.getStringExtra("remedioId")
        val switchEstoqueChecked = intent.getBooleanExtra("switchEstoque", false)
        documentId = intent.getStringExtra("documentId") ?: ""

        // Configuração do Spinner TipoMed
        val spinnerTipoMed = findViewById<Spinner>(R.id.spinnerTipoMed)
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

        // Configurando o SwitchMaterial
        val switchEstoque = findViewById<SwitchMaterial>(R.id.switchEstoque)
        switchEstoque.isChecked = switchEstoqueChecked
        val containerEstoque = findViewById<View>(R.id.container_estoque)
        val textviewQtdEstoque = findViewById<View>(R.id.textview_qtdestoque)
        val editTextEstoqueAtual = findViewById<EditText>(R.id.editTextEstoqueAtual)
        val containerEstoqueAtual = findViewById<View>(R.id.container_estoqueAtual)
        val textviewQtdEstoqueAlarme = findViewById<View>(R.id.textview_qtdestoqueAlarme)
        val editTextLembreme = findViewById<EditText>(R.id.editTextLembreme)

        // Configurando o estado inicial do Switch e a visibilidade dos elementos relacionados ao estoque
        val visibility = if (switchEstoqueChecked) View.VISIBLE else View.GONE
        containerEstoque.visibility = visibility
        textviewQtdEstoque.visibility = visibility
        editTextEstoqueAtual.visibility = visibility
        containerEstoqueAtual.visibility = visibility
        textviewQtdEstoqueAlarme.visibility = visibility
        editTextLembreme.visibility = visibility

        // Configurando o listener para o SwitchMaterial
        switchEstoque.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            containerEstoque.visibility = visibility
            textviewQtdEstoque.visibility = visibility
            editTextEstoqueAtual.visibility = visibility
            containerEstoqueAtual.visibility = visibility
            textviewQtdEstoqueAlarme.visibility = visibility
            editTextLembreme.visibility = visibility

            if (isChecked) {
                editTextEstoqueAtual.setText(estoque)
                editTextLembreme.setText(lembreme)
            }
        }

        // Configurando o listener para o botão de voltar
        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, ConfigDuracao::class.java).apply {
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horaemhora)
                putExtra("duracao", duracao)
                putExtra("data", data)
                putExtra("lembreme", lembreme)
                putExtra("tipomed", tipomed)
                putExtra("estoque", estoque)
                putExtra("remedioId", nome)
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("switchEstoque", switchEstoque.isChecked)
                putExtra("documentId", documentId) // Adicionando o documentId aqui
            }
            startActivity(intent)
        }

        // Configurando o listener para o botão de salvar
        findViewById<Button>(R.id.btn_salvarEstoque).setOnClickListener {
            val tipoMed = spinnerTipoMed.selectedItem.toString()
            val estoqueAtual = editTextEstoqueAtual.text.toString()
            val lembremeAtual = editTextLembreme.text.toString()

            estoque_db(estoqueAtual, lembremeAtual, tipoMed)

            val intent = Intent(this, CriaAlarme::class.java).apply {
                putExtra("frequencia", frequencia)
                putExtra("horaemhora", horaemhora)
                putExtra("duracao", duracao)
                putExtra("data", data)
                putExtra("remedioId", nome)
                putExtra("horaDiariamente", horaDiariamente)
                putExtra("switchEstoque", switchEstoque.isChecked)
                putExtra("tipomed", tipoMed)
                putExtra("estoque", estoqueAtual)
                putExtra("lembreme", lembremeAtual)
                putExtra("documentId", documentId) // Adicionando o documentId aqui
            }
            startActivity(intent)
            finish() // Finaliza a activity para voltar à anterior
        }
    }

    private fun estoque_db(estoqueAtual: String, lembremeAtual: String, tipomed: String?) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userDocRef = db.collection("clientes").document(userId)
            val alarmeDocRef = userDocRef.collection("Alarmes").document(documentId)

            val estoqueData = mapOf(
                "estoqueAtual" to estoqueAtual,
                "lembremeAtual" to lembremeAtual,
                "tipoMed" to tipomed
            )

            alarmeDocRef.update(estoqueData)
                .addOnSuccessListener {
                    println("Informações do estoque atualizadas com sucesso.")
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}
