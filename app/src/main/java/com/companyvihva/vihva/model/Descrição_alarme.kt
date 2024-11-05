package com.companyvihva.vihva.model

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Descrição_alarme : AppCompatActivity() {

    private lateinit var descricaoTextView: TextView
    private lateinit var nomeRemedioTextView: TextView

    // Criação das instâncias do FirebaseAuth e FirebaseFirestore
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descricao_alarme)

        // Inicializa as Views
        descricaoTextView = findViewById(R.id.descricao)
        nomeRemedioTextView = findViewById(R.id.nome_remedio_view)

        // Recupera o ID do alarme e o nome do remédio passados via Intent
        val alarmeId = intent.getStringExtra("ALARME_ID")
        val nomeRemedio = intent.getStringExtra("NOME_REMEDIO")
        val descriçãoAlarme = intent.getStringExtra("ALARME_DESCRICAO")
        val tipoMedAlarme = intent.getStringExtra("ALARME_TIPOMED")
        val lembremeAlarme = intent.getStringExtra("ALARME_LEMBREME")
        val dataAlarme = intent.getStringExtra("ALARME_DATA")
        val frequencia = intent.getStringExtra("ALARME_FREQUENCIA")


        // Exibir o nome do remédio se foi passado corretamente
        if (nomeRemedio != null) {
            nomeRemedioTextView.text = nomeRemedio
        } else {
            nomeRemedioTextView.text = "Nome do remédio não encontrado."
        }

        if (tipoMedAlarme != null){
            descricaoTextView.text = "$frequencia; tipo do medicamento: $tipoMedAlarme;" +
                    " será lembrado de comprar mais nesta quantidade de medicamentos: $lembremeAlarme; " +
                    "será avisado até esta data: $dataAlarme; será avisado as $descriçãoAlarme"
        } else {
            descricaoTextView.text = "nada haver"
        }

        // Log para verificar se o ID do alarme está sendo recuperado corretamente
        Log.d("DescriçãoAlarme", "ID do alarme: $alarmeId")

        // Verifica se o ID do alarme foi passado corretamente
        if (alarmeId != null) {
            buscarAlarme(alarmeId)
        } else {
            descricaoTextView.text = "Erro: ID do alarme não encontrado."
        }

        // Configuração do botão de exclusão de alarme
        val btnExcluir = findViewById<ImageButton>(R.id.lixeira_alarme)
        btnExcluir.setOnClickListener {
            alarmeId?.let { id -> showConfirmDeleteDialog(id) }
        }

        // Configuração do botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltarDO)
        btnVoltar.setOnClickListener {
            onBackPressed() // Volta para a tela anterior
        }

        // Obtém o ID do lembrete, altere conforme sua lógica
        val eventoId = intent.getStringExtra("ALARME_ID")
        if (eventoId != null) {
            findViewById<ImageButton>(R.id.lixeira_alarme).setOnClickListener {
                showConfirmDeleteDialogLembrete(eventoId)
            }
        } else {
            Log.w("DescriçãoLembrete", "ID do lembrete não encontrado.")
        }
    }

    private fun buscarAlarme(alarmeId: String) {
        val userId = auth.currentUser?.uid

        // Log para verificar o ID do usuário
        Log.d("DescriçãoAlarme", "ID do usuário: $userId")

        if (userId == null) {
            Log.e("DescriçãoAlarme", "Usuário não autenticado.")
            descricaoTextView.text = "Erro: Usuário não autenticado."
            return
        }

        Log.d("DescriçãoAlarme", "Buscando alarme com ID: $alarmeId para usuário: $userId")

        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("Alarmes")
            .document(alarmeId)

        Log.d("DescriçãoAlarme", "Caminho do documento: ${docRef.path}")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Verificar o conteúdo do documento e a presença do campo "data"
                    val descricao = document.getString("descricao")
                    descricaoTextView.text = descricao ?: "Descrição não disponível"
                    Log.d("DescriçãoAlarme", "Descrição encontrada: $descricao")
                }
            }
    }


    private fun showConfirmDeleteDialog(alarmeId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este alarme? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ -> deleteAlarme(alarmeId) }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    // Método para excluir o alarme individualmente
    private fun deleteAlarme(alarmeId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("Alarmes")
            .document(alarmeId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Alarme excluído com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescriçãoAlarme", "Alarme excluído com sucesso")
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoAlarme", "Erro ao excluir o alarme", e)
                Toast.makeText(this, "Erro ao excluir o alarme", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showConfirmDeleteDialogLembrete(eventoId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação de Exclusão")
            setMessage("Tem certeza que deseja excluir este lembrete? Esta ação não pode ser desfeita.")
            setPositiveButton("Sim") { _, _ -> deleteLembrete(eventoId) }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    private fun deleteLembrete(eventoId: String) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("clientes")
            .document(userId)
            .collection("Lembretes")
            .document(eventoId)

        Log.d("DescriçãoLembrete", "Tentando excluir o lembrete com ID: $eventoId")

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Lembrete excluído com sucesso", Toast.LENGTH_SHORT).show()
                Log.d("DescriçãoLembrete", "Lembrete excluído com sucesso")
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Log.w("DescriçãoLembrete", "Erro ao excluir o lembrete", e)
                Toast.makeText(this, "Erro ao excluir o lembrete", Toast.LENGTH_SHORT).show()
            }
    }

    // Animação da tela
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
