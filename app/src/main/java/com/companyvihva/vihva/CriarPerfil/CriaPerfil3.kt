package com.companyvihva.vihva.CriarPerfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.companyvihva.vihva.Inicio.Inicio
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CriaPerfil3 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil3)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val checkBoxes = listOf(
            findViewById<CheckBox>(R.id.tabagismo),
            findViewById<CheckBox>(R.id.alcolismo),
            findViewById<CheckBox>(R.id.ciclismo),
            findViewById<CheckBox>(R.id.caminhada),
            findViewById<CheckBox>(R.id.fast_food),
            findViewById<CheckBox>(R.id.good_food),
            findViewById<CheckBox>(R.id.gym),
            findViewById<CheckBox>(R.id.sleep),
            findViewById<CheckBox>(R.id.sono_ruim),
            findViewById<CheckBox>(R.id.agua)
        )

        val btnSalvar = findViewById<Button>(R.id.btn_proximo)
        btnSalvar.setOnClickListener {
            salvarHabitosSelecionados(checkBoxes)
        }
    }

    private fun salvarHabitosSelecionados(checkBoxes: List<CheckBox>) {
        val habitosSelecionados = checkBoxes.filter { it.isChecked }.map { it.text.toString() }

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userDocRef = db.collection("clientes").document(userId)

            userDocRef.update("habitos", habitosSelecionados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Sucesso ao salvar seus hábitos", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Inicio::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar seus hábitos", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
