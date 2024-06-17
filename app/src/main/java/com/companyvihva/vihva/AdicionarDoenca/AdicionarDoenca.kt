package com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Tipo_Classe
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.firebase.firestore.FirebaseFirestore

class AdicionarDoenca : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_doenca)

        // Inicializando o Firestore
        firestore = FirebaseFirestore.getInstance()

        //configurando recyclerwview
        val recyclerview_adicionar = findViewById<RecyclerView>(R.id.recyclerview_adicionar)
        recyclerview_adicionar.layoutManager = LinearLayoutManager(this)
         recyclerview_adicionar.setHasFixedSize(true)
        val listaAdicionarRemedio: MutableList<Tipo_Remedios> = mutableListOf()

        //inicalizando o adapter
        val adapterAdicionar = AdapterListanova(this,listaAdicionarRemedio)
        recyclerview_adicionar.adapter = adapterAdicionar

       //metodo para buscar as doenças

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltarAddDoenca)
        btnVoltar.setOnClickListener {
            finish()
        }
    }
}