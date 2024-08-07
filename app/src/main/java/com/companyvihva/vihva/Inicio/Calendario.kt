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
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_lembrete
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

import java.text.SimpleDateFormat
import java.util.Date

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

        adapterLembrete = Adapter_lembrete(emptyList())
        binding.ListaCalendario.layoutManager = LinearLayoutManager(requireContext())
        binding.ListaCalendario.adapter = adapterLembrete
        binding.ListaCalendario.setHasFixedSize(true)

        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        requireContext().createConfigurationContext(configuration)

        fetchEventos()

        val calendarView = binding.calendario
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            val intent = Intent(activity, Evento::class.java).apply {
                putExtra("selectedDate", selectedDate)
            }
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
                    val titulo = document.getString("titulo") ?: ""
                    val data = document.getDate("data") ?: Date()

                    val dataStr = dateFormat.format(data)
                    val evento = tipo_lembrete(titulo, dataStr)
                    eventos.add(evento)
                }
                adapterLembrete.updateEventos(eventos)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EVENTO && resultCode == AppCompatActivity.RESULT_OK) {
            fetchEventos() // Atualizar eventos após salvar um novo evento
        }
    }

    companion object {
        private const val REQUEST_CODE_EVENTO = 1
    }
}
