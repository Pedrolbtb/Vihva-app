package com.example.vihva.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.Cadastro.CadastroPac
import com.example.vihva.CriarPerfil.CriaPerfil
import com.example.vihva.R
import com.example.vihva.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
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
        // Inflar o layout do Toast personalizado
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast))

        // Configurar o texto do Toast
        val text = layout.findViewById<TextView>(R.id.toast)
        text.text = message

        // Configurar e exibir o Toast
        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
    /*override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null){
            irParaTelaPrincipal()
        }*/
}
