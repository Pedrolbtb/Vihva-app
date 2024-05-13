package com.companyvihva.vihva.CriarPerfil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityFotoBioBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class FotoBio : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editTextBiografia: EditText
    private lateinit var contadorCaracteres: TextView
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val IMAGE_REQUEST_CODE = 100
        private const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio)

        imageView = findViewById(R.id.img_save)
        editTextBiografia = findViewById(R.id.Edit_biografia)

        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade", 0)
        val altura = intent.getIntExtra("altura", 0)
        val peso = intent.getIntExtra("peso", 0)
        val genero = intent.getStringExtra("genero")

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

        imageView.setOnClickListener {
            pickImageGallery()
        }

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
            val biografia = editTextBiografia.text.toString()
            saveData(nome, sobrenome, genero, idade, altura, peso, biografia)
            startActivity(intent)
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageView.setImageURI(data.data)
            imageView.clipToOutline = true
        }
    }

    private fun saveData(
        nome: String?,
        sobrenome: String?,
        genero: String?,
        idade: Int,
        altura: Int,
        peso: Int,
        biografia: String?
    ) {
        val dadosCliente = HashMap<String, Any>()
        dadosCliente["nome"] = nome!!
        dadosCliente["sobrenome"] = sobrenome!!
        dadosCliente["genero"] = genero!!
        dadosCliente["idade"] = idade
        dadosCliente["altura"] = altura
        dadosCliente["peso"] = peso
        dadosCliente["biografia"] = biografia!!

        //try {
            // Convertendo a imagem em bytes
           // val drawable = imageView.drawable
           // val bitmap = (drawable as BitmapDrawable).bitmap
           // val outputStream = ByteArrayOutputStream()
           /// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
           // val imageData = outputStream.toByteArray()
           // dadosCliente["bytesImagem"] = imageData

            // Adiciona os dados à coleção "clientes" no Firestore
            db.collection("clientes").add(dadosCliente)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Dados salvos com sucesso: $documentReference")
                    Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao salvar os dados: ", e)
                    Toast.makeText(
                        this,
                        "Erro ao salvar os dados: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
       // } catch (e: Exception) {
         //   Log.e(TAG, "Erro ao converter a imagem em bytes: ", e)
          //  Toast.makeText(
           //     this,
            //    "Erro ao converter a imagem em bytes: ${e.message}",
             //   Toast.LENGTH_SHORT
           // ).show()
        }
    }
//}

