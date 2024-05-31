package com.companyvihva.vihva.Configurações

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    // Definindo constantes e lista de palavras proibidas
    companion object {
        const val PREF_NAME = "socorro"
        const val KEY_DDI = "ddi"
        const val KEY_PHONE = "phone"
        const val KEY_DEFAULT_MSG = "default_msg"
        val PROIBIDAS = listOf(
            "porra",
            "Porra",
            "caralho",
            "Caralho",
            "Pinto",
            "pinto",
            "Buceta",
            "buceta",
            "boceta",
            "Boceta",
            "puta",
            "Puta",
            "caceta",
            "Caceta",
            "escroto",
            "Escroto",
            "cu",
            "Cu",
            "acefalo",
            "Acefalo",
            "foder",
            "Foder",
            "fudeu",
            "Fudeu",
            "Fodeu",
            "fodeu",
            "fude",
            "Fude",
            "Desgraçado",
            "desgraçado",
            "Desgraçada",
            "desgraçada",
            "Vadia",
            "vadia",
            "Vadio",
            "vadio",
            "Arrombado",
            "arrombado",
            "Piroca",
            "piroca",
            "Rola",
            "rola",
            "cuzão",
            "Cuzão",
            "cuzona",
            "Cuzona",
            "vai tomar no rabo",
            "Vai tomar no rabo"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)


        // Vincula elementos do layout com variáveis Kotlin
        spinnerDDI = findViewById(R.id.spinnerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMessage = findViewById(R.id.editTextMsg)

        //deixar spinner bonito
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ddi,
            R.layout.spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDDI.adapter = adapter


        // Obter um conjunto de preferências do aplicativo
        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Carregar preferências
        loadPreferences(preferences)

        // Botão para salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            val message = editTextMessage.text.toString()

            if (containsPalavrasProibidas(message)) {
                Toast.makeText(
                    this,
                    "esta mensagem fere os termos do aplicativo e foi bloqueada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                preferences.edit()
                    .putInt(KEY_DDI, spinnerDDI.selectedItemPosition)
                    .putLong(KEY_PHONE, editTextPhone.text.toString().toLong())
                    .putString(KEY_DEFAULT_MSG, message)
                    .apply()

                // Exibir uma mensagem de sucesso
                Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT)
                    .show()
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

                    // Carrega as novas preferências (refresh do Fernandinho)
                    loadPreferences(preferences)
                }
                .setNegativeButton("Não", null)
                .create()
                .show()
        } // Fim do restaurar
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
}
