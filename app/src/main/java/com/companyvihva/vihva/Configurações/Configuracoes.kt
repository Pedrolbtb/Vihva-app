package com.companyvihva.vihva.Configurações;

import Deletar_perfil
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityConfiguracoesBinding

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener {
            // Ao clicar no botão de deletar, leva o usuário para a tela de deletar perfil
            val intent = Intent(this, Deletar_perfil::class.java)
            startActivity(intent)
        }

        // Vincula elementos do layout com variáveis Kotlin
        spinnerDDI = findViewById(R.id.spinnerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMessage = findViewById(R.id.editTextMsg)

        // Obter um conjunto de preferências do aplicativo
        val preferences = getSharedPreferences(
            "socorro",
            MODE_PRIVATE
        )

        // Carregar preferências
        loadPreferences(preferences)

        // Botão para salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            preferences.edit()
                .putInt("ddi", spinnerDDI.selectedItemPosition)
                .putLong("phone", editTextPhone.text.toString().toLong())
                .putString("default_msg", editTextMessage.text.toString())
                .apply()

            // Exibir uma mensagem de sucesso
            Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT)
                .show()
        }

        // Ouvinte do botão para restaurar preferências
        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            // Aqui você pode implementar a lógica para restaurar as preferências
        }
    }

    // Método para carregar preferências e atualizar a UI
    private fun loadPreferences(preferences: SharedPreferences) {
        spinnerDDI.setSelection(preferences.getInt("ddi", 2))
        editTextPhone.setText(preferences.getLong("phone", 0).toString())
        editTextMessage.setText(
            preferences.getString(
                "default_msg",
                getString(R.string.default_msg)
            )
        )
    }
}
