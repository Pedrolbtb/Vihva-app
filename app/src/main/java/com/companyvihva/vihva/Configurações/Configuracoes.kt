package com.companyvihva.vihva.Configurações

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.companyvihva.vihva.R

class Configuracoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        /* TESTE DE INTENTS
        val intent = intent
        val name = intent.getStringExtra("name")
        val phone = intent.getIntExtra("phone", 0)

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        */

        //obter um conjunto de preferências do app
        val preference = getSharedPreferences("Vihva", Context.MODE_PRIVATE)

        //ler as preferencias
        val ddi = preference.getInt("ddi",2)
        val phone = preference.getLong("text_celular", 0)
        val mensagem = preference.getString("text_msg_padrao",R.string.text_msg_padrao.toString() )

        val spinnerDDI: Spinner = findViewById(R.id.spinerDDI)
        val editTextCelular: EditText = findViewById(R.id.editTextPhone)
        val editTextMsg: EditText = findViewById(R.id.editTextMsg)

        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            preference.edit().putInt("ddi", spinnerDDI.selectedItemPosition)
            preference.edit().putLong("phone", editTextCelular.text.toString().toLong())
            preference.edit().putString("text_msg_padrao", editTextMsg.text.toString())
            preference.edit().apply()

        }


    }
}