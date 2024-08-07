package com.companyvihva.vihva.Inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.companyvihva.vihva.databinding.FragmentCalendarioBinding
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.Adapter_lembrete
import com.companyvihva.vihva.com.companyvihva.vihva.model.Tipo_lembrete

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class Calendario : Fragment() {

    private lateinit var binding: FragmentCalendarioBinding
    private lateinit var adapterLembrete: Adapter_lembrete
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar o adapter com uma lista vazia
        adapterLembrete = Adapter_lembrete(emptyList())

        // Encontrar a RecyclerView e configurá-la
        binding.ListaCalendario.layoutManager = LinearLayoutManager(requireContext())
        binding.ListaCalendario.adapter = adapterLembrete
        binding.ListaCalendario.setHasFixedSize(true)

        // Definindo a localidade para português
        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        requireContext().createConfigurationContext(configuration)

        // Chamar o método para buscar e atualizar eventos
        fetchEventos()

        // Obtendo a referência ao CalendarView do layout
        val calendarView = binding.calendario

        // Configurando um listener para responder a mudanças de data
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            val intent = Intent(activity, Evento::class.java).apply {
                putExtra("selectedDate", selectedDate)
            }
            startActivityForResult(intent, REQUEST_CODE_EVENTO)
        }
    }

    private fun fetchEventos() {
        db.collection("clientes") // Coleção de usuários
            .get()
            .addOnSuccessListener { result ->
                val eventos = mutableListOf<Tipo_lembrete>()

                // Para cada usuário, busque eventos
                val tasks = result.documents.map { document ->
                    val userUid = document.id
                    val docRef = db.collection("clientes").document(userUid).collection("events")

                    docRef.get()
                        .addOnSuccessListener { eventResult ->
                            if (!eventResult.isEmpty) {
                                for (eventDoc in eventResult.documents) {
                                    val titulo = eventDoc.getString("title") ?: ""
                                    val data = eventDoc.getString("data") ?: ""

                                    if (titulo != null) {
                                        val evento = Tipo_lembrete(titulo, data)
                                        eventos.add(evento)
                                    }
                                }
                            }

                            // Atualiza o adapter quando todos os eventos tiverem sido processados
                            if (eventos.size == result.size()) {
                                adapterLembrete.updateEventos(eventos)
                            }
                        }
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                }

                // Espera todas as tarefas serem concluídas
                Tasks.whenAll(tasks).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        it.exception?.printStackTrace()
                    }
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EVENTO && resultCode == AppCompatActivity.RESULT_OK) {
            val event = data?.getStringExtra("event")
            event?.let {
                println("Evento salvo: $it")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_EVENTO = 1
    }
}
