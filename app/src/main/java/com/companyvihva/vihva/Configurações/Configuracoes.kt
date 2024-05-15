package com.companyvihva.vihva.Configurações;

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.R
import com.companyvihva.vihva.databinding.ActivityConfiguracoesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener {
            // Abrir o popup para confirmar a exclusão
            showDeleteProfilePopup()
        }

        // Vincula elementos do layout com variáveis Kotlin
        spinnerDDI = findViewById(R.id.spinnerDDI)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextMessage = findViewById(R.id.editTextMsg)

        // Obter um conjunto de preferências do aplicativo
        val preferences = getSharedPreferences(
            "socorro",
            MODE_PRIVATE
        )

        // Carregar preferências
        loadPreferences(preferences)

        // Botão para salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            preferences.edit()
                .putInt("ddi", spinnerDDI.selectedItemPosition)
                .putLong("phone", editTextPhone.text.toString().toLong())
                .putString("default_msg", editTextMessage.text.toString())
                .apply()

            // Exibir uma mensagem de sucesso
            Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT)
                .show()
        }

        // Ouvinte do botão para restaurar preferências
        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            // Aqui você pode implementar a lógica para restaurar as preferências
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_warning))
                .setMessage(getString(R.string.text_restore_mensage))
                .setPositiveButton("Sim", DialogInterface.OnClickListener{dialogInterface, i ->


                    preferences.edit()
                        .remove("ddi")
                        .remove("phone")
                        .remove("default_msg")
                        .apply()

                    //carrega as novas preferencias(similar ao refresh em aplicações web
                    loadPreferences(preferences)
                })
                .setNegativeButton("Não", null)
                .create()
                .show()
        }//Fim do restaurar
    }

    // Método para carregar preferências e atualizar a UI
    private fun loadPreferences(preferences: SharedPreferences) {
        spinnerDDI.setSelection(preferences.getInt("ddi", 2))
        editTextPhone.setText(preferences.getLong("phone", 0).toString())
        editTextMessage.setText(

            preferences.getString(
                "default_msg",
                getString(R.string.default_msg)
            )
        )
    }

    // Método para mostrar o popup de exclusão de perfil
    private fun showDeleteProfilePopup() {
        // Inflar o layout do popup
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_deletar_perfil, null)

        // Criar um AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(popupView)

        // Encontrar os botões dentro do popup
        val btnSim = popupView.findViewById<Button>(R.id.btn_sim)
        val btnNao = popupView.findViewById<Button>(R.id.btn_nao)

        // Criar e mostrar o AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        //Configurar os cliques dos botões
        btnNao.setOnClickListener {
            // Fecha o alertDialog
            alertDialog.dismiss()
        }

        btnSim.setOnClickListener {
            // Excluir a conta do Firebase Authentication
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()

            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        //Excluir o documento do perfil do usuario no Firestore
                        user.uid.let { userID ->
                            db.collection("clientes").document(userID)
                                .delete()
                                .addOnCompleteListener {
                                    //Documento excluido com sucesso
                                    startActivity(Intent(this, Login::class.java))
                                }
                                .addOnFailureListener{ e ->
                                    //Ocorreu um erro ao excluir usuario abre um toast
                                    Toast.makeText(this,"Erro ao excluir o perfil ", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Ocorreu um erro ao excluir a conta
                        // Exiba uma mensagem de erro ao usuário ou lide com o erro de outra forma
                        Toast.makeText(this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


// fim do loadPreferences
//fim da classe

