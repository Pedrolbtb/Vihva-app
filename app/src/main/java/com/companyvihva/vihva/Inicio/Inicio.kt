package com.companyvihva.vihva.Inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
    }//fim do oncreate

    fun irParaTelaConfig(View:View) {
        val telaL = Intent(this, Configuracoes::class.java)
        telaL.putExtra("name", "Batman")
        telaL.putExtra("phone", 98955)
        startActivity(telaL)
    }

}//fim da classe