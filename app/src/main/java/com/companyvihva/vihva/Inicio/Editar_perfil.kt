package com.companyvihva.vihva.Inicio

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.companyvihva.vihva.CriarPerfil.FotoBio
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Editar_perfil : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var popupImageView: ImageView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        // Inicializar os elementos da tela
        val editNomeV2 = findViewById<EditText>(R.id.edit_nome_V2)
        val editSobreNomeV2 = findViewById<EditText>(R.id.edit_sobrenome_V2)
        val editIdadeV2 = findViewById<EditText>(R.id.edit_idade_V2)
        val editAlturaV2 = findViewById<EditText>(R.id.edit_altura_V2)
        val editPesoV2 = findViewById<EditText>(R.id.edit_peso_V2)
        val radioGroupV2 = findViewById<RadioGroup>(R.id.radioGroup)
        val editbiografiaV2 = findViewById<EditText>(R.id.edit_biografia_V2)
        popupImageView = findViewById(R.id.img_perfil_popup)
        val btnSAlvarV2 = findViewById<Button>(R.id.btn_salvar_V2)
        val btnClose = findViewById<ImageButton>(R.id.btnClose)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar o ImageView para abrir a galeria
        popupImageView.setOnClickListener {
            pickImageGalleryForPopup()
        }

        // Adicionar o OnClickListener ao botão "Salvar"
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
                        val novaIdade = editIdadeV2.text.toString().ifEmpty { currentIdade }?.toIntOrNull()
                        val novaAltura = editAlturaV2.text.toString().ifEmpty { currentAltura }?.toIntOrNull()
                        val novoPeso = editPesoV2.text.toString().ifEmpty { currentPeso }?.toIntOrNull()
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
                                Log.d("EditarPerfil", "Informações atualizadas com sucesso")
                                // Verifique se uma nova imagem foi selecionada
                                selectedImageUri?.let {
                                    uploadImageToFirebaseStorage(it, uid)
                                }
                            }
                            .addOnFailureListener {
                                Log.e("EditarPerfil", "Erro ao atualizar informações", it)
                            }
                    }
                }
            }
            Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnClose.setOnClickListener {

        }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptions.makeCustomAnimation(
                    this, R.anim.fade_in, R.anim.fade_out
                )
                finishAfterTransition()
            } else {
            finish() // Fecha a atividade ao clicar no botão fechar
        }
    }

    private fun pickImageGalleryForPopup() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, FotoBio.IMAGE_REQUEST_CODE_POPUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FotoBio.IMAGE_REQUEST_CODE_POPUP && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            popupImageView.setImageURI(selectedImageUri)  // Atualiza o ImageView
            popupImageView.clipToOutline = true
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/profile_image.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    updateUserProfileImageUrl(imageUrl, userId)
                }.addOnFailureListener { e ->
                    Log.e("EditarPerfil", "Erro ao obter URL da imagem", e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditarPerfil", "Erro ao fazer upload da imagem", e)
            }
    }

    private fun updateUserProfileImageUrl(imageUrl: String, userId: String) {
        val userDocRef = db.collection("clientes").document(userId)
        userDocRef.update("imageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("EditarPerfil", "URL da imagem atualizada com sucesso")
            }
            .addOnFailureListener { e ->
                Log.e("EditarPerfil", "Erro ao atualizar URL da imagem", e)
            }
    }
}
