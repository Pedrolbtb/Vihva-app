package com.companyvihva.vihva.Configurações

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Configuracoes.ConfigNotificacoes
import com.companyvihva.vihva.Configuracoes.Configuracoes
import com.companyvihva.vihva.R


class Config_List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_list)

        findViewById<View>(R.id.button_configList_sos).setOnClickListener {
            val telaConfig = Intent(this, Configuracoes::class.java)
            startActivity(telaConfig)
        }

        val btnVoltar = findViewById<ImageButton>(R.id.voltar_configlist)
        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        findViewById<TableRow>(R.id.button_configList_deleteconta).setOnClickListener {
            val telaDelete = Intent(this, Config_DeletarPerfil::class.java)
            startActivity(telaDelete)
            finish()
        }

        val btnSobrenos = findViewById<TableRow>(R.id.button_configList_sobrenos).setOnClickListener {
            val telaDelete = Intent(this, Config_Sobrenos::class.java)
            startActivity(telaDelete)
            finish()
        }

        val btnManual = findViewById<TableRow>(R.id.button_configlist_manual).setOnClickListener {
            val telaManu = Intent(this,Configuracoes_manual_usuario::class.java)
            startActivity(telaManu)
            finish()
        }

    }
}
