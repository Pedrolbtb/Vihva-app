package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.R

// Define a classe Evento como uma AppCompatActivity
class Evento : AppCompatActivity() {

    // Interface para comunicar o evento salvo à atividade pai ou fragmento
    interface OnEventSaveListener {
        fun onEventSave(event: String)
    }

    // Variável para armazenar a instância do listener
    private var listener: OnEventSaveListener? = null

    // Método para definir o listener
    fun setOnEventSaveListener(listener: OnEventSaveListener) {
        this.listener = listener
    }

    // Método chamado ao criar a atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o layout da atividade
        setContentView(R.layout.activity_evento)

        // Obtém referências para os elementos de UI
        val saveButton = findViewById<Button>(R.id.saveButton)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val eventEditText = findViewById<EditText>(R.id.eventEditText)
        val backButton = findViewById<View>(R.id.btnVoltar) // Obtém referência ao botão de voltar

        // Define um listener para o botão de salvar
        saveButton.setOnClickListener {
            // Obtém o texto do EditText
            val event = eventEditText.text.toString()
            // Chama o método onEventSave do listener com o texto do evento, se o listener não for nulo
            listener?.onEventSave(event)
            // Fecha a atividade
            finish()
        }

        // Define um listener para o botão de voltar
        backButton.setOnClickListener {
            // Fecha a atividade
            finish()
        }
    }
}

