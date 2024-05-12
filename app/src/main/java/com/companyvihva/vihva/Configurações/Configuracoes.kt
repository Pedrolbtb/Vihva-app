package com.companyvihva.vihva.Configurações

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.companyvihva.vihva.R
import com.companyvihva.vihva.Configurações.ActivityDeletarPerfil





class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMsg: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        findViewById<Button>(R.id.btn_delete).setOnClickListener {
           // // Redirecionar para a ActivityDeletarPerfil
            startActivity(Intent(this, ActivityDeletarPerfil::class.java))
        //    startActivity(ActivityDeletarPerfil)
       // }

        // Obter um conjunto de preferências do app
        val preference = getSharedPreferences("vihva", Context.MODE_PRIVATE)
       // loadPreference(preference)

        spinnerDDI = findViewById(R.id.spinerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMsg = findViewById(R.id.editTextMsg)

        // Salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            preference.edit()
                .putInt("ddi", spinnerDDI.selectedItemPosition)
                .putLong("phone", editTextPhone.text.toString().toLong())
                .putString("text_msg_padrao", editTextMsg.text.toString())
                .apply()

            // Exibir o toast de confirmação
            Toast.makeText(this, getString(R.string.Toast_configuracoes_sucesso), Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            // Implementar a lógica de restaurar as configurações padrão aqui
        }
    }

   // private fun loadPreference(preferences: SharedPreferences) {
        //val ddi = preferences.getInt("ddi", 0)
       // val phone = preferences.getLong("phone", 0)
       // val mensagem = preferences.getString("text_msg_padrao", "")

        // Exibir as preferências
        // spinnerDDI.setSelection(ddi)
        // editTextPhone.setText(phone.toString())
        // editTextMsg.setText(mensagem)
    }
}
