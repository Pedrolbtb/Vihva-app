package com.companyvihva.vihva.Inicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.companyvihva.vihva.R
import com.google.firebase.firestore.FirebaseFirestore

// Declaração da classe Perfil, que é um Fragment
class Perfil : Fragment() {

    // Método onCreate() chamado quando a atividade está sendo criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Método onCreateView() usado para inflar a IU do fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

 val db = FirebaseFirestore.getInstance()


}


