package com.companyvihva.vihva.Configurações

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R

class Config_List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_list)

        findViewById<View>(R.id.button_configList_sos).setOnClickListener {
            val telaConfig = Intent(this, Configuracoes::class.java)
            startActivity(telaConfig)
        }

        findViewById<ImageButton>(R.id.voltar_configList).setOnClickListener {
            val telaInicio = Intent(this, Inicio::class.java)
            startActivity(telaInicio)
        }

        findViewById<TableRow>(R.id.button_configList_deleteconta).setOnClickListener {
            val telaDelete = Intent(this, Config_DeletarPerfil::class.java)
            startActivity(telaDelete)
            finish()
        }
    }
}
