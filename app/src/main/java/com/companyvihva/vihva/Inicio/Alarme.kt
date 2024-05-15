package com.companyvihva.vihva.Inicio

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.CriarPerfil.CriaPerfil
import com.companyvihva.vihva.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore

class Alarme : Fragment() {

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_alarme, container, false)
        val parentLayout = rootView.findViewById<LinearLayout>(R.id.layout_alarme)

        preferences = requireActivity().getSharedPreferences("vihva", Context.MODE_PRIVATE)

        val add_foto = rootView.findViewById<ImageButton>(R.id.add_foto)
        add_foto.setOnClickListener {

            val telaA = Intent(requireActivity(), CriaAlarme::class.java)
            startActivity(telaA)

             var hour: Int = -1
             var minute: Int = -1

                saveData(hour, minute)

                val layoutToAdd = LayoutInflater.from(requireContext())
                    .inflate(R.layout.widget_alarme, null)

                val textViewHour = layoutToAdd.findViewById<TextView>(R.id.hora)
                val timeText = "$hour:$minute"
                textViewHour.text = timeText

                // Definir margem inferior para o novo widget de alarme
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16) // Defina a margem inferior aqui (em pixels)

                // Adicionar margem ao layout
                layoutToAdd.layoutParams = params

                parentLayout.addView(layoutToAdd)
            }




        loadPreference()

        return rootView
    }

    private fun saveData(hour: Int, minute: Int) {
        val dadosCliente = HashMap<String, Any>()
        dadosCliente["hora"] = hour
        dadosCliente["minuto"] = minute

        val db = FirebaseFirestore.getInstance()
        db.collection("alarme").add(dadosCliente)
            .addOnSuccessListener { documentReference ->
                // Toast.makeText(requireContext(), "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Toast.makeText(requireContext(), "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPreference() {
        val hour = preferences.getInt("ddi", 0)
        val minutes = preferences.getLong("phone", 0)
        val mensagem = preferences.getString("text_msg_padrao", "")
        // Use esses valores conforme necessário
    }
}
