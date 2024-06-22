package com.companyvihva.vihva.Inicio

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Alarme.ConfigFrequencia
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca.AdicionarDoenca
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterDoenca
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Adapter.AdapterRemedio_inicio1
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Inicio1 : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListanova: AdapterRemedio_inicio1
    private lateinit var adapterDoenca: AdapterDoenca
    private lateinit var listaInicio: MutableList<Tipo_Remedios>
    private lateinit var listadoenca: MutableList<Tipo_Remedios>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnSOS: Button
    private lateinit var recyclerview_doenca: RecyclerView
    private lateinit var recyclerViewRemedioAdicionado: RecyclerView
    private lateinit var textview_naotemremedio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)
        textview_naotemremedio = view.findViewById(R.id.textview_naotemremedio)
        recyclerViewRemedioAdicionado = view.findViewById(R.id.Recyclerview_remedioAdicionado)
        recyclerview_doenca = view.findViewById(R.id.recyclerview_doenca) // Adicionado setup aqui

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
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview_doenca)

        val btnAddDoenca: ImageButton = view.findViewById(R.id.image_adddoenca)
        btnAddDoenca.setOnClickListener {
            val intent = Intent(requireContext(), AdicionarDoenca::class.java)
            startActivity(intent)
        }

        // Configuração das RecyclerViews
        setupRecyclerView(view)
        setupRecyclerView2(view)

        // Busca dos dados dos usuários
        fetchRemediosDoUsuario()
        fetchRemediosDoUsuario2()

        val imageLixeiraGlobal: ImageView = view.findViewById(R.id.image_lixeira_global)
        imageLixeiraGlobal.setOnClickListener {
            deletarArrayRemedios()
        }

        return view
    }

    // Configuração da RecyclerView para remédios
    private fun setupRecyclerView(view: View) {
        recyclerViewRemedioAdicionado.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRemedioAdicionado.setHasFixedSize(true)

        listaInicio = mutableListOf()
        adapterListanova = AdapterRemedio_inicio1(requireContext(), listaInicio)
        recyclerViewRemedioAdicionado.adapter = adapterListanova
    }

    // Busca dos remédios do usuário
    private fun fetchRemediosDoUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val remediosIds = document.get("remedios") as? List<String>
                        if (remediosIds.isNullOrEmpty()) {
                            textview_naotemremedio.visibility = View.VISIBLE
                            recyclerViewRemedioAdicionado.visibility = View.GONE
                        } else {
                            textview_naotemremedio.visibility = View.GONE
                            recyclerViewRemedioAdicionado.visibility = View.VISIBLE
                            for (remedioId in remediosIds) {
                                fetchDadosDoFirebase(remedioId)
                            }
                        }
                    } else {
                        Log.d("Inicio1", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                }
        }
    }

    // Busca dos dados de cada remédio
    private fun fetchDadosDoFirebase(remedioId: String) {
        val docRef = db.collection("remedios").document(remedioId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val tipo = document.getString("tipo")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    if (nome != null && tipo != null && descricao != null) {
                        val remedio = Tipo_Remedios(url ?: "", nome, tipo, descricao)
                        atualizarListaRemedios(remedio)
                    } else {
                        Log.d("Inicio1", "Nome, tipo ou descricao do remédio está nulo")
                    }
                } else {
                    Log.d("Inicio1", "Documento do remédio não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Inicio1", "Erro ao obter documento do remédio", e)
            }
    }

    // Atualização da lista de remédios e notificação para o adapter
    private fun atualizarListaRemedios(remedio: Tipo_Remedios) {
        listaInicio.add(remedio)
        adapterListanova.notifyDataSetChanged()
    }

    // Configuração da RecyclerView para doenças
    private fun setupRecyclerView2(view: View) {
        recyclerview_doenca.layoutManager = LinearLayoutManager(requireContext())
        recyclerview_doenca.setHasFixedSize(true)

        listadoenca = mutableListOf()
        adapterDoenca = AdapterDoenca(requireContext(), listadoenca)
        recyclerview_doenca.adapter = adapterDoenca
    }

    // Busca das doenças do usuário
    private fun fetchRemediosDoUsuario2() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val doencaIds = document.get("doenca") as? List<String>
                        if (doencaIds.isNullOrEmpty()) {
                            textview_naotemremedio.visibility = View.VISIBLE
                            recyclerview_doenca.visibility = View.GONE
                        } else {
                            textview_naotemremedio.visibility = View.GONE
                            recyclerview_doenca.visibility = View.VISIBLE
                            for (doencaId in doencaIds) {
                                fetchDadosDoFirebase2(doencaId)
                            }
                        }
                    } else {
                        Log.d("Inicio1", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                }
        }
    }

    // Busca dos dados de cada doença
    private fun fetchDadosDoFirebase2(doencaId: String) {
        val docRef = db.collection("doenca").document(doencaId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val tipo = document.getString("tipo")
                    val descricao = document.getString("descricao")
                    val url = document.getString("Url")

                    if (nome != null && tipo != null && descricao != null) {
                        val doenca = Tipo_Remedios(url ?: "", nome, tipo, descricao)
                        atualizarListaDoencas(doenca)
                    } else {
                        Log.d("Inicio1", "Nome, tipo ou descricao da doença está nulo")
                    }
                } else {
                    Log.d("Inicio1", "Documento da doença não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Inicio1", "Erro ao obter documento da doença", e)
            }
    }

    // Atualização da lista de doenças e notificação para o adapter
    private fun atualizarListaDoencas(doenca: Tipo_Remedios) {
        listadoenca.add(doenca)
        adapterDoenca.notifyDataSetChanged()
    }

    // Função para deletar remédios (implemente conforme necessário)

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private fun setupDiabetesInfo(view: View) {
        // Aqui estava o código do card de diabetes que foi removido
    }

    private fun deletarArrayRemedios() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.update("remedios", FieldValue.delete())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Lista de remédios deletado com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao deletar o lista de remédios: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Erro: UID do usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    // Método requestPermissions
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