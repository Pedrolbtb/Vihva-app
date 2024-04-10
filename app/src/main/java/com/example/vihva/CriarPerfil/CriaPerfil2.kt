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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.vihva.Inicio.Inicio
import com.example.vihva.R


class CriaPerfil2 : AppCompatActivity() {

    //definição das variaveis
    private lateinit var edit_peso: EditText
    private lateinit var np_peso: NumberPicker
    private lateinit var numberPickerView: View
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil2)

        //ligando variaveis com a id
        val edit_altura: EditText = findViewById(R.id.edit_altura)
        edit_peso = findViewById(R.id.edit_peso)
        np_peso = findViewById(R.id.np_peso)


        // Inflar number_picker_layout.xml apenas uma vez
        numberPickerView = layoutInflater.inflate(R.layout.number_picker_layout, null)

        //criação do alert dialog que é o fundo da escolha de peso
        alertDialog = AlertDialog.Builder(this)
            .setView(numberPickerView)
            .setTitle("Selecione o peso em kg")
            .setPositiveButton("OK") { dialog, which ->
                val np_peso = numberPickerView.findViewById<NumberPicker>(R.id.np_peso)
                edit_peso.setText("${np_peso.value}kg")
            }
            .setNegativeButton("Cancelar", null)
            .create()

        //clique do campo de peso que abre o alert dialog
        edit_peso.setOnClickListener {
            val np_peso = numberPickerView.findViewById<NumberPicker>(R.id.np_peso)
            np_peso.minValue = 30
            np_peso.maxValue = 400
            np_peso.value = 30
            alertDialog.show()
        }

        // Desativa a exibição do teclado ao clicar na EditText
        edit_peso.showSoftInputOnFocus = false

        np_peso.setOnValueChangedListener { picker, oldVal, newVal ->
            // Atualiza o texto da EditText com o novo valor selecionado no NumberPicker
            edit_peso.setText(newVal.toString())
        }

        //codigo para mask que coloca o cm dps do resultado
        edit_altura.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val alturaText = s.toString()
                val pesoText = s.toString()
                if (alturaText.isNotEmpty() && !alturaText.endsWith("cm")) {
                    edit_altura.removeTextChangedListener(this)
                    edit_altura.setText("${alturaText}cm")
                    edit_altura.setSelection(edit_altura.text.length - 2)
                    edit_altura.addTextChangedListener(this)
                }
            }
        })

    //verificação dos campos se estão vazios ou com valor certo
    findViewById<Button>(R.id.btn_proximo).setOnClickListener {

        //tirando o cm e o kg da variavel pra evitar problemas
        val altura = edit_altura.text.toString().removeSuffix("cm").toIntOrNull()
        val peso = edit_peso.text.toString().removeSuffix("kg").toIntOrNull()

        if (altura != null && altura >= 100 && altura <= 300 && peso != null){

            irParaTelaInicial()

        }else{
            Toast.makeText(this, "Digite um valor de altura entre 100cm e 300cm ou insira um peso", Toast.LENGTH_SHORT).show()
        }
    }

    }

    private fun irParaTelaInicial() {
        val telaL = Intent(this, Inicio::class.java)
        startActivity(telaL)
        finish()
    }


    //função que destroi o alertdialog evitando que fique aberto e cause problemas no funcionamento do app
    override fun onDestroy() {
        super.onDestroy()
        alertDialog.dismiss()
    }
}