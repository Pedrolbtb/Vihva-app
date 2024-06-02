package com.companyvihva.vihva.Inicio

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.MyApplication
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.OnRemedioSelectedListener
import com.companyvihva.vihva.model.Tipo_Classe
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
class Inicio1 : Fragment(), OnRemedioSelectedListener {

    //// Firebase ////
    private lateinit var db: FirebaseFirestore

    private lateinit var remedios: MutableList<Tipo_Classe>
    private lateinit var adapter: AdapterRemedio
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)

        // Inicializa o Firebase
        db = FirebaseFirestore.getInstance()

        // Inicializa o RecyclerView e o Adapter

        remedios = mutableListOf()
        adapter = AdapterRemedio(requireContext(), remedios) { remedios ->
            // Implementar a ação ao clicar no remédio
        }


        // Configura o listener de seleção de remédio no MyApplication
        (requireActivity().application as MyApplication).setOnRemedioSelectedListener(this)

        // Encontra o RecyclerView na view inflada
        recyclerView = view.findViewById(R.id.recyclerview_nova_lista)
        if (recyclerView == null) {
            Log.e("Inicio1", "RecyclerView não encontrado!")
        } else {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

        }

        // Referência ao documento "doenca" na coleção, precisa ajustar conforme sua estrutura
        val doencaRef = db.collection("doenca").document("diabetes")
        doencaRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Documentos encontrados no BD
                    val nome = document.getString("nome") ?: ""
                    val imageUrl = document.getString("Url") ?: ""
                    // Atualizando os campos da UI
                    val nomeTextView: TextView = view.findViewById(R.id.nome_widget)
                    val imageView1: ImageView = view.findViewById(R.id.image_widget)
                    nomeTextView.text = nome
                    // Carregando a imagem em uma imageView utilizando Picasso
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView1)
                    } else {
                        Toast.makeText(requireContext(), "URL da imagem não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Documento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Encontra o botão de imagem
        val cardDiabete = view.findViewById<View>(R.id.card_diabete)
        cardDiabete.setOnClickListener {
            // Chama o método para mostrar o popup quando o botão é clicado
            mostrarPopup()
        }

        return view
    }

    // Método para mostrar os dados no popup
    private fun mostrarPopup() {
        // Referência ao documento "diabetes" na coleção "doenca"
        val docRef = db.collection("doenca").document("diabetes")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtém dados do documento Firestore
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val imageUrl1 = document.getString("Url")
                    val imageUrl2 = document.getString("Url2")
                    // Infla o layout do popup
                    val inflater = LayoutInflater.from(requireContext())
                    val popupView = inflater.inflate(R.layout.popup_descricao, null)

                    // Encontra elementos no layout
                    val nomeTextView: TextView = popupView.findViewById(R.id.diabetes)
                    val descricaoTextView: TextView = popupView.findViewById(R.id.descricao)
                    val imageView1: ImageView = popupView.findViewById(R.id.foto_diabete1)
                    val imageView2: ImageView = popupView.findViewById(R.id.foto_diabete2)

                    // Define dados nos TextViews
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao

                    // Carrega imagens com o Picasso
                    if (!imageUrl1.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl1).into(imageView1)
                    }
                    if (!imageUrl2.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl2).into(imageView2)
                    }

                    // Mostra o popup
                    val popupWindow = AlertDialog.Builder(requireContext())
                        .setView(popupView)
                        .create()
                    popupWindow.show()

                    val btnClose: AppCompatImageButton = popupView.findViewById(R.id.close_button)
                    btnClose.setOnClickListener {
                        // Fecha o alertDialog
                        popupWindow.dismiss()
                    }
                } else {
                    // Trata documento não encontrado
                    Log.d("Inicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { exception ->
                // Trata falhas
                Log.e("Inicio1", "Erro ao obter documento", exception)
            }
    }

    override fun onRemedioSelected(remedio: Tipo_Classe) {
        remedios.add(remedio)
        adapter.notifyDataSetChanged()
    }
}
