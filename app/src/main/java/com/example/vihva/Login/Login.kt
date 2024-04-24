package com.example.vihva.Login

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.vihva.Cadastro.CadastroPac
import com.example.vihva.CriarPerfil.CriaPerfil
import com.example.vihva.R
import com.example.vihva.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    // Variáveis para armazenar os drawables originais dos campos de e-mail e senha
    private lateinit var originalEmailDrawable: Drawable
    private lateinit var originalSenhaDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa os drawables originais dos campos de e-mail e senha
        originalEmailDrawable = binding.editEmail.background
        originalSenhaDrawable = binding.editSenha.background

        // Adiciona o textWatcher para restaurar o drawable original do campo de e-mail
        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário implementar neste caso
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Não é necessário implementar neste caso
            }

            override fun afterTextChanged(s: Editable?) {
                // Restaura o drawable original do campo de e-mail
                binding.editEmail.background = originalEmailDrawable
            }
        })

        // Adiciona o TextWatcher para restaurar o drawable original do campo de senha
        binding.editSenha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário implementar neste caso
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Não é necessário implementar neste caso
            }

            override fun afterTextChanged(s: Editable?) {
                // Restaura o drawable original do campo de senha
                binding.editSenha.background = originalSenhaDrawable
            }
        })

        // Configurando clique no botão de login
        binding.btnEntar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                showToast("Preencha todos os campos!")

                // Configura o background dos campos de e-mail e senha para vermelho
                binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
            } else {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { login ->
                        // Configura o background dos campos de e-mail e senha para vermelho em caso de erro
                        if (!login.isSuccessful) {
                            binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                            binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                        } else {
                            // Limpa o texto dos campos se o login for bem-sucedido
                            binding.editEmail.text = null
                            binding.editSenha.text = null
                        }

                        if (login.isSuccessful) {
                            irParaTelaPrincipal()
                        } else {
                            val mensagemErro = when (login.exception) {
                                is FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas. Verifique seu e-mail ou senha."
                                is FirebaseAuthInvalidUserException -> "Usuário não encontrado. Verifique seu e-mail."
                                else -> "Erro ao realizar o login. Tente novamente mais tarde."
                            }
                            showToast(mensagemErro)
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
