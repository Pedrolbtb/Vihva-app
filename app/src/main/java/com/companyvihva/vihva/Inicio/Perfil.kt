package com.companyvihva.vihva.Inicio


import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configuracoes.ConfigNotificacoes
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Perfil : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance() // Inicializa o Firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserUid?.let { uid ->
            val userDocRef = db.collection("clientes").document(uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nome = document.getString("nome")
                        val sobrenome = document.getString("sobrenome")
                        val idade = document.getLong("idade")?.toString()
                        val altura = document.getLong("altura")?.toString()
                        val peso = document.getLong("peso")?.toString()
                        val biografia = document.getString("biografia")
                        val genero = document.getString("genero")
                        val imageUrl = document.getString("imageUrl")

                        view.findViewById<TextView>(R.id.text_nome).text =
                            "${nome ?: "Nome não fornecido"} ${sobrenome ?: "Sobrenome não fornecido"}"
                        view.findViewById<TextView>(R.id.text_genero).text =
                            "${genero ?: "Gênero não fornecido"}"
                        view.findViewById<TextView>(R.id.text_idade).text =
                            "${idade ?: "Idade não fornecida"} anos"
                        view.findViewById<TextView>(R.id.text_altura).text =
                            "${altura ?: "Altura não fornecida"} cm"
                        view.findViewById<TextView>(R.id.text_peso).text =
                            "${peso ?: "Peso não fornecido"} kg"
                        view.findViewById<TextView>(R.id.View_biografia).text =
                            "${biografia ?: "Biografia não fornecida"}"

                        // Carregar a imagem usando Picasso
                        imageUrl?.let {
                            view.findViewById<ImageView>(R.id.img_save_perfil).let { imageView ->
                                Picasso.get().load(it).into(imageView)
                            }
                        }

                        // Chama a função para buscar os hábitos
                        fetchHabitosDoUsuario(uid)
                    } else {
                        Log.d("PerfilFragment", "Documento não existe")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PerfilFragment", "Erro ao pegar o documento", exception)
                }
        }

        val btn_editar = view.findViewById<Button>(R.id.btn_editarPerfil_dois)
        btn_editar.setOnClickListener {
            Log.d("Botão", "Botão Editar clicado")
            val editarPerfil = Intent(requireContext(), Editar_perfil::class.java)
            startActivity(editarPerfil)
        }

        val btnMedicos = view.findViewById<ImageButton>(R.id.btn_medicos)
        btnMedicos.setOnClickListener {
            val intent = Intent(requireContext(), Lista_amizades::class.java)
            startActivity(intent)
        }

        val btnNotific = view.findViewById<ImageButton>(R.id.btn_notific).setOnClickListener {
            val telaNotificacoes = Intent(requireContext(), ConfigNotificacoes::class.java)
            startActivity(telaNotificacoes)
        }

        val btnCodigo = view.findViewById<AppCompatButton>(R.id.btn_cod_usuario)
        btnCodigo.setOnClickListener {
            currentUserUid?.let { uid ->
                val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UID", uid)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    requireContext(),
                    "Código do usuário copiado para área de transferência",
                    Toast.LENGTH_SHORT
                ).show()
                mostrarpopupcodigo(uid)
            }
        }
    }

    private fun fetchHabitosDoUsuario(uid: String) {
        val docRef = db.collection("clientes").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val habitos = document.get("habitos") as? List<String>
                    val habitosTextView = view?.findViewById<TextView>(R.id.View_biografia_habitos)
                    if (habitos != null && habitosTextView != null) {
                        habitosTextView.text = habitos.joinToString(separator = ", ")
                    } else {
                        Log.d("Perfil", "Hábitos não encontrados ou TextView nula")
                    }
                } else {
                    Log.d("Perfil", "Documento do cliente não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Perfil", "Erro ao obter documento do cliente", e)
            }
    }


    private fun mostrarpopupcodigo(uid: String) {
        val inflater = LayoutInflater.from(requireContext())
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        val popupView = inflater.inflate(R.layout.popup_codigo, null)
        val btnCancelar = popupView.findViewById<ImageButton>(R.id.btnCancelar)
        val textViewCodigo = popupView.findViewById<TextView>(R.id.textView_codigo)

        textViewCodigo.text = uid

        builder.setView(popupView)
        val alertDialog = builder.create()
        alertDialog.window?.setDimAmount(0.10f)
        alertDialog.show()

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }
    }

}
