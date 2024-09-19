package com.companyvihva.vihva.Configurações

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableRow
import com.companyvihva.vihva.R

class Configuracoes_manual_usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_manual_usuario)
    }
    val manuInicio = findViewById<TableRow>(R.id.ManuInicio).setOnClickListener {

    }

}