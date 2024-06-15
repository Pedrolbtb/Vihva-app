package com.companyvihva.vihva.Inicio
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Adapter.AdapterRemedio
import com.companyvihva.vihva.model.Tipo_Classe
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent


class Inicio1 : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListanova: AdapterListanova
    private lateinit var listaInicio: MutableList<Tipo_Remedios>
    private lateinit var urlImageView: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnSOS: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)
        val textAviso: TextView? = view.findViewById(R.id.textViewAviso)
        val fullText = "Texto Exemplo"
        val spannableString = SpannableString(fullText)

        val redColor = ContextCompat.getColor(requireContext(), R.color.vermelho_alerta)
        spannableString.setSpan(
            ForegroundColorSpan(redColor),
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textAviso?.text = spannableString

        db = FirebaseFirestore.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        btnSOS = view.findViewById(R.id.sos)
        btnSOS.setOnClickListener {
            requestPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            )
        }

        setupRecyclerView(view)
        fetchRemediosDoUsuario()

        setupDiabetesInfo(view)

        return view
    }
    ////////////////////////////////////////////////////////////////////////////////////////
// Método setupRecyclerView
    private fun setupRecyclerView(view: View) {
        // Encontra a RecyclerView no layout com o ID Recyclerview_remedioAdicionado
        val recyclerview_remedioAdicionado =
            view.findViewById<RecyclerView>(R.id.Recyclerview_remedioAdicionado)

        // Define o layout manager para a RecyclerView como LinearLayoutManager vertical
        recyclerview_remedioAdicionado.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Define que a RecyclerView terá um tamanho fixo para otimizar o desempenho
        recyclerview_remedioAdicionado.setHasFixedSize(true)

        // Inicializa uma lista vazia para conter os itens da RecyclerView
        listaInicio = mutableListOf()

        // Inicializa o adapter da RecyclerView com a lista vazia
        adapterListanova = AdapterListanova(requireContext(), listaInicio)

        // Define o adapter criado para a RecyclerView
        recyclerview_remedioAdicionado.adapter = adapterListanova
    }

    ////////////////////////////////////////////////////////////////////////////////////////
// Método fetchRemediosDoUsuario
    // Método fetchRemediosDoUsuario para buscar os IDs dos remédios do usuário
    private fun fetchRemediosDoUsuario() {
        // Obtém o usuário atualmente logado
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        // Verifica se o UID do usuário não é nulo
        if (uid != null) {
            // Obtém a referência do documento do cliente no Firestore
            val clientRef = db.collection("clientes").document(uid)

            // Realiza a leitura assíncrona do documento do cliente
            clientRef.get()
                .addOnSuccessListener { document ->
                    // Verifica se o documento existe e contém dados
                    if (document != null && document.exists()) {
                        // Obtém os IDs dos remédios do documento
                        val remediosIds = document.get("remedios") as? List<String>

                        // Verifica se existem remédios para o usuário
                        remediosIds?.let {
                            // Itera sobre os IDs dos remédios e chama o método para buscar os dados de cada um
                            for (remedioId in it) {
                                fetchDadosDoFirebase(remedioId)
                            }
                        }
                    } else {
                        // Loga uma mensagem de erro caso o documento do cliente não seja encontrado
                        Log.d("Inicio1", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    // Loga uma mensagem de erro em caso de falha ao obter o documento do cliente
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                }
        }
    }

    // Método fetchDadosDoFirebase para buscar os detalhes de cada remédio usando o ID
    private fun fetchDadosDoFirebase(remedioId: String) {
        // Obtém a referência do documento do remédio no Firestore
        val docRef = db.collection("remedios").document(remedioId)

        // Realiza a leitura assíncrona do documento do remédio
        docRef.get()
            .addOnSuccessListener { document ->
                // Verifica se o documento existe e contém dados
                if (document != null && document.exists()) {
                    // Obtém os campos do documento
                    val nome = document.getString("nome")
                    val tipo = document.getString("tipo")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    // Verifica se nome, tipo e descricao não são nulos antes de criar o objeto Tipo_Remedios
                    if (nome != null && tipo != null && descricao != null) {
                        // Cria um objeto Tipo_Remedios com os dados obtidos
                        val remedio = Tipo_Remedios(url ?: "", nome, tipo, descricao)

                        // Atualiza a lista de remédios e notifica o adapter sobre a mudança nos dados
                        atualizarListaRemedios(remedio)
                    } else {
                        // Loga uma mensagem de aviso caso nome, tipo ou descricao do remédio sejam nulos
                        Log.d("Inicio1", "Nome, tipo ou descricao do remédio está nulo")
                    }
                } else {
                    // Loga uma mensagem de erro caso o documento do remédio não seja encontrado
                    Log.d("Inicio1", "Documento do remédio não encontrado")
                }
            }
            .addOnFailureListener { e ->
                // Loga uma mensagem de erro em caso de falha ao obter o documento do remédio
                Log.w("Inicio1", "Erro ao obter documento do remédio", e)
            }
    }

    // Método atualizarListaRemedios para adicionar o remédio à lista e notificar o adapter
    private fun atualizarListaRemedios(remedio: Tipo_Remedios) {
        // Adiciona o remédio à lista de remédios
        listaInicio.add(remedio)

        // Notifica o adapter que os dados mudaram para atualizar a RecyclerView
        adapterListanova.notifyDataSetChanged()
    }

    ////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun setupDiabetesInfo(view: View) {
        val doencaRef = db.collection("doenca").document("diabetes")
        doencaRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome") ?: ""
                    val imageUrl = document.getString("Url") ?: ""
                    val nomeTextView: TextView = view.findViewById(R.id.nome_widget)
                    val imageView1: ImageView = view.findViewById(R.id.image_widget)
                    nomeTextView.text = nome
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView1)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "URL da imagem não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Documento não encontrado", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar dados: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun requestPermissions(vararg permissions: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                permissions[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                if (location != null) {
                    val preferences = requireContext().getSharedPreferences(
                        Configuracoes.PREF_NAME, AppCompatActivity.MODE_PRIVATE)

                    val savedMessage = preferences.getString(
                        Configuracoes.KEY_DEFAULT_MSG,
                        "Mensagem padrão não definida"
                    )

                    val smsMessage = "$savedMessage\nhttps://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"

                    val smsManager: SmsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(
                        "+5519989769783",
                        null,
                        smsMessage,
                        null,
                        null
                    )

                    Toast.makeText(
                        requireContext(),
                        "SMS enviado com a localização e a mensagem salva!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Localização não disponível",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Erro ao obter localização: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            }
        }
}