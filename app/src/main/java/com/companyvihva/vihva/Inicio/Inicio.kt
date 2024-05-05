package com.companyvihva.vihva.Inicio

import Inicio1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.CriarPerfil.CriaPerfil2
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityInicioBinding
import com.companyvihva.vihva.databinding.ActivityLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.selects.select


class Inicio : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Inicio1())

        //tela inicia selecionada o inicio
        binding.bottomNavigation.selectedItemId = R.id.inicio

        //codigo da nav bar que leva de um fragment a outro
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.remédio -> replaceFragment(Remedio())

                R.id.calendario -> replaceFragment(Calendario())

                R.id.inicio -> replaceFragment(Inicio1())

                R.id.alarme -> replaceFragment(Alarme())

                R.id.perfil -> replaceFragment(Perfil())

                else -> {
                    false
                }
            }
            true
        }



    }//fim do oncreate

    fun irParaTelaConfig(View:View) {
        val telaL = Intent(this, Configuracoes::class.java)
        telaL.putExtra("name", "Batman")
        telaL.putExtra("phone", 98955)
        startActivity(telaL)
    }



   private fun replaceFragment(fragment: Fragment){
       val fragmentManager = supportFragmentManager
       val fragmentTransaction = fragmentManager.beginTransaction()
       fragmentTransaction.replace(R.id.frame_layout,fragment)
       fragmentTransaction.commit()
   }


}//fim da classe