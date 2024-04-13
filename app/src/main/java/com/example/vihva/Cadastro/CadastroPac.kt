package com.example.vihva.Cadastro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.CriarPerfil.CriaPerfil
import com.example.vihva.Login.Login
import com.example.vihva.R
import com.example.vihva.databinding.ActivityCadastroPacBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import org.w3c.dom.Text

class CadastroPac : AppCompatActivity() {

    // Referência para o binding do layout
    private lateinit var binding: ActivityCadastroPacBinding

    // Instância do Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar o layout usando ViewBinding
        binding = ActivityCadastroPacBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar o listener para o texto "Já tem uma conta? Faça login"
        binding.textTelaCadastro.setOnClickListener {
            irParaTelaLoginP()
        }

        // Configurar o listener para o botão de cadastro
        binding.btnCadastro.setOnClickListener { view ->
            val edit_email = binding.editEmail.text.toString()
            val edit_senha = binding.editSenha.text.toString()
            val edit_confirmsenha = binding.editConfirmsenha.text.toString()

            // Verificar se todos os campos estão preenchidos
            if (edit_email.isEmpty() || edit_senha.isEmpty() || edit_confirmsenha.isEmpty()){
                showToast("Preencha todos os campos")
            } else if (edit_senha == edit_confirmsenha) {
                // Tentar criar um novo usuário no Firebase Auth
                auth.createUserWithEmailAndPassword(edit_email, edit_senha).addOnCompleteListener { cadastro ->
                    if (!cadastro.isSuccessful) {
                        // Verificar o tipo de exceção e exibir mensagem de erro correspondente
                        val mensagemErro = when(cadastro.exception){
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!"
                            is FirebaseAuthInvalidCredentialsException -> "Digite um email válido"
                            is FirebaseAuthUserCollisionException -> "Conta já cadastrada"
                            is FirebaseNetworkException -> "Sem conexão com a internet"
                            else -> "Erro ao cadastrar usuário"
                        }
                        showToast(mensagemErro)
                    } else {
                        // Limpar os campos e ir para a tela de login
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                        irParaTelaLoginP()
                    }
                }
            } else {
                // Caso as senhas não coincidam
                showToast("As senhas não coincidem")
            }
        }
    }

    // Função para exibir um Toast personalizado
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

    // Função para ir para a tela de login
    private fun irParaTelaLoginP() {
        val telaL = Intent(this, Login::class.java)
        startActivity(telaL)
        finish()
    }

    // Função para ir para a tela de criação de perfil
    private fun irParaTelaCriaPerfil() {
        val telaL = Intent(this, CriaPerfil::class.java)
        startActivity(telaL)
    }
}