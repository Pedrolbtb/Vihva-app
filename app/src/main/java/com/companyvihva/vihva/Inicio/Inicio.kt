package com.companyvihva.vihva.Inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.remédio -> {
                    val intentremedio = Intent(this, Remedio::class.java)
                    startActivity(intentremedio)
                    true
                }
                R.id.calendario -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.inicio -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.alarme -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.perfil -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }


    }//fim do oncreate

    fun irParaTelaConfig(View:View) {
        val telaL = Intent(this, Configuracoes::class.java)
        telaL.putExtra("name", "Batman")
        telaL.putExtra("phone", 98955)
        startActivity(telaL)
    }

}//fim da classe