package com.companyvihva.vihva.Inicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.companyvihva.vihva.R
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
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Formatar a data selecionada em uma string
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
        }
    }
}
