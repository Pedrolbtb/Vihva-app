import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.companyvihva.vihva.Login.Login
import com.google.firebase.auth.FirebaseAuth
import com.companyvihva.vihva.R

class Deletar_perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener {
            // Abrir o popup para confirmar a exclusão
            showDeleteProfilePopup()
        }

        // Restante do seu código...
    }

    private fun showDeleteProfilePopup() {
        // Inflar o layout do popup
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_deletar_perfil, null)

        // Criar um AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(popupView)

        // Adicionar botões ao AlertDialog
        alertDialogBuilder
            .setCancelable(false)
            .setNegativeButton("Cancelar") { dialog, id ->
                // Fechar o AlertDialog
                dialog.dismiss()
            }
            .setPositiveButton("Confirmar") { dialog, id ->
                // Excluir a conta do Firebase Authentication
                val user = FirebaseAuth.getInstance().currentUser
                user?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Conta excluída com sucesso
                            startActivity(Intent(this, Login::class.java))
                        } else {
                            // Ocorreu um erro ao excluir a conta
                            // Exiba uma mensagem de erro ao usuário ou lide com o erro de outra forma
                            Toast.makeText(this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        // Criar e mostrar o AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
