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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class FotoBio : AppCompatActivity() {

    val firebaseRef = FirebaseDatabase.getInstance().reference

    // Declaração da propriedade lateinit para a imageView
    private lateinit var imageView: ImageView
    private lateinit var editTextBiografia: EditText
    private lateinit var contadorCaracteres: TextView
    private var _binding: ActivityFotoBioBinding? = null
    private val binding get() = _binding

    // Companion object para declarar uma constante para o código de solicitação de imagem
    companion object {
        val IMAGE_REQUEST_CODE = 100
        private const val TAG = "KotlinActivit  y"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio) // Define o layout da atividade

        // Inicializa a imageView com base em seu ID no layout
        imageView = findViewById(R.id.img_save)
        editTextBiografia = findViewById(R.id.Edit_biografia)


        //Recuperando os extras da intent
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade", 0)
        val altura = intent.getIntExtra("altura", 0)
        val peso = intent.getIntExtra("peso", 0)
        val genero = intent.getStringExtra("genero")

        //Exibindo os dados nas TextViews
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
            pickImageGallery() // Chama o método para selecionar uma imagem da galeria
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
            val intent = Intent(this, Inicio::class.java)
            saveData()
            startActivity(intent)
        }


    }
////////////////////////////////////////////////////////////////////////////////////////////

    // Método para abrir a galeria de mídia e selecionar uma imagem
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK) // Criação de uma Intent para selecionar um item
        intent.type =
            "image/*" // Define o tipo de item a ser selecionado (neste caso, qualquer imagem)
        startActivityForResult(
            intent,
            IMAGE_REQUEST_CODE
        ) // Inicia uma atividade para selecionar um item e espera o resultado
    }

    // Função chamada quando uma atividade iniciada por este aplicativo retorna um resultado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica se o resultado veio da seleção da imagem e se foi bem-sucedido
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Define a imagem selecionada no ImageView
            imageView.setImageURI(data.data)

            imageView.clipToOutline = true
            // Define a propriedade clipToOutline do ImageView como true para aplicar a forma do contorno do ImageView
            imageView.clipToOutline = true
        }
    }

    /////////////////////////////BANCO DE DADOS/////////////////////////////////////////

    private fun saveData() {
        val nome = binding?.textNome?.text.toString()
        val idade = binding?.textIdade?.text.toString()

        val login_cliente = hashMapOf(
            "nome" to nome,
            "idade" to idade
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("Vihva").add(login_cliente)
            .addOnSuccessListener {
                Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar os dados", Toast.LENGTH_SHORT).show()


                fun basicReadWrite() {
                    // [START write_message]
                    // Write a message to the database
                    val database = Firebase.database
                    val myRef = database.getReference("message")

                    myRef.setValue("Hello, World!")
                    // [END write_message]

                    // [START read_message]
                    // Read from the database
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nome = intent.getStringExtra("nome")
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            val value = dataSnapshot.getValue<String>()
                            Log.d(TAG, "O nome do usuário é $nome")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(TAG, "Falha ao registrar.", error.toException())
                        }
                    })
                    // [END read_message]
                }
            }
    }
}
