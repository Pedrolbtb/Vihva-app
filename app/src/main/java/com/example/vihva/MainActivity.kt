package com.example.vihva

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                showToast("Preencha todos os campos!")
            } else {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { autenticacao ->
                        if (autenticacao.isSuccessful) {
                            irParaTelaPrincipal() // Mude para a tela principal após o login
                        } else {
                            showToast("Erro ao realizar o login!")
                        }
                    }
            }
        }

        binding.textTelaCadastro.setOnClickListener {
            irParaTelaCadastro()
        }
    }

    private fun irParaTelaCadastro() {
        val telaCadastro = Intent(this, CadastroPac::class.java)
        startActivity(telaCadastro)
    }

    private fun irParaTelaPrincipal() {
        val telaPrincipal = Intent(this, CriaPerfil::class.java) // Altere para a tela principal desejada
        startActivity(telaPrincipal)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    /*override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null){
            irParaTelaPrincipal()
        }*/
}
