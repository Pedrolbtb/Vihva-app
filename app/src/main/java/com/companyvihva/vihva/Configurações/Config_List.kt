package com.companyvihva.vihva.Configurações

import Inicio1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.ImageButton
import com.companyvihva.vihva.R

class Config_List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_list)

        val configlist_sos = findViewById<View>(R.id.button_configList_sos).setOnClickListener {
            val telaConfig = Intent(this, Configuracoes::class.java)
            startActivity(telaConfig)

        val voltr_configList = findViewById<ImageButton>(R.id.voltar_configList).setOnClickListener {
            val telaInicio = Intent(this, Inicio1::class.java)
            startActivity(telaInicio)
        }
        }
    }
}