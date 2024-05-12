package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Declaração da classe Perfil, que é um Fragment
class Perfil : Fragment() {

    //Declaração da instancia do FireStore como propriedade da classe
    private lateinit var db: FirebaseFirestore

    // Método onCreate() chamado quando a atividade está sendo criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialização do Firestore dentro do onCreate
        db = FirebaseFirestore.getInstance()
    }

    // Método onCreateView() usado para inflar a IU do fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    // Método onViewCreated() chamado após a criação da visualização do fragmento
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        currentUserUid?.let { uid ->
            val userDocRef = db.collection("clientes").document(uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nome = document.getString("nome")
                        val sobrenome = document.getString("sobrenome")
                        val idade = document.getLong("idade")?.toInt()
                        val altura = document.getLong("altura")?.toInt()
                        val peso = document.getLong("peso")?.toInt()
                        val genero = document.getString("genero")

                        view.findViewById<TextView>(R.id.text_nome).text =
                            "Nome: ${nome ?: "Nome não fornecido"} ${sobrenome ?: "Sobrenome não fornecido"}"
                        view.findViewById<TextView>(R.id.text_genero).text = "Gênero: ${genero ?: "Gênero não fornecido"}"
                        view.findViewById<TextView>(R.id.text_idade).text = "Idade: ${idade ?: "Idade não fornecida"}"
                        view.findViewById<TextView>(R.id.text_altura).text = "Altura: ${altura ?: "Altura não fornecida"}"
                        view.findViewById<TextView>(R.id.text_peso).text = "Peso: ${peso ?: "Peso não fornecido"}"
                    } else {
                        Log.d("PerfilFragment", "Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PerfilFragment", "Error getting document", exception)
                }
        }
    }
}