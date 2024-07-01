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

    // Declarando as variáveis necessárias
    private lateinit var binding: ActivityCadastroPacBinding
    private val auth = FirebaseAuth.getInstance() // Instância do Firebase Auth
    private val db = FirebaseFirestore.getInstance() // Instância do Firestore

    // Variáveis para armazenar os backgrounds originais dos campos de texto
    private lateinit var originalEmailDrawable: Drawable
    private lateinit var originalSenhaDrawable: Drawable
    private lateinit var originalConfirmSenhaDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando os backgrounds originais dos campos de texto
        originalEmailDrawable = binding.editEmail.background
        originalSenhaDrawable = binding.editSenha.background
        originalConfirmSenhaDrawable = binding.editConfirmsenha.background

        // Configurando o listener para o texto "Já tenho cadastro"
        binding.textTelaCadastro.setOnClickListener {
            irParaTelaLogin()
        }

        // Configurando listeners para os campos de texto para limpar os erros de validação
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

        // Configurando o listener para o botão de cadastro
        binding.btnCadastro.setOnClickListener {

            // Obtendo os valores dos campos de texto
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmSenha = binding.editConfirmsenha.text.toString()

            // Verificando se todos os campos foram preenchidos
            if (email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                showToast("Preencha todos os campos")
                // Destacando os campos não preenchidos com erro
                if (email.isEmpty()) {
                    binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                }
                if (senha.isEmpty()) {
                    binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                }
                if (confirmSenha.isEmpty()) {
                    binding.editConfirmsenha.background = resources.getDrawable(R.drawable.edit_text_error)
                }
            } else if (senha == confirmSenha) { // Verificando se as senhas coincidem
                // Criando o usuário no Firebase Auth
                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
                    if (!cadastro.isSuccessful) {
                        // Tratamento de exceções durante o cadastro
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
                        // Obtendo o UID do usuário cadastrado
                        val user = auth.currentUser
                        val uid = user?.uid

                        // Verificando se o UID não é nulo
                        if (uid != null) {
                            // Criando um mapa de dados do cliente
                            val dadosCliente = hashMapOf(
                                "email" to email,
                                "uid" to uid
                                // Adicione outros dados do cliente aqui
                            )
                            // Salvando os dados do cliente no Firestore na coleção "clientes"
                            db.collection("clientes").document(uid).set(dadosCliente)
                                .addOnSuccessListener {
                                    // Sucesso ao salvar os dados do cliente
                                    enviarEmailVerificacao()
                                    irParaTelaLogin()
                                }
                                .addOnFailureListener { e ->
                                    // Falha ao salvar os dados do cliente
                                    showToast("Erro ao salvar os dados do cliente: ${e.message}")
                                }
                        }
                    }
                }
            } else {
                // Senhas não coincidem, destacando os campos de senha com erro
                showToast("As senhas não coincidem")
                binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                binding.editConfirmsenha.background = resources.getDrawable(R.drawable.edit_text_error)
            }
        }
    }

    // Função para exibir um Toast
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    // Função para navegar para a tela de login
    private fun irParaTelaLogin() {
        val telaLoginIntent = Intent(this, Login::class.java)
        startActivity(telaLoginIntent)
        finish()
    }

    // Função para enviar o email de verificação para o usuário
    private fun enviarEmailVerificacao() {
        val user=auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Um e-mail de verificação foi enviado para ${user.email}. Por favor, verifique seu e-mail.")
            } else {
                showToast("Falha ao enviar e-mail. Tente novamente mais tarde")
            }
        }
    }
}

