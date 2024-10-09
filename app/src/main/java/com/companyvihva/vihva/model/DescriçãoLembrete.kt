package com.companyvihva.vihva.model

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterListaAmigos
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DescriçãoLembrete : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListaAmigos: AdapterListaAmigos
    private val listaMedicos: MutableList<Amigos> = mutableListOf()
    private lateinit var recyclerViewMedicos: RecyclerView
    private lateinit var tituloTextView: TextView
    private lateinit var descricaoTextView: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_lembrete)

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance()

        // Configura o RecyclerView
        recyclerViewMedicos = findViewById(R.id.lista_medicos)
        recyclerViewMedicos.layoutManager = LinearLayoutManager(this)
        recyclerViewMedicos.setHasFixedSize(true)
        adapterListaAmigos = AdapterListaAmigos(this, listaMedicos)
        recyclerViewMedicos.adapter = adapterListaAmigos

        tituloTextView = findViewById(R.id.titulo)
        descricaoTextView = findViewById(R.id.descricao_lembrete)

        // Botão de voltar
        findViewById<ImageButton>(R.id.btn_voltarDO).setOnClickListener {
            finish()
        }

        // Recupera o ID do lembrete passado via Intent
        val eventoId = intent.getStringExtra("eventoId")

        eventoId?.let { buscarLembrete(it) } ?: run {
            tituloTextView.text = "Erro: ID do lembrete não encontrado."
            descricaoTextView.text = ""
        }

        // Botão de excluir
        findViewById<ImageButton>(R.id.lixeira_doencas).setOnClickListener {
            eventoId?.let { id -> showConfirmDeleteDialog(id) }
        }
    }

    private fun buscarLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = db.collection("clientes")
            .document(userId)
            .collection("eventos")
            .document(eventoId)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val titulo = document.getString("titulo") ?: "Título não disponível"
                val descricao = document.getString("descricao") ?: "Descrição não disponível"
                val medicoUid = document.getString("medicoUid") // Supondo que seja uma string única

                tituloTextView.text = titulo
                descricaoTextView.text = descricao

                medicoUid?.let {
                    buscarMedico(it)
                }
            } else {
                tituloTextView.text = "Documento não encontrado."
                descricaoTextView.text = ""
            }
        }.addOnFailureListener { exception ->
            tituloTextView.text = "Erro ao carregar o lembrete."
            descricaoTextView.text = ""
            Log.w("DescricaoLembrete", "Erro ao acessar o Firestore", exception)
        }
    }

    private fun buscarMedico(medicoUid: String) {
        listaMedicos.clear() // Limpa a lista antes de adicionar novos médicos
        adapterListaAmigos.notifyDataSetChanged()

        if (medicoUid.isEmpty()) {
            // Caso não haja médico para exibir
            return
        }

        db.collection("medicos").document(medicoUid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val url = document.getString("imageUrl") ?: ""
                    val nome = document.getString("nome") ?: "Nome não disponível"
                    val crm = document.getString("crm") ?: "CRM não disponível"
                    val medico = Amigos(url, nome, medicoUid, crm)
                    listaMedicos.add(medico)
                    Log.d("DescricaoLembrete", "Médico adicionado: $medico")
                    adapterListaAmigos.notifyDataSetChanged()
                } else {
                    Log.d("DescricaoLembrete", "Documento do médico não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("DescricaoLembrete", "Erro ao buscar médico", e)
            }
    }

    private fun showConfirmDeleteDialog(eventoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este lembrete? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ -> deleteLembrete(eventoId) }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    private fun deleteLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = db.collection("clientes")
            .document(userId)
            .collection("eventos")
            .document(eventoId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Lembrete excluído com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescricaoLembrete", "Lembrete excluído com sucesso")
                finish() // Fecha a atividade após a exclusão
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir lembrete", Toast.LENGTH_SHORT).show()
                Log.w("DescricaoLembrete", "Erro ao excluir lembrete", e)
            }
    }

    //animaçõa da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
