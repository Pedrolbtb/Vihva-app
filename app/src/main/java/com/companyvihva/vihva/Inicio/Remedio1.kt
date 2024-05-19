package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.Remedio2

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Remedio1.newInstance] factory method to
 * create an instance of this fragment.
 */
class Remedio1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.remedio2, container, false)

        // Configura o RecyclerView
        val recyclerViewRemedios = view.findViewById<RecyclerView>(R.id.recyclerView_remedios)
        recyclerViewRemedios.layoutManager = LinearLayoutManager(context)
        recyclerViewRemedios.setHasFixedSize(true)

        // Lista de remédios
        val listaRemedios: MutableList<Remedio2> = mutableListOf()
        val adapterRemedio = AdapterRemedio(requireContext(), listaRemedios)
        recyclerViewRemedios.adapter = adapterRemedio

        // Adicione os itens à lista
        val remedio1 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio1)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio2 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio2)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio3 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio3)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio4 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio4)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio5 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio5)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio6 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio6)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio7 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio7)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio8 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio8)
        // Adicione mais itens conforme necessário

        // Adicione os itens à lista
        val remedio9 = Remedio2(
            R.drawable.adicionarfoto,
            "Dipirona",
            "Descrição da Dipirona."
        )
        listaRemedios.add(remedio9)
        // Adicione mais itens conforme necessário


        return view
    }
}
