package com.example.vihva.Login

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

        // Configurando clique no botão de login
        binding.btnEntar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                showToast("Preencha todos os campos!")
                binding.editEmail.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_text_error))
                binding.editSenha.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_text_error))
            } else {
                // Autenticar usuário com e-mail e senha
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { autenticacao ->
                        if (autenticacao.isSuccessful) {
                            irParaTelaPrincipal() // Ir para a tela principal após o login
                        } else {
                            showToast("Erro ao realizar o login!")
                        }
                    }
            }
        }

        // Configurando clique no texto "Cadastre-se"
        binding.textTelaCadastro.setOnClickListener {
            irParaTelaCadastro()
        }

        // Configurando clique no texto "Esqueci minha senha"
        binding.esqueciSenha.setOnClickListener {
            mostrarDialogRedefinirSenha()
        }
    }

    // Função para abrir a tela de cadastro
    private fun irParaTelaCadastro() {
        val telaCadastro = Intent(this, CadastroPac::class.java)
        startActivity(telaCadastro)
    }

    // Função para abrir a tela principal
    private fun irParaTelaPrincipal() {
        val telaPrincipal = Intent(this, CriaPerfil::class.java) // Altere para a tela principal desejada
        startActivity(telaPrincipal)
    }

    // Função para exibir uma mensagem Toast
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

    // Função para mostrar o dialog de redefinição de senha
    private fun mostrarDialogRedefinirSenha() {
        // Construir o dialog
        val emailDialog = AlertDialog.Builder(this)
        val emailInput = EditText(this)
        emailDialog.setTitle("Esqueci minha senha")
        emailDialog.setMessage("Insira seu e-mail para redefinir a senha:")
        emailDialog.setView(emailInput)

        // Configurar botões do dialog
        emailDialog.setPositiveButton("Enviar") { dialog, _ ->
            val email = emailInput.text.toString()
            if (email.isNotEmpty()) {
                enviarEmailRedefinicaoSenha(email)
            } else {
                showToast("Por favor, insira seu e-mail.")
            }
            dialog.dismiss()
        }
        emailDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostrar o dialog
        emailDialog.show()
    }

    // Função para enviar e-mail de redefinição de senha
    private fun enviarEmailRedefinicaoSenha(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("E-mail de redefinição de senha enviado para $email.")
                } else {
                    showToast("Falha ao enviar e-mail de redefinição de senha. Verifique se o endereço de e-mail está correto.")
                }
            }
    }
}
