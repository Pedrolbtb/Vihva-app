package com.example.vihva.CriarPerfil

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.vihva.R

class FotoBio : AppCompatActivity() {

    // Declaração da propriedade lateinit para a imageView
    private lateinit var imageView: ImageView
    private lateinit var editTextBiografia: EditText
    private lateinit var contadorCaracteres: TextView

    // Companion object para declarar uma constante para o código de solicitação de imagem
    companion object{
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio) // Define o layout da atividade

        // Inicializa a imageView com base em seu ID no layout
        imageView = findViewById(R.id.img_save)
        editTextBiografia = findViewById(R.id.Edit_biografia)
        contadorCaracteres = findViewById(R.id.contador_caracteres)

        //COnfigura um TextWatcher para a EditTExt de biografia
        editTextBiografia.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}

            override fun afterTextChanged(s: Editable?){
                // Atualiza o contador de caracteres
                s?.let{
                    val caracteresDigitados = s.length
                    contadorCaracteres.text = "$caracteresDigitados/250"
                }
            }
        })

        //Recuperando os extras da intent
        val nome = intent.getStringExtra("nome")
        val sobrenome = intent.getStringExtra("sobrenome")
        val idade = intent.getIntExtra("idade",0)
        val altura = intent.getIntExtra("altura",0)
        val peso = intent.getIntExtra("peso",0)
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
            val idade = intent.getIntExtra("idade",0 )

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

    }


    // Método para abrir a galeria de mídia e selecionar uma imagem
    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK) // Cria um intent para abrir a galeria
        intent.type = "image/*" // Define o tipo de mídia como imagens
        startActivityForResult(intent, IMAGE_REQUEST_CODE) // Inicia a atividade esperando um resultado
    }

    // Método chamado quando o resultado da atividade anterior é retornado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica se o resultado é para a solicitação de imagem e foi bem-sucedido
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK ){
            imageView.setImageURI(data?.data) // Define a imagem selecionada na imageView
        }
    }
}
