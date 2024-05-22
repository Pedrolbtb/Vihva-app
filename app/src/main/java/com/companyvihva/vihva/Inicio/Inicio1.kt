import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.companyvihva.vihva.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Inicio1 : Fragment() {

    // Firebase
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)

        // Inicializa o Firebase
        db = FirebaseFirestore.getInstance()

        // Referência ao documento "doenca" na coleção, precisa ajustar conforme sua estrutura
        val doencaRef = db.collection("doenca").document("diabetes")

        doencaRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Documentos encontrados no BD
                    val nome = document.getString("nome") ?: ""
                    val imageUrl = document.getString("Url") ?: ""

                    // Atualizando os campos da UI
                    val nomeTextView: TextView = view.findViewById(R.id.nome_widget)
                    val imageView1: ImageView = view.findViewById(R.id.image_widget)

                    nomeTextView.text = nome

                    // Carregando a imagem em uma imageView utilizando Picasso
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView1)
                    } else {
                        Toast.makeText(requireContext(), "URL da imagem não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Documento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Encontra o botão de imagem
        val card_diabete = view.findViewById<View>(R.id.card_diabete)

        card_diabete.setOnClickListener {
            // Chama o método para mostrar o popup quando o botão é clicado
            mostrarPopup()
        }

        // Encontrar o botão dentro do popup para fechar o AlertDialog
        //val close_button = popupView.findViewById<Buttom>(R.id.close_button)
        // Configurar o clique do botão para fechar o AlertDialog
        //close_button.setOnClickListener {
            //fecha
            //alertDialog.dismiss()
        //}

        // Retorna a view inflada
        return view
    }

    // Método para mostrar os dados no popup
    private fun mostrarPopup() {
        // Referência ao documento "diabetes" na coleção "doenca"
        val docRef = db.collection("doenca").document("diabetes")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtém dados do documento Firestore
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val imageUrl1 = document.getString("Url")
                    val imageUrl2 = document.getString("Url2")

                    // Infla o layout do popup
                    val inflater = requireActivity().layoutInflater
                    val popupView = inflater.inflate(R.layout.popup_descricao, null)

                    // Encontra elementos no layout
                    val nomeTextView: TextView = popupView.findViewById(R.id.diabetes)
                    val descricaoTextView: TextView = popupView.findViewById(R.id.descricao)
                    val imageView1: ImageView = popupView.findViewById(R.id.foto_diabete1)
                    val imageView2: ImageView = popupView.findViewById(R.id.foto_diabete2)

                    // Define dados nos TextViews
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao

                    // Carrega imagens com o Picasso
                    if (imageUrl1 != null && imageUrl2 != null) {
                        Picasso.get().load(imageUrl1).into(imageView1)
                        Picasso.get().load(imageUrl2).into(imageView2)
                    }

                    // Mostra o popup
                    val popupWindow = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setView(popupView)
                        .create()
                    popupWindow.show()
                } else {
                    // Trata documento não encontrado
                    Log.d("Inicio1", "Documento não encontrado")
                }
            }
            .addOnFailureListener { exception ->
                // Trata falhas
                Log.e("Inicio1", "Erro ao obter documento", exception)
            }


    }
}
