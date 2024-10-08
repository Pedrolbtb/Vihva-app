package com.companyvihva.vihva.Inicio


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.companyvihva.vihva.Evento.Evento

import com.companyvihva.vihva.databinding.FragmentCalendarioBinding
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.Adapter_lembrete
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_lembrete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class Calendario : Fragment() {

    private lateinit var binding: FragmentCalendarioBinding
    private lateinit var adapterLembrete: Adapter_lembrete
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento e retorna a root view
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura o adaptador do RecyclerView com uma lista vazia inicialmente
        adapterLembrete = Adapter_lembrete(emptyList(), requireContext())
        binding.ListaCalendario.layoutManager = LinearLayoutManager(requireContext())
        binding.ListaCalendario.adapter = adapterLembrete
        binding.ListaCalendario.setHasFixedSize(true)

        // Define o locale para português do Brasil
        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        // Atualiza a configuração de locale do contexto
        val configuration = resources.configuration
        configuration.setLocale(locale)
        requireContext().createConfigurationContext(configuration)

        // Busca e exibe eventos
        fetchEventos()

        // Configura o listener para mudanças de data no calendário
        val calendarView = binding.calendario
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Formata a data selecionada
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Cria um Intent para iniciar a atividade de evento com a data selecionada
            val intent = Intent(activity, Evento::class.java).apply {
                putExtra("selectedDate", selectedDate)
            }
            // Inicia a atividade para resultado e define um código de solicitação
            startActivityForResult(intent, REQUEST_CODE_EVENTO)
        }
    }

    private fun fetchEventos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("clientes")
            .document(userId)
            .collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                val eventos = mutableListOf<tipo_lembrete>()
                val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                for (document in result) {
                    val id = document.id // Obtenha o ID do documento
                    val titulo = document.getString("titulo") ?: ""
                    val data = document.getDate("data") ?: Date()
                    val dataStr = dateFormat.format(data)
                    val descricao = document.getString("descricao") ?: ""

                    eventos.add(tipo_lembrete(id, titulo, dataStr, data, descricao))
                }

                eventos.sortBy { it.dataDate }
                adapterLembrete.updateEventos(eventos)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica se o resultado é de uma atividade com o código de solicitação esperado e se foi bem-sucedida
        if (requestCode == REQUEST_CODE_EVENTO && resultCode == AppCompatActivity.RESULT_OK) {
            // Atualiza a lista de eventos após salvar um novo evento
            fetchEventos()
        }
    }

    companion object {
        private const val REQUEST_CODE_EVENTO = 1
    }
}
