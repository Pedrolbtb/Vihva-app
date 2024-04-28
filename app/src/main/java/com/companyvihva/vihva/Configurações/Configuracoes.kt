package com.companyvihva.vihva.Configurações

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.companyvihva.vihva.R

class Configuracoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val intent = intent
        val name = intent.getStringExtra("name")
        val phone = intent.getIntExtra("phone", 0)

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()

    }
}