package com.companyvihva.vihva.Configurações

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Config_DeletarPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_deletar_perfil)

        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener {
            // Abrir o popup para confirmar a exclusão
            showDeleteProfilePopup()
        }
    }

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

        // Configurar os cliques dos botões
        btnNao.setOnClickListener {
            // Fecha o alertDialog
            alertDialog.dismiss()
        }

        btnSim.setOnClickListener {
            // Excluir a conta do Firebase Authentication
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()

            user?.let { userID ->
                // Referência para o documento do usuário no Firestore
                val userDocRef = db.collection("clientes").document(userID.uid)

                // Deletar documento do usuário no Firestore
                userDocRef.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Documento excluído com sucesso
                            deleteUserFolder(userID.uid) {
                                // Excluir a conta do Firebase Authentication
                                user.delete()
                                    .addOnCompleteListener { deleteTask ->
                                        if (deleteTask.isSuccessful) {
                                            // Conta excluída com sucesso
                                            startActivity(Intent(this, Login::class.java))
                                            finish()
                                        } else {
                                            // Ocorreu um erro ao excluir a conta
                                            Toast.makeText(this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            // Ocorreu um erro ao excluir o documento no Firestore
                            Toast.makeText(this, "Erro ao excluir o perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
            } ?: run {
                // Ocorreu um erro ao excluir a conta
                Toast.makeText(this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show()
            }
           }
        }

    private fun deleteUserFolder(userId: String, onComplete: () -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/")
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val items = listResult.items
                val tasks = items.map { it.delete() }

                // Esperar todas as tarefas de exclusão serem concluídas
                Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Todos os arquivos excluídos com sucesso
                            onComplete()
                        } else {
                            // Lidar com falhas
                            Toast.makeText(this, "Erro ao excluir arquivos na pasta do usuário", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener { e ->
                // Ocorreu um erro ao listar os arquivos na pasta
                Toast.makeText(this, "Erro ao listar arquivos na pasta: ${e.message}", Toast.LENGTH_SHORT).show()
               }
        }
}
