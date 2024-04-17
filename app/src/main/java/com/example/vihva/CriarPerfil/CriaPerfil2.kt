package com.example.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.vihva.Inicio.Inicio
import com.example.vihva.R

class CriaPerfil2 : AppCompatActivity() {

    // Declarando as variáveis para os elementos da UI
    private lateinit var edit_peso: EditText
    private lateinit var np_peso: NumberPicker
    private lateinit var numberPickerView: View
    private lateinit var alertDialog: AlertDialog
    var UltimoValor = 30 // Valor inicial do peso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil2)

        // Inicializando as variáveis com referências aos elementos da UI
        val edit_altura: EditText = findViewById(R.id.edit_altura)
        edit_peso = findViewById(R.id.edit_peso)
        np_peso = findViewById(R.id.np_peso)

        // Inflando a view do NumberPicker
        numberPickerView = layoutInflater.inflate(R.layout.number_picker_layout, null)

        // Criando o AlertDialog para o NumberPicker
        alertDialog = AlertDialog.Builder(this)
            .setView(numberPickerView)
            .setTitle("Selecione o peso em kg")
            .setPositiveButton("OK") { dialog, which ->
                val np_peso = numberPickerView.findViewById<NumberPicker>(R.id.np_peso)
                UltimoValor = np_peso.value
                edit_peso.setText("${np_peso.value}kg")
            }
            .setNegativeButton("Cancelar", null)
            .create()

        // Configurando o clique no EditText de peso para exibir o NumberPicker
        edit_peso.setOnClickListener {
            val np_peso = numberPickerView.findViewById<NumberPicker>(R.id.np_peso)
            np_peso.minValue = 30
            np_peso.maxValue = 400
            np_peso.value = UltimoValor
            alertDialog.show()
        }

        // Desabilitando o teclado virtual ao clicar no EditText de peso
        edit_peso.showSoftInputOnFocus = false

        // Configurando o listener para o NumberPicker
        np_peso.setOnValueChangedListener { picker, oldVal, newVal ->
            UltimoValor = newVal
            edit_peso.setText(newVal.toString())
        }

        // Configurando o TextWatcher para o EditText de altura
        edit_altura.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val alturaText = s.toString()
                if (alturaText.isNotEmpty() && !alturaText.endsWith("cm")) {
                    edit_altura.removeTextChangedListener(this)
                    edit_altura.setText("${alturaText}cm")
                    edit_altura.setSelection(edit_altura.text.length - 2)
                    edit_altura.addTextChangedListener(this)
                }
            }
        })

        // Configurando o clique no botão de "Próximo"
        findViewById<Button>(R.id.btn_proximo).setOnClickListener {
            // Obtendo os valores de altura, peso e gênero
            val altura = edit_altura.text.toString().removeSuffix("cm").toIntOrNull()
            val peso = edit_peso.text.toString().removeSuffix("kg").toIntOrNull()
            val genero = when (findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId){
                R.id.radio_fem -> "Feminino"
                R.id.radio_masc -> "Masculino"
                R.id.radio_gen -> "Sem genero"
                else -> null
            }

            // Validando os dados inseridos pelo usuário
            if (altura != null && altura >= 100 && altura <= 300 && peso != null){
                // Criando a Intent e iniciando a próxima atividade
                val intent = Intent(this, FotoBio::class.java)
                intent.putExtra("altura",altura)
                intent.putExtra("peso",peso)
                intent.putExtra("genero",genero)
                startActivity(intent)
                finish() // Finalizando a atividade atual
            }else{
                // Exibindo um Toast caso os dados não sejam válidos
                showToast("Digite um valor de altura entre 100cm e 300cm ou insira um peso")
            }
        }
    }

    // Função para navegar para a tela inicial
    private fun irParaTelaInicial() {
        val telaL = Intent(this, FotoBio::class.java)
        startActivity(telaL)
        finish()
    }

    // Liberando recursos quando a atividade é destruída
    override fun onDestroy() {
        super.onDestroy()
        alertDialog.dismiss()
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
