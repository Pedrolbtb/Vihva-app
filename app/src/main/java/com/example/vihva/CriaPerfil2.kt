package com.example.vihva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class CriaPerfil2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cria_perfil2)

        val edit_peso: EditText = findViewById(R.id.edit_peso)

        edit_peso.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val input = it.toString()
                    if (input.length == 2 && !input.contains(".")) {
                        edit_peso.setText("$input.")
                        edit_peso.setSelection(edit_peso.text.length)
                    }
                }
            }
        })
    }
}