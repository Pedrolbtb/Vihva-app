package com.companyvihva.vihva.Login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.companyvihva.vihva.Cadastro.CadastroPac
import com.companyvihva.vihva.CriarPerfil.CriaPerfil
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

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
                        if (login.isSuccessful) {
// Se o login for bem-sucedido, verifique se o documento do usuário existe no banco de dados
                            verificarDocumentoUsuario()
                        } else {
// Se o login falhar, trate os erros e mostre uma mensagem ao usuário
                            val exception = login.exception
                            if (exception is FirebaseAuthInvalidCredentialsException) {
// Se o erro for de credenciais inválidas, apenas o campo de senha deve ficar vermelho
                                binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                            } else if (exception is FirebaseAuthInvalidUserException) {
// Se o erro for de usuário inválido, apenas o campo de e-mail deve ficar vermelho
                                binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                            } else {
// Se for qualquer outro erro, ambos os campos ficarão vermelhos
                                binding.editEmail.background = resources.getDrawable(R.drawable.edit_text_error)
                                binding.editSenha.background = resources.getDrawable(R.drawable.edit_text_error)
                            }
// Exiba uma mensagem de erro para o usuário
                            val mensagemErro = when (exception) {
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
    // Função para verificar se o documento do usuário existe no banco de dados
    private fun verificarDocumentoUsuario() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("clientes").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
// Se o documento existir, verifique se contém as informações necessárias do perfil
                        val nome = document.getString("nome")
                        val sobrenome = document.getString("sobrenome")
                        val biografia = document.getString("biografia")
                        val genero = document.getString("genero")
                        val peso = document.getLong("peso")
                        val altura = document.getLong("altura")
                        val idade = document.getLong("idade")
                        if (nome != null && sobrenome != null && biografia != null && genero != null && peso != null && altura != null && idade != null) {
// Se o documento contiver todas as informações do perfil, vá para a tela principal
                            irParaTelaPrincipal()
                        } else {
// Caso contrário, redirecione o usuário para a tela de criação de perfil
                            irParaTelaCriacaoPerfil()
                        }
                    } else {
// Se o documento não existir, redirecione o usuário para a tela de criação de perfil
                        irParaTelaCriacaoPerfil()
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Erro ao verificar a existência do documento: ${e.message}")
                }
        } else {
            showToast("Usuário não autenticado")
        }
    }
    // Função para redirecionar o usuário para a tela principal
    private fun irParaTelaPrincipal() {
        val telaPrincipal = Intent(this, Inicio::class.java) // Substitua TelaPrincipal pela sua atividade principal
        startActivity(telaPrincipal)
        finish()
    }
    // Função para redirecionar o usuário para a tela de criação de perfil
    private fun irParaTelaCriacaoPerfil() {
        val telaCriacaoPerfil = Intent(this, CriaPerfil::class.java)
        startActivity(telaCriacaoPerfil)
    }
    // Função para exibir uma mensagem Toast
    private fun showToast(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
    // Função para redirecionar o usuário para a tela de cadastro
    private fun irParaTelaCadastro() {
        val telaCadastro = Intent(this, CadastroPac::class.java)
        startActivity(telaCadastro)
    }
    // Função para exibir o diálogo de redefinição de senha
    @SuppressLint("MissingInflatedId")
    private fun mostrarDialogRedefinirSenha() {
        val inflater = LayoutInflater.from(this)
        val builder = AlertDialog.Builder(this)
        val popupView = inflater.inflate(R.layout.popup_esqueci_senha, null)

        builder.setView(popupView)

        val alertDialog = builder.create()
        alertDialog.show()

        // Encontrar os botões dentro do layout inflado
        val btnCancelar = popupView.findViewById<Button>(R.id.btnCancelar)
        val btnEnviar = popupView.findViewById<Button>(R.id.btnEnviar)
        val input = popupView.findViewById<EditText>(R.id.editTextTextEmailAddress)

        // Configurar listener para o botão Cancelar
        btnCancelar.setOnClickListener {
            alertDialog.dismiss() // Fechar o diálogo ao cancelar
        }

        // Configurar listener para o botão Enviar
        btnEnviar.setOnClickListener {
            val email = input.text.toString()
            if (email.isNotEmpty()) {
                enviarEmailRedefinirSenha(email)
                alertDialog.dismiss() // Fechar o diálogo após enviar o e-mail
            } else {
                showToast("Por favor, insira seu e-mail")
            }
        }
    }

    // Função para enviar o e-mail de redefinição de senha
    private fun enviarEmailRedefinirSenha(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("E-mail de redefinição de senha enviado para $email")
                } else {
                    showToast("Erro ao enviar o e-mail de redefinição de senha: ${task.exception?.message}")
                }
            }
    }
}