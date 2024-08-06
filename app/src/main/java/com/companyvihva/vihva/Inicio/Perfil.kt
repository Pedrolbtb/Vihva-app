
package com.companyvihva.vihva.Inicio

import android.annotation.SuppressLint
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.CriarPerfil.FotoBio
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class Perfil : Fragment() {
    private lateinit var db: FirebaseFirestore
    private var selectedImageUri: Uri? = null
    private var popupImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialização do Firestore dentro do onCreate
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
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
                        view.findViewById<TextView>(R.id.text_nome).text = "${nome ?: "Nome não fornecido"} ${sobrenome ?: "Sobrenome não fornecido"}"
                        view.findViewById<TextView>(R.id.text_genero).text = "${genero ?: "Gênero não fornecido"}"
                        view.findViewById<TextView>(R.id.text_idade).text = "${idade ?: "Idade não fornecida"} anos"
                        view.findViewById<TextView>(R.id.text_altura).text = "${altura ?: "Altura não fornecida"} cm"
                        view.findViewById<TextView>(R.id.text_peso).text = "${peso ?: "Peso não fornecido"} kg"
                        view.findViewById<TextView>(R.id.View_biografia).text = "${biografia ?: "Biografia não fornecida"} "
                        // Carregar a imagem usando Picasso
                        imageUrl?.let {
                            view.findViewById<ImageView>(R.id.img_save_perfil).let { imageView ->
                                Picasso.get().load(it).into(imageView)
                            }
                        }
                    } else {
                        Log.d("PerfilFragment", "Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PerfilFragment", "Error getting document", exception)
                }
        }
        val btn_editar = view.findViewById<Button>(R.id.btn_editar)
        btn_editar.setOnClickListener {
        val  intent = Intent (requireContext(), Editar_perfil :: class.java)
            startActivity(intent)

        }

        // Encontrar o botão btn_medicos
        val btnMedicos = view.findViewById<ImageButton>(R.id.btn_medicos)
        btnMedicos.setOnClickListener {
            val intent = Intent(requireContext(), Lista_amizades::class.java)
            startActivity(intent)
        }

        val btnCodigo = view.findViewById<AppCompatButton>(R.id.btn_cod_usuario)
        btnCodigo.setOnClickListener{
            currentUserUid.let { uid ->
                val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UID",uid)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Código do usuário copiado para área de transferencia", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
