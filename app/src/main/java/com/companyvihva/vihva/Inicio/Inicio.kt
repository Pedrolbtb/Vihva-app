package com.companyvihva.vihva.Inicio

import Inicio1

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configurações.Config_List
import com.companyvihva.vihva.R
import com.companyvihva.vihva.Remedio1
import com.companyvihva.vihva.databinding.ActivityInicioBinding
import com.google.firebase.firestore.FirebaseFirestore

class Inicio : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding

    //FireBase
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciando o firebase no código
        db = FirebaseFirestore.getInstance()

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carregar os dados do Firestore
        loadDoencaData()

        // Configurando o fragmento inicial
        replaceFragment(Inicio1())

        // Tela inicial selecionada como início
        binding.bottomNavigation.selectedItemId = R.id.inicio

        // Código da nav bar que leva de um fragmento a outro
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.remédio -> replaceFragment(Remedio1())
                R.id.calendario -> replaceFragment(com.companyvihva.vihva.Inicio.Calendario())
                R.id.inicio -> replaceFragment(Inicio1())
                R.id.alarme -> replaceFragment(Alarme())
                R.id.perfil -> replaceFragment(com.companyvihva.vihva.Inicio.Perfil())
                else -> {
                    false
                }
            }
            true
        }

        // Verifica se o app não tem as permissões de GPS
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Não temos a permissão
            // Solicitar permissões aqui, se necessário
        } else {
            // Temos a permissão
        }
    }

    private fun loadDoencaData() {
        // Referência ao documento/coleção no Cloud Firestore
        val doencaRef = db.collection("doenca").document("diabetes")


    }

    fun irParaTelaConfig(view: View) {
        val telaL = Intent(this, Config_List::class.java)
        telaL.putExtra("name", "Batman")
        telaL.putExtra("phone", 98955)
        startActivity(telaL)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}


