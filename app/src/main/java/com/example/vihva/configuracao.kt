package com.example.vihva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vihva.databinding.ActivityConfiguracaoBinding
import com.google.firebase.auth.FirebaseAuth

class configuracao : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarLogin = Intent(this,MainActivity::class.java)
            startActivity(voltarLogin)
        }
    }
}