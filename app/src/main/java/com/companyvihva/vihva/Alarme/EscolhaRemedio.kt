package com.companyvihva.vihva.Alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterRemedio_Alarme
import com.companyvihva.vihva.com.companyvihva.vihva.model.Remedios_alarme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EscolhaRemedio : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterRemedioAlarme: AdapterRemedio_Alarme
    private lateinit var listaRemedios: MutableList<Remedios_alarme>
    private lateinit var recyclerViewRemedios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha_doenca)

        recyclerViewRemedios = findViewById(R.id.recyclerview_adicionarRemedioAlarme)
        setupRecyclerView()

        db = FirebaseFirestore.getInstance()
        fetchRemediosDoUsuario()

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarAddDoenca)
        btnVoltar.setOnClickListener {
            finish()
        }

    }

    private fun setupRecyclerView() {
        recyclerViewRemedios.layoutManager = LinearLayoutManager(this)
        recyclerViewRemedios.setHasFixedSize(true)

        listaRemedios = mutableListOf()
        adapterRemedioAlarme = AdapterRemedio_Alarme(this, listaRemedios)
        recyclerViewRemedios.adapter = adapterRemedioAlarme
    }

    private fun fetchRemediosDoUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val remediosIds = document.get("remedios") as? List<String>
                        remediosIds?.forEach { remedioId ->
                            fetchDadosDoFirebase(remedioId)
                        }
                    } else {
                        Log.d("EscolhaRemedio", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EscolhaRemedio", "Erro ao obter documento do cliente", e)
                }
        }
    }

    private fun fetchDadosDoFirebase(remedioId: String) {
        if (remedioId.isNotEmpty()) {
            val docRef = db.collection("remedios").document(remedioId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val url = document.getString("Url")
                        val nome = document.getString("nome")
                        val tipo = document.getString("tipo")


                        if (nome != null && tipo != null) {
                            val remedio = Remedios_alarme(url ?:"",nome,tipo, remedioId)

                            atualizarListaRemedios(remedio)
                        } else {
                            Log.d("EscolhaRemedio", "Nome, tipo ou url do remédio está nulo")
                        }
                    } else {
                        Log.d("EscolhaRemedio", "Documento do remédio não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EscolhaRemedio", "Erro ao obter documento do remédio", e)
                }
        } else {
            Log.d("EscolhaRemedio", "ID do remédio está vazio")
        }
    }

    private fun atualizarListaRemedios(remedio: Remedios_alarme) {
        listaRemedios.add(remedio)
        adapterRemedioAlarme.notifyDataSetChanged()
    }
}
