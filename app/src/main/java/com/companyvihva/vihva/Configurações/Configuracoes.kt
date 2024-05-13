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
import com.companyvihva.vihva.Configurações.Configuracoes





class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val btn_delete = findViewById<Button>(R.id.btn_delete)

        btn_delete.setOnClickListener {
            val intent = Intent(this, Configuracoes::class.java)
            startActivity(intent)

            /* SÓ TESTE
        val nome = intent.getStringExtra("nome")
        val nome = intent.getIntExtra("nome", 0)

        Toast.makeTest(this, name + "Tel: " + phone, Toast.LENGHT_SHORT).show()
         */


            //vincula XML (tela)  e Kotlin (classe)
            spinnerDDI = findViewById(R.id.spinnerDDI)
            editTextPhone = findViewById(R.id.editTextPhone)
            editTextMessage = findViewById(R.id.editTextMsg)

            //obter um conjunto de preferências do App
            val preferences = getSharedPreferences(
                "socorro",
                Context.MODE_PRIVATE
            ); /* esse mode private é pra não pegar tipo o modo escuro do seu celular, é p vc escolher só pra esse app*/

            //ler as preferências(se existir)
            loadPreferences(preferences)
            //ddi é int pq ele tá num vetor, então ta salvando a POSIÇÃO (0, 1, 2...)


            //botão salvar das preferencias
            findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
                //val editPreferences = preferences.edit()
                //preferences.edit().putInt("ddi", spinnerDDI.selectedItemPosition)
                //preferences.edit().putLong("phone", editTextPhone.text.toString().toLong())
                //preferences.edit().putString("default_msg", editTextMessage.text.toString())
                //preferences.edit().apply()

                preferences.edit()
                    .putInt("ddi", spinnerDDI.selectedItemPosition)
                    .putLong("phone", editTextPhone.text.toString().toLong())
                    .putString("default_msg", editTextMessage.text.toString())
                    .apply()

                //TOAST é a mensagenzinha que aparece embaixo falando que deu certo
                Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT)
                    .show()

            }//fim do save

            //ouvinte do click do botão restaurar
            findViewById<Button>(R.id.btn_restaurar).setOnClickListener {


            }//fim do restaurar

        } //fim do onCreate



    }//fim da classe

    fun loadPreferences(preferences: SharedPreferences) {
        //ja faz isso la embaixo
        //val ddi = preferences.getInt("ddi",2) //se tiver ddi(preferencia), pega ela, se n tiver, pega o padrão (posição 2: Brasil)
        //val phone = preferences.getLong("phone",0) //ex.: 19.999.739.741; int não salva toda essa qtde, só 32bits
        //val defaultMsg = preferences.getString("default_msg",getString (R.string.default_msg)) //R é uma classe q guarda tudo que eu crio de tela

        //exibir as preferencias
        spinnerDDI.setSelection(preferences.getInt("ddi", 2))
        editTextPhone.setText(preferences.getLong("phone", 0).toString())
        editTextMessage.setText(
            preferences.getString(
                "default_msg",
                getString(R.string.default_msg)
            )
        )
    }//fim do loadpreferences

}