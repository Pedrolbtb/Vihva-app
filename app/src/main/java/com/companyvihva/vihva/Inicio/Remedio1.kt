import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.Remedio2
import com.google.firebase.firestore.FirebaseFirestore

class Remedio1 : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.remedio2, container, false)

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configura o RecyclerView
        val recyclerViewRemedios = view.findViewById<RecyclerView>(R.id.recyclerView_remedios)
        recyclerViewRemedios.layoutManager = LinearLayoutManager(context)
        recyclerViewRemedios.setHasFixedSize(true)

        // Lista de remédios
        val listaRemedios: MutableList<Remedio2> = mutableListOf()
        val adapterRemedio = AdapterRemedio(requireContext(), listaRemedios)
        recyclerViewRemedios.adapter = adapterRemedio

        // Busca os dados do Firestore para o remédio "insulina"
        firestore.collection("remedios").document("insulina").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nome = document.getString("nome")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    // Cria um objeto Remedio2 com os dados obtidos
                    val insulina = Remedio2(
                        url ?: "",// Se a URL for nula, atribui uma string vazia
                        nome ?: "", // Se o nome for nulo, atribui uma string vazia
                        descricao ?: "" // Se a descrição for nula, atribui uma string vazia
                    )

                    // Adiciona o remédio à lista
                    listaRemedios.add(insulina)

                    // Notifica o adaptador sobre as mudanças nos dados
                    adapterRemedio.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                // Trata o erro, se houver
            }

        return view
    }
}