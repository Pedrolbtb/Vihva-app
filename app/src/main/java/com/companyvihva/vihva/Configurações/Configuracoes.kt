package com.companyvihva.vihva.Configurações

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityConfiguracoesBinding

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var  editTextPhone: EditText
    private lateinit var  editTextMsg: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        // Inflar o layout usando o View Binding
        //binding = ActivityConfiguracoesBinding.inflate(layoutInflater)
        //setContentView(binding.root)


        //obter um conjunto de preferências do app
        val preference = getSharedPreferences("vihva", Context.MODE_PRIVATE)


        spinnerDDI = findViewById(R.id.spinerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMsg = findViewById(R.id.editTextMsg)


        //Salva as preferencias
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            preference.edit()
            .putInt("ddi", spinnerDDI.selectedItemPosition)
            .putLong("phone", editTextPhone.text.toString().toLong())
            .putString("text_msg_padrao", editTextMsg.text.toString())
            .apply()

            //exibe o toast de confirmação
            Toast.makeText(this, getString(R.string.Toast_configuracoes_sucesso), Toast.LENGTH_SHORT).show()


        }// fim do save

       findViewById<Button>(R.id.btn_restaurar).setOnClickListener {

       }


        }




    }//fim do onCreate
