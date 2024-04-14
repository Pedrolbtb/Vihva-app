package com.example.vihva.CriarPerfil

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.vihva.R

class FotoBio : AppCompatActivity() {

    // Declaração da propriedade lateinit para a imageView
    private lateinit var imageView: ImageView

    // Companion object para declarar uma constante para o código de solicitação de imagem
    companion object{
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_bio) // Define o layout da atividade

        // Inicializa a imageView com base em seu ID no layout
        imageView = findViewById(R.id.img_save)

        // Define um ouvinte de clique para a imageView
        imageView.setOnClickListener {
            pickImageGallery() // Chama o método para selecionar uma imagem da galeria
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
