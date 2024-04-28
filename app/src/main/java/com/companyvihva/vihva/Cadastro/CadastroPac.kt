package com.companyvihva.vihva.Cadastro

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityCadastroPacBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroPac : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPacBinding
    private val auth = FirebaseAuth.getInstance()

    private lateinit var originalEmailDrawable: Drawable
    private lateinit var originalSenhaDrawable: Drawable
    private lateinit var originalConfirmSenhaDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacBinding.inflate(layoutInflater)
        setContentView(binding.root)

        originalEmailDrawable = binding.editEmail.background
        originalSenhaDrawable = binding.editSenha.background
        originalConfirmSenhaDrawable = binding.editConfirmsenha.background

        binding.textTelaCadastro.setOnClickListener {
            irParaTelaLogin()
        }

        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.editEmail.background = originalEmailDrawable
            }
        })

        binding.editSenha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.editSenha.background = originalSenhaDrawable
            }
        })

        binding.editConfirmsenha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.editConfirmsenha.background = originalConfirmSenhaDrawable
            }
        })

        binding.btnCadastro.setOnClickListener {

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmSenha = binding.editConfirmsenha.text.toString()

            if (email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                showToast("Preencha todos os campos")
                if (email.isEmpty()) {
                    binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                }
                if (senha.isEmpty()) {
                    binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                }
                if (confirmSenha.isEmpty()) {
                    binding.editConfirmsenha.background = resources.getDrawable(R.drawable.edit_text_error)
                }
            } else if (senha == confirmSenha) {
                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
                    if (!cadastro.isSuccessful) {
                        val exception = cadastro.exception
                        if (exception != null) {
                            when (exception) {
                                is FirebaseAuthWeakPasswordException -> {
                                    showToast("Digite uma senha com no mínimo 6 caracteres!")
                                    binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                                    binding.editConfirmsenha.background = resources.getDrawable(R.drawable.edit_text_error)
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    showToast("Digite um email válido")
                                    binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                                }
                                is FirebaseAuthUserCollisionException -> {
                                    showToast("Conta já cadastrada")
                                    binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                                }
                                is FirebaseNetworkException -> {
                                    showToast("Sem conexão com a internet")
                                }
                                else -> {
                                    showToast("Erro ao cadastrar usuário")
                                }
                            }
                        }
                    } else {
                        enviarEmailVerificacao()
                        irParaTelaLogin()
                    }
                }
            } else {
                showToast("As senhas não coincidem")
                binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                binding.editConfirmsenha.background = resources.getDrawable(R.drawable.edit_text_error)
            }
        }
    }

    private fun showToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast))
        val text = layout.findViewById<TextView>(R.id.toast)
        text.text = message
        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    private fun irParaTelaLogin() {
        val telaLoginIntent = Intent(this, Login::class.java)
        startActivity(telaLoginIntent)
        finish()
    }

    private fun enviarEmailVerificacao() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Um e-mail de verificação foi enviado para ${user.email}. Por favor, verifique seu e-mail.")
            } else {
                showToast("Falha ao enviar e-mail. Tente novamente mais tarde")
            }
        }
    }
}