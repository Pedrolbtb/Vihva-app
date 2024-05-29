package com.companyvihva.vihva.Configurações

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
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

class Configuracoes : AppCompatActivity() {

    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText

    // Definindo constantes e lista de palavras proibidas
    companion object {
        const val PREF_NAME = "socorro"
        const val KEY_DDI = "ddi"
        const val KEY_PHONE = "phone"
        const val KEY_DEFAULT_MSG = "default_msg"
        val PROIBIDAS = listOf(
            "porra", "Porra", "caralho", "Caralho", "Pinto", "pinto", "Buceta", "buceta","boceta","Boceta",
            "puta", "Puta", "caceta", "Caceta", "escroto", "Escroto", "cu", "Cu", "acefalo", "Acefalo", "foder", "Foder",
            "fudeu", "Fudeu", "Fodeu", "fodeu", "fude", "Fude", "Desgraçado", "desgraçado", "Desgraçada", "desgraçada",
            "Vadia", "vadia", "Vadio", "vadio", "Arrombado", "arrombado", "Piroca", "piroca", "Rola", "rola","cuzão",
            "Cuzão","cuzona","Cuzona", "vai tomar no rabo", "Vai tomar no rabo"
        )
    }

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
        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Carregar preferências
        loadPreferences(preferences)

        // Botão para salvar as preferências
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            val message = editTextMessage.text.toString()

            if (containsPalavrasProibidas(message)) {
                Toast.makeText(this, "esta mensagem fere os termos do aplicativo e foi bloqueada", Toast.LENGTH_SHORT).show()
            } else {
                preferences.edit()
                    .putInt(KEY_DDI, spinnerDDI.selectedItemPosition)
                    .putLong(KEY_PHONE, editTextPhone.text.toString().toLong())
                    .putString(KEY_DEFAULT_MSG, message)
                    .apply()

                // Exibir uma mensagem de sucesso
                Toast.makeText(this, getString(R.string.preferences_success), Toast.LENGTH_SHORT).show()
            }
        }

        // Ouvinte do botão para restaurar preferências
        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_warning))
                .setMessage(getString(R.string.text_restore_mensage))
                .setPositiveButton("Sim") { _, _ ->
                    preferences.edit()
                        .remove(KEY_DDI)
                        .remove(KEY_PHONE)
                        .remove(KEY_DEFAULT_MSG)
                        .apply()

                    // Carrega as novas preferências (refresh do Fernandinho)
                    loadPreferences(preferences)
                }
                .setNegativeButton("Não", null)
                .create()
                .show()
        } // Fim do restaurar
    }

    // Método para carregar preferências e atualizar a UI
    private fun loadPreferences(preferences: SharedPreferences) {
        spinnerDDI.setSelection(preferences.getInt(KEY_DDI, 2))
        editTextPhone.setText(preferences.getLong(KEY_PHONE, 0).toString())
        editTextMessage.setText(preferences.getString(KEY_DEFAULT_MSG, getString(R.string.default_msg)))
    }

    // Método para verificar se a mensagem contém palavras proibidas
    private fun containsPalavrasProibidas(message: String): Boolean {
        return PROIBIDAS.any { message.contains(it, ignoreCase = true) }
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
