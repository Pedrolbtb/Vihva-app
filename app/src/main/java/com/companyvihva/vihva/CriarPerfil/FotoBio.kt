package com.companyvihva.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.companyvihva.vihva.R
import android.util.Log
import android.widget.Toast
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.databinding.ActivityFotoBioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FotoBio : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editTextBiografia: EditText
    private var _binding: ActivityFotoBioBinding? = null
    private val binding get() = _binding
    private val db = FirebaseFirestore.getInstance() // Referência para o Firestore
    private var userUid: String? = null // Variável para armazenar o UID do usuário atual

    companion object {
        val IMAGE_REQUEST_CODE = 100 // Código de solicitação de imagem para o onActivityResult
        private const val TAG = "KotlinActivity" // TAG para logs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio) // Define o layout da atividade

        // Inicializa as views
        imageView = findViewById(R.id.img_save)
        editTextBiografia = findViewById(R.id.Edit_biografia)

        // Recupera os dados da intent anterior
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade", 0)
        val altura = intent.getIntExtra("altura", 0)
        val peso = intent.getIntExtra("peso", 0)
        val genero = intent.getStringExtra("genero")

        // Exibe os dados nas TextViews
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

        // Define um OnClickListener para a imageView
        imageView.setOnClickListener {
            pickImageGallery() // Abre a galeria para selecionar uma imagem
        }

        // Configura o OnClickListener para o botão de retornar
        findViewById<Button>(R.id.btn_retornar).setOnClickListener {

            // Passa os dados para a próxima activity
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

        // Configura o OnClickListener para o botão de próximo
        findViewById<Button>(R.id.btn_proximo).setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            saveData(nome, sobrenome, genero, idade, altura, peso) // Salva os dados no Firestore
            startActivity(intent)
        }
    }

    // Método para abrir a galeria de mídia e selecionar uma imagem
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK) // Cria uma Intent para selecionar um item
        intent.type = "image/*" // Define o tipo de item a ser selecionado (neste caso, qualquer imagem)
        startActivityForResult(intent, IMAGE_REQUEST_CODE) // Inicia a atividade para selecionar um item e espera o resultado
    }

    // Método chamado quando uma atividade iniciada por este aplicativo retorna um resultado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica se o resultado veio da seleção da imagem e se foi bem-sucedido
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Define a imagem selecionada no ImageView
            imageView.setImageURI(data.data)
            imageView.clipToOutline = true // Define a propriedade clipToOutline do ImageView como true para aplicar a forma do contorno do ImageView
            imageView.clipToOutline = true
        }
    }

    // Método para salvar os dados no Firestore
    private fun saveData(nome: String?, sobrenome: String?, genero: String?, idade: Int, altura: Int, peso: Int) {
        // Verifica se algum dos campos essenciais está vazio
        if (nome.isNullOrEmpty() || sobrenome.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser // Obtém o usuário atualmente logado
        userUid = currentUser?.uid // Obtém o UID do usuário

        // Verifica se o UID do usuário foi obtido com sucesso
        if (userUid.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: UID do usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria um mapa com os dados a serem salvos
        val dadosCliente = hashMapOf(
            "nome" to nome,
            "sobrenome" to sobrenome,
            "genero" to genero,
            "idade" to idade,
            "altura" to altura,
            "peso" to peso
        )

        // Adiciona os dados à coleção "clientes" no Firestore usando o UID do usuário como identificador do documento
        db.collection("clientes").document(userUid!!).set(dadosCliente, SetOptions.merge()) //SetOptions.merge()mescla dados
            .addOnSuccessListener {
                Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
