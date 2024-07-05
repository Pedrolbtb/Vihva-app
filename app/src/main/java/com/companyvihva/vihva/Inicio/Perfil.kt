
package com.companyvihva.vihva.Inicio

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
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
            // Abrir o popup para editar o perfil
            showEditPerfilPopup()
        }

        // Encontrar o botão btn_medicos
        val btnMedicos = view.findViewById<AppCompatButton>(R.id.btn_medicos)
        btnMedicos.setOnClickListener {
            val intent = Intent(requireContext(), Lista_amizades::class.java)
            startActivity(intent)
        }

        val btnCodigo = view.findViewById<AppCompatButton>(R.id.btn_cod_usuario)
        btnCodigo.setOnClickListener{

        }
    }

    private fun showEditPerfilPopup() {
        // Inflar o layout do popup
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_editar_bio, null)
        // Criar o AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(popupView)
        val alertDialog = alertDialogBuilder.create()

        // Encontrar os elementos dentro do popup
        val editNomeV2 = popupView.findViewById<EditText>(R.id.edit_nome_V2)
        val editSobreNomeV2 = popupView.findViewById<EditText>(R.id.edit_sobrenome_V2)
        val editIdadeV2 = popupView.findViewById<EditText>(R.id.edit_idade_V2)
        val editAlturaV2 = popupView.findViewById<EditText>(R.id.edit_altura_V2)
        val editPesoV2 = popupView.findViewById<EditText>(R.id.edit_peso_V2)
        val radioGroupV2 = popupView.findViewById<RadioGroup>(R.id.radioGroup)
        val editbiografiaV2 = popupView.findViewById<EditText>(R.id.edit_biografia_V2)
        popupImageView = popupView.findViewById(R.id.img_perfil_popup)  // Atualiza a referência ao ImageView do popup
        val btnSAlvarV2 = popupView.findViewById<Button>(R.id.btn_salvar_V2)
        val btnClose = popupView.findViewById<AppCompatImageButton>(R.id.btnClose)

        // Configurar o ImageView do popup para abrir a galeria
        popupImageView?.setOnClickListener {
            pickImageGalleryForPopup()
        }

        // Configurar o botão de fechar
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        // Adiciona o OnClickListener ao botão "Salvar"
        btnSAlvarV2.setOnClickListener {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            currentUserId?.let { uid ->
                val userDocRef = db.collection("clientes").document(uid)

                userDocRef.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val currentNome = document.getString("nome")
                        val currentSobrenome = document.getString("sobrenome")
                        val currentIdade = document.getLong("idade")?.toString()
                        val currentAltura = document.getLong("altura")?.toString()
                        val currentPeso = document.getLong("peso")?.toString()
                        val currentBiografia = document.getString("biografia")
                        val currentGenero = document.getString("genero")

                        val novoNome = editNomeV2.text.toString().ifEmpty { currentNome }
                        val novoSobrenome = editSobreNomeV2.text.toString().ifEmpty { currentSobrenome }
                        val novaIdade = editIdadeV2.text.toString().ifEmpty { currentIdade }
                            ?.toIntOrNull()
                        val novaAltura = editAlturaV2.text.toString().ifEmpty { currentAltura }
                            ?.toIntOrNull()
                        val novoPeso = editPesoV2.text.toString().ifEmpty { currentPeso }
                            ?.toIntOrNull()
                        val novoGenero = when (radioGroupV2.checkedRadioButtonId) {
                            R.id.radio_masc_V2 -> "Masculino"
                            R.id.radio_fem_V2 -> "Feminino"
                            R.id.radio_Semgen_V2 -> "Prefiro não dizer"
                            else -> currentGenero
                        }
                        val novaBiografia = editbiografiaV2.text.toString().ifEmpty { currentBiografia }

                        // Atualizar os valores no Firebase
                        val updates = mapOf(
                            "nome" to novoNome,
                            "sobrenome" to novoSobrenome,
                            "idade" to novaIdade,
                            "altura" to novaAltura,
                            "peso" to novoPeso,
                            "genero" to novoGenero,
                            "biografia" to novaBiografia
                        )

                        userDocRef.update(updates)
                            .addOnSuccessListener {
                                Log.d("PerfilFragment", "Informações atualizadas com sucesso")
                                // Verifique se uma nova imagem foi selecionada
                                selectedImageUri?.let {
                                    uploadImageToFirebaseStorage(it, uid)
                                }
                                // Fecha o AlertDialog após salvar
                                alertDialog.dismiss()
                            }
                            .addOnFailureListener {
                                Log.e("PerfilFragment", "Erro ao atualizar informações", it)
                            }
                    }
                }
            }
        }

        alertDialog.show()
    }

    private fun pickImageGalleryForPopup() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, FotoBio.IMAGE_REQUEST_CODE_POPUP)
    }

    // Função chamada quando uma atividade iniciada por este aplicativo retorna um resultado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FotoBio.IMAGE_REQUEST_CODE_POPUP && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            popupImageView?.setImageURI(selectedImageUri)  // Atualiza o ImageView do popup
            popupImageView?.clipToOutline = true
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/profile_image.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    updateUserProfileImageUrl(imageUrl, userId)
                }.addOnFailureListener { e ->
                    Log.e("PerfilFragment", "Erro ao obter URL da imagem", e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilFragment", "Erro ao fazer upload da imagem", e)
            }
    }

    private fun updateUserProfileImageUrl(imageUrl: String, userId: String) {
        val userDocRef = db.collection("clientes").document(userId)
        userDocRef.update("imageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("PerfilFragment", "URL da imagem atualizada com sucesso")
            }
            .addOnFailureListener { e ->
                Log.e("PerfilFragment", "Erro ao atualizar URL da imagem", e)
            }
    }
}
