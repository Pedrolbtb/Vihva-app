package com.companyvihva.vihva.Inicio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.companyvihva.vihva.R
import com.squareup.picasso.Picasso

class Chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userImage = findViewById<ImageView>(R.id.user_image)
        val userName = findViewById<TextView>(R.id.user_name)
        val btnSair = findViewById<ImageButton>(R.id.sair)

        // Recebe os dados do Intent
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL")
        val nome = intent.getStringExtra("EXTRA_NOME")

        // Define o nome completo no TextView
        userName.text = nome

        // Carrega a imagem usando o Picasso
        imageUrl?.let {
            Picasso.get().load(it).into(userImage)
        }

        // Configura o botão de sair
        btnSair.setOnClickListener {
            onBackPressed() // Volta para a tela anterior
        }
    }
}
