package com.companyvihva.vihva.Inicio
import Inicio1
import Remedio1
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.CriarPerfil.CriaPerfil2
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityInicioBinding
import com.companyvihva.vihva.databinding.ActivityLoginBinding
import com.companyvihva.vihva.model.Remedio2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.selects.select

class Inicio : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding

    //FireBase
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Emplementando o firebase no código
       db = FirebaseFirestore.getInstance()

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Inicio1())
//tela inicia selecionada o inicio
        binding.bottomNavigation.selectedItemId = R.id.inicio




//codigo da nav bar que leva de um fragment a outro
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.remédio -> replaceFragment(Remedio1())
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

        //Verifica se o app não tem as permissões de GPS
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

       //Não demos a permissão

        }else{
            //Temos a permissão

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