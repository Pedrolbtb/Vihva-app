package com.companyvihva.vihva.Inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.databinding.FragmentCalendarioBinding
import java.util.Locale

class Calendario : Fragment() {

    // Variável para o binding do layout do fragmento
    private lateinit var binding: FragmentCalendarioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout para este fragmento usando ViewBinding
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)
        // Retornar a raiz do layout inflado
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Definindo a localidade para português
        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        // Atualizando a configuração do contexto para usar a nova localidade
        val configuration = resources.configuration
        configuration.setLocale(locale)
        context?.createConfigurationContext(configuration)

        // Obtendo a referência ao CalendarView do layout
        val calendarView = binding.calendario

        // Configurando um listener para responder a mudanças de data
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Formatar a data selecionada em uma string
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Iniciar a atividade para adicionar um evento
            val intent = Intent(activity, Evento::class.java).apply {
                putExtra("selectedDate", selectedDate)
            }
            startActivityForResult(intent, REQUEST_CODE_EVENTO)
        }
    }

    // Receber o resultado da atividade Evento
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EVENTO && resultCode == AppCompatActivity.RESULT_OK) {
            val event = data?.getStringExtra("event")
            // Aqui você pode tratar o evento salvo, como exibir uma mensagem ou salvar em um banco de dados
            event?.let {
                println("Evento salvo: $it")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_EVENTO = 1
    }
}