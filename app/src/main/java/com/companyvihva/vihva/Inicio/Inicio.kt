package com.companyvihva.vihva.Inicio

import Inicio1
import Remedio1
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityInicioBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

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

        doencaRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Documentos encontrados no BD
                    val nome = document.getString("nome") ?: ""
                    val imageUrl = document.getString("Url") ?: ""

                    // Atualizando os campos da UI
                    val nomeTextView: TextView = findViewById(R.id.nome_widget)
                    val imageView1: ImageView = findViewById(R.id.image_widget)

                    nomeTextView.text = nome

                    // Carregando a imagem em uma imageView utilizando Picasso
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView1)
                    } else {
                        Toast.makeText(this, "URL da imagem não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Documento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun irParaTelaConfig(view: View) {
        val telaL = Intent(this, Configuracoes::class.java)
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
