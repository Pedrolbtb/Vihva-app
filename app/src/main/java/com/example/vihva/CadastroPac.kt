package com.example.vihva

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.vihva.databinding.ActivityCadastroPacBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.FirebaseAuthLegacyRegistrar

class CadastroPac : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPacBinding

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_pac)

        binding = ActivityCadastroPacBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textTelaCadastro.setOnClickListener {
            irParaTelaLoginP()
        }

        binding.btnCadastro.setOnClickListener { view ->
            val edit_email = binding.editEmail.text.toString()
            val edit_senha = binding.editSenha.text.toString()
            val edit_confirmsenha = binding.editConfirmsenha.text.toString()

            if (edit_email.isEmpty() || edit_senha.isEmpty() || edit_confirmsenha.isEmpty()){
                showToast("Preencha todos os campos")
            } else if (edit_senha == edit_confirmsenha) {
                auth.createUserWithEmailAndPassword(edit_email, edit_senha).addOnCompleteListener { cadastro ->
                    if (!cadastro.isSuccessful) {
                        // Verifica o tipo de exceção e define a mensagem de erro correspondente
                        val mensagemErro = when(cadastro.exception){
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!"
                            is FirebaseAuthInvalidCredentialsException -> "Digite um email válido"
                            is FirebaseAuthUserCollisionException -> "Conta já cadastrada"
                            is FirebaseNetworkException -> "Sem conexão com a internet"
                            else -> "Erro ao cadastrar usuário"
                        }
                        showToast(mensagemErro)
                    } else {
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                        irParaTelaLoginP()
                    }
                }
            } else {
                // Tratar o caso em que as senhas não coincidem
                showToast("As senhas não coincidem")
            }
        }
    }

    //função para exibir Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //função para ir para tela de login
    private fun irParaTelaLoginP() {
        val telaL = Intent(this, MainActivity::class.java)
        startActivity(telaL)
        finish()
    }

    //função para ir para tela de criação de perfil
    private fun irParaTelaCriaPerfil() {
        val telaL = Intent(this, CriaPerfil::class.java)
        startActivity(telaL)
    }
}
