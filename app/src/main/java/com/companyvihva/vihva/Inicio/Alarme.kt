package com.companyvihva.vihva.Inicio

import android.content.Context
import android.os.Bundle
import android.content.SharedPreferences
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.companyvihva.vihva.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore


class Alarme : Fragment() {

    val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout primeiro
        val rootView = inflater.inflate(R.layout.fragment_alarme, container, false)

        // Encontrar o botão de imagem após inflar o layout
        val add_foto = rootView.findViewById<ImageButton>(R.id.add_foto)

        // Configurar o listener de clique para o botão de imagem
        add_foto.setOnClickListener {
            //picker de hora
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Selecionar horário")
                    .build()

            picker.addOnPositiveButtonClickListener {
                val hour = picker.hour // Obter a hora selecionada
                val minute = picker.minute // Obter o minuto selecionado

                val preferencehour = requireActivity().getSharedPreferences("vihva", Context.MODE_PRIVATE)
                val editor = preferencehour.edit()
                editor.putInt("hour", hour)
                editor.putInt("minute", minute)
                editor.apply()
            }

            picker.show(childFragmentManager, "timePicker")
        }

        // Verificar o formato de hora do sistema usando o contexto da atividade associada ao fragmento
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        // Retornar a raiz do layout inflado
        return rootView
    }

    /*private fun saveData(hour: Int, minute: Int){
        val dadosCliente = HashMap<String, Any>()
        dadosCliente["hora"] = hour
        dadosCliente["minuto"] = minute

        db.collection("alarme").add(dadosCliente)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }*/
}


