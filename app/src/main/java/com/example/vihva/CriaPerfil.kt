package com.example.vihva

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import java.util.Calendar

class CriaPerfil : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var dia = 0
    var mes = 0
    var ano = 0

    var savedDia = 0
    var savedMes = 0
    var savedAno = 0

    lateinit var datePickerDialog: DatePickerDialog // Declaração do DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil)

        val edit_aniversario = findViewById<EditText>(R.id.edit_aniversario)

        // Configurar o DatePickerDialog
        val cal = Calendar.getInstance()
        dia = cal.get(Calendar.DAY_OF_MONTH)
        mes = cal.get(Calendar.MONTH)
        ano = cal.get(Calendar.YEAR)
        datePickerDialog = DatePickerDialog(this, this, ano, mes, dia)

        // Configurar o OnClickListener para o edit_aniversario EditText
        edit_aniversario.setOnClickListener {
            pickDate()
        }

        findViewById<Button>(R.id.btn_proximo).setOnClickListener {
            irParaTelaCriaPerfil2()
        }

        // Remova esta linha para evitar que o teclado seja aberto automaticamente
        edit_aniversario.showSoftInputOnFocus = false

    }

    // Função para exibir o DatePickerDialog
    private fun pickDate() {
        datePickerDialog.show()
    }

    // Função para passar para a próxima tela
    private fun irParaTelaCriaPerfil2() {
        val telaL = Intent(this, CriaPerfil2::class.java)
        startActivity(telaL)
    }

    // Função chamada quando a data é definida no DatePickerDialog
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDia = dayOfMonth
        savedMes = month
        savedAno = year

        // Atualizar o texto do EditText com a data selecionada
        val edit_aniversario = findViewById<EditText>(R.id.edit_aniversario)
        edit_aniversario.setText("$dayOfMonth/${month + 1}/$year")
    }
}
