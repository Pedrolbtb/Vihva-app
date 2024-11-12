package com.companyvihva.vihva.CriarPerfil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.companyvihva.vihva.R
import android.util.Log
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configuracoes.ConfigNotificacoes
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.databinding.ActivityFotoBioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class FotoBio : AppCompatActivity() {
    // Declaração da propriedade lateinit para a imageView
    private lateinit var imageView: ImageView
    private lateinit var editTextBiografia: EditText
    private var _binding: ActivityFotoBioBinding? = null
    private val binding get() = _binding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Companion object para declarar uma constante para o código de solicitação de imagem
    companion object {
        const val IMAGE_REQUEST_CODE = 100
        const val IMAGE_REQUEST_CODE_POPUP = 101
        private const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio) // Define o layout da atividade
        // Inicializa a imageView com base em seu ID no layout
        imageView = findViewById(R.id.img_save)
        editTextBiografia = findViewById(R.id.Edit_biografia)

        // Recuperando os extras da intent
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade", 0)
        val altura = intent.getIntExtra("altura", 0)
        val peso = intent.getIntExtra("peso", 0)
        val genero = intent.getStringExtra("genero")

        // Exibindo os dados nas TextViews
        val textNome = findViewById<TextView>(R.id.text_nome)
        textNome.text = "$nome $sobrenome"
        val textIdade = findViewById<TextView>(R.id.text_idade)
        textIdade.text = "$idade anos"
        val textAltura = findViewById<TextView>(R.id.text_altura)
        textAltura.text = "$altura cm"
        val textPeso = findViewById<TextView>(R.id.text_peso)
        textPeso.text = "$peso kg"
        val textGenero = findViewById<TextView>(R.id.text_genero)
        textGenero.text = "$genero"

        // Define um ouvinte de clique para a imageView
        imageView.setOnClickListener {
            pickImageGallery() // Chama o método para abrir a galeria
        }

        // Configura o OnClickListener para o botão retornar
        findViewById<Button>(R.id.btn_retornar).setOnClickListener {
            val altura = intent.getIntExtra("altura", 0)
            val peso = intent.getIntExtra("peso", 0)
            val genero = intent.getStringExtra("genero")
            val nome = intent.getStringExtra("nome")
            val sobrenome = intent.getStringExtra("sobrenome")
            val idade = intent.getIntExtra("idade", 0)
            val criaPerfil2 = Intent(this, CriaPerfil2::class.java)
            criaPerfil2.putExtra("altura", altura)
            criaPerfil2.putExtra("peso", peso)
            criaPerfil2.putExtra("genero", genero)
            criaPerfil2.putExtra("nome", nome)
            criaPerfil2.putExtra("sobrenome", sobrenome)
            criaPerfil2.putExtra("idade", idade)
            startActivity(criaPerfil2)
            finish()
        }

        findViewById<Button>(R.id.btn_proximo).setOnClickListener {
            val intent = Intent(this, CriaPerfil3::class.java)
            val biografia = editTextBiografia.text.toString()
            saveData(nome, sobrenome, genero, idade, altura, peso, biografia)
            startActivity(intent)
            finish()
        }
    }



    // Método para abrir a galeria de mídia e selecionar uma imagem
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK) // Criação de uma Intent para selecionar um item
        intent.type = "image/*" // Define o tipo de item a ser selecionado (neste caso, qualquer imagem)
        startActivityForResult(intent, IMAGE_REQUEST_CODE) // Inicia uma atividade para selecionar um item e espera o resultado
    }

    // Função chamada quando uma atividade iniciada por este aplicativo retorna um resultado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica se o resultado veio da seleção da imagem e se foi bem-sucedido
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Define a imagem selecionada no ImageView
            imageView.setImageURI(data.data)
            // Define a propriedade clipToOutline do ImageView como true para aplicar a forma do contorno do ImageView
            imageView.clipToOutline = true
        }
    }

    // Método para salvar os dados no banco de dados Firestore
    private fun saveData(
        nome: String?,
        sobrenome: String?,
        genero: String?,
        idade: Int,
        altura: Int,
        peso: Int,
        biografia: String?
    ) {
        // Obter o UID do usuário autenticado
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Cria um mapa com os dados adicionais do perfil
            val dadosPerfil = hashMapOf(
                "nome" to nome!!,
                "sobrenome" to sobrenome!!,
                "genero" to genero!!,
                "idade" to idade,
                "altura" to altura,
                "peso" to peso,
                "biografia" to biografia!!
            )

            // Converter a imagem para um array de bytes
            val drawable = imageView.drawable
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                val data = outputStream.toByteArray()

                // Referência ao storage do Firebase para armazenar a imagem
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val storageRef = storage.reference.child("users/$uid/profile_image.jpg")

                val uploadTask = storageRef.putBytes(data)
                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Adicionar a URL da imagem aos dados do perfil
                        dadosPerfil["imageUrl"] = uri.toString()

                        // Atualiza o documento existente com os dados adicionais do perfil
                        if (uid != null) {
                            db.collection("clientes").document(uid).update(dadosPerfil as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao fazer upload da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Se não houver imagem, salvar apenas os dados do perfil
                db.collection("clientes").document(uid).update(dadosPerfil as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    // Animação da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
