package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get


class CriaPerfil2 : AppCompatActivity() {

    private lateinit var edit_peso: EditText
    private lateinit var np_peso: NumberPicker
    private lateinit var numberPickerView: View
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil2)

        val edit_altura: EditText = findViewById(R.id.edit_altura)
        edit_peso = findViewById(R.id.edit_peso)
        np_peso = findViewById(R.id.np_peso)

        // Inflar number_picker_layout.xml apenas uma vez
        numberPickerView = layoutInflater.inflate(R.layout.number_picker_layout, null)

        alertDialog = AlertDialog.Builder(this)
            .setView(numberPickerView)
            .setTitle("Selecione o peso")
            .setPositiveButton("OK") { dialog, which ->
                val np_peso = numberPickerView.findViewById<NumberPicker>(R.id.np_peso)
                edit_peso.setText("${np_peso.value}kg")
            }
            .setNegativeButton("Cancelar", null)
            .create()

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

    findViewById<Button>(R.id.btn_proximo).setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        alertDialog.dismiss()
    }
}