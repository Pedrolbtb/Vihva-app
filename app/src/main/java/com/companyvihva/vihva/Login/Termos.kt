package com.companyvihva.vihva.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.CriarPerfil.CriaPerfil
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Termos : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termos)

        val btnAceitar = findViewById<Button>(R.id.btn_confirmo)
        btnAceitar.setOnClickListener {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                db.collection("clientes").document(uid)
                    .update("termosAceitos", true)
                    .addOnSuccessListener {
                        verificarInformacoesPerfil()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao aceitar os termos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        val btnDetalhes = findViewById<Button>(R.id.btn_dados)
        btnDetalhes.setOnClickListener {
            val intent = Intent(this, Termos_detalhes::class.java)
            startActivity(intent)
        }
    }

    private fun verificarInformacoesPerfil() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("clientes").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nome = document.getString("nome")
                        val sobrenome = document.getString("sobrenome")
                        val biografia = document.getString("biografia")
                        val genero = document.getString("genero")
                        val peso = document.getLong("peso")
                        val altura = document.getLong("altura")
                        val idade = document.getLong("idade")
                        if (nome != null && sobrenome != null && biografia != null && genero != null && peso != null && altura != null && idade != null) {
                            irParaTelaPrincipal()
                        } else {
                            irParaTelaCriacaoPerfil()
                        }
                    } else {
                        irParaTelaCriacaoPerfil()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao verificar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irParaTelaPrincipal() {
        val telaPrincipal = Intent(this, Inicio::class.java)
        startActivity(telaPrincipal)
        finish()
    }

    private fun irParaTelaCriacaoPerfil() {
        val telaCriacaoPerfil = Intent(this, CriaPerfil::class.java)
        startActivity(telaCriacaoPerfil)
        finish()
    }

    private fun irParaTelaDetalhesTermo () {
        val telaTermos_detalhes = Intent(this,Termos_detalhes::class.java)
        startActivity(telaTermos_detalhes)
        finish()
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
