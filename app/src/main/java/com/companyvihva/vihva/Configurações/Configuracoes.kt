package com.companyvihva.vihva.Configuracoes

import android.app.ActivityOptions
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    companion object {
        const val PREF_NAME = "socorro"
        const val KEY_DDI = "ddi"
        const val KEY_PHONE = "phone"
        const val KEY_DEFAULT_MSG = "default_msg"
        val PROIBIDAS = listOf(
            "porra", "caralho", "pinto", "buceta", "boceta", "puta", "caceta",
            "escroto", "cu", "acefalo", "foder", "fudeu", "desgraçado", "vadia", "vadio",
            "arrombado", "piroca", "rola", "cuzão", "cuzona", "vai tomar no rabo"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        // Vincula elementos do layout com variáveis Kotlin
        spinnerDDI = findViewById(R.id.spinnerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMessage = findViewById(R.id.editTextMsg)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarConfigsos)
        btnVoltar.setOnClickListener {
            // Adiciona animação ao voltar para a tela anterior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(
                    this, R.anim.fade_in, R.anim.fade_out
                )
                finishAfterTransition()
            } else {
                finish()
            }
        }

        // Configuração do Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ddi,
            R.layout.spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDDI.adapter = adapter

        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Carregar preferências
        loadPreferences(preferences)

        // Botão para salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            val message = editTextMessage.text.toString()

            if (containsPalavrasProibidas(message)) {
                Toast.makeText(
                    this,
                    "Esta mensagem fere os termos do aplicativo e foi bloqueada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                try {
                    preferences.edit()
                        .putInt(KEY_DDI, spinnerDDI.selectedItemPosition)
                        .putLong(KEY_PHONE, editTextPhone.text.toString().toLong())
                        .putString(KEY_DEFAULT_MSG, message)
                        .apply()

                    // Exibir uma mensagem de sucesso
                    Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT)
                        .show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Número de telefone inválido", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Ouvinte do botão para restaurar preferências
        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_warning))
                .setMessage(getString(R.string.text_restore_mensage))
                .setPositiveButton("Sim") { _, _ ->
                    preferences.edit()
                        .remove(KEY_DDI)
                        .remove(KEY_PHONE)
                        .remove(KEY_DEFAULT_MSG)
                        .apply()

                    // Carrega as novas preferências
                    loadPreferences(preferences)
                }
                .setNegativeButton("Não", null)
                .create()
                .show()
        }
    }

    // Método para carregar preferências e atualizar a UI
    private fun loadPreferences(preferences: SharedPreferences) {
        spinnerDDI.setSelection(preferences.getInt(KEY_DDI, 2))
        editTextPhone.setText(preferences.getLong(KEY_PHONE, 0).toString())
        editTextMessage.setText(
            preferences.getString(
                KEY_DEFAULT_MSG,
                getString(R.string.default_msg)
            )
        )
    }

    // Método para verificar se a mensagem contém palavras proibidas
    private fun containsPalavrasProibidas(message: String): Boolean {
        return PROIBIDAS.any { message.contains(it, ignoreCase = true) }
    }

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
