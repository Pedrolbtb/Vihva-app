package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.companyvihva.vihva.R

// Define uma classe DialogFragment para exibir um diálogo de evento
class Evento : DialogFragment() {

    // Interface para comunicar o evento salvo ao fragmento pai ou atividade
    interface OnEventSaveListener {
        fun onEventSave(event: String)
    }

    // Variável para armazenar a instância do listener
    private var listener: OnEventSaveListener? = null

    // Método para definir o listener
    fun setOnEventSaveListener(listener: OnEventSaveListener) {
        this.listener = listener
    }

    // Método para inflar o layout do fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout activity_evento para este fragmento
        return inflater.inflate(R.layout.activity_evento, container, false)
    }

    // Método chamado após o layout ser criado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtém referências para os elementos de UI
        //val eventEditText = view.findViewById<EditText>(R.id.eventEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)

        // Define um listener para o botão de salvar
        saveButton.setOnClickListener {
            // Obtém o texto do EditText
            //val event = eventEditText.text.toString()
            // Chama o método onEventSave do listener com o texto do evento
            //listener?.onEventSave(event)
            // Fecha o diálogo
            dismiss()
        }
    }
}
