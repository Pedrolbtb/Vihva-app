package com.companyvihva.vihva.Configurações

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.companyvihva.vihva.R

class Config_Sobrenos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_sobrenos)

       val btnVoltar = findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
           finish()
       }
    }
}