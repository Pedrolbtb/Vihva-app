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
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Alarme.CriaAlarme
import com.companyvihva.vihva.Configurações.Configuracoes
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca.AdicionarDoenca
import com.companyvihva.vihva.model.Adapter.AdapterListanova
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class Inicio1 : Fragment(), AdapterListanova.OnItemClickListener {


    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListanova: AdapterListanova
    private lateinit var listaInicio: MutableList<Tipo_Remedios>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnSOS: Button
    private lateinit var recyclerViewRemedioAdicionado: RecyclerView
    private lateinit var textview_naotemremedio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)
        textview_naotemremedio = view.findViewById(R.id.textview_naotemremedio)
        recyclerViewRemedioAdicionado = view.findViewById(R.id.Recyclerview_remedioAdicionado)


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

        val btnAddDoenca: ImageButton = view.findViewById(R.id.image_adddoenca)
        btnAddDoenca.setOnClickListener {
            val telaAddDoenca = Intent(requireContext(), AdicionarDoenca::class.java)
            startActivity(telaAddDoenca)
        }

        setupRecyclerView(view)
        fetchRemediosDoUsuario()
        setupDiabetesInfo(view)

        val imageLixeiraGlobal: ImageView = view.findViewById(R.id.image_lixeira_global)
        imageLixeiraGlobal.setOnClickListener {
            deletarArrayRemedios()
        }

        val card_diabete = view.findViewById<View>(R.id.card_diabete).setOnClickListener {
            mostrarPopup()
        }

        return view
    }


    private fun setupRecyclerView(view: View) {
        recyclerViewRemedioAdicionado = view.findViewById(R.id.Recyclerview_remedioAdicionado)
        recyclerViewRemedioAdicionado.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRemedioAdicionado.setHasFixedSize(true)

        listaInicio = mutableListOf()
        adapterListanova = AdapterListanova(requireContext(), listaInicio, this)
        recyclerViewRemedioAdicionado.adapter = adapterListanova
    }

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
                    val inflater = LayoutInflater.from(requireContext())
                    val popupView = inflater.inflate(R.layout.popup_descricao, null)

                    // Encontra elementos no layout
                    val nomeTextView: TextView = popupView.findViewById(R.id.diabetes)
                    val descricaoTextView: TextView = popupView.findViewById(R.id.descricao)
                    val imageView1: ImageView = popupView.findViewById(R.id.foto_diabete1)
                    val imageView2: ImageView = popupView.findViewById(R.id.foto_diabete2)
                    val textViewAviso: TextView = popupView.findViewById(R.id.textViewAviso)

                    // Aplica cor vermelha na primeira letra do aviso
                    val avisoText = textViewAviso.text.toString()
                    val spannableAviso = SpannableString(avisoText)
                    val redColor = ContextCompat.getColor(requireContext(), R.color.vermelho_alerta)
                    spannableAviso.setSpan(
                        ForegroundColorSpan(redColor),
                        0,
                        6,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textViewAviso.text = spannableAviso

                    // Define dados nos TextViews
                    nomeTextView.text = nome
                    descricaoTextView.text = descricao

                    Picasso.get().load(imageUrl1).into(imageView1)

                    if (!imageUrl2.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl2).into(imageView2)
                    }

                    // Mostra o popup
                    val popupWindow = AlertDialog.Builder(requireContext())
                        .setView(popupView)
                        .create()
                    popupWindow.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    popupWindow.show()

                    val btnClose: AppCompatImageButton = popupView.findViewById(R.id.close_button)
                    btnClose.setOnClickListener {
                        // Fecha o alertDialog
                        popupWindow.dismiss()
                    }
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

    fun mostrarPopupRemedio(remedioId: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val clientRef = db.collection("clientes").document(uid)
            clientRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Obtém a lista de remédios como uma List<Map<String, Any>>
                        val remedios = document.get("remedios") as? List<*>
                        if (!remedios.isNullOrEmpty()) {
                            // Procura o remédio pelo ID
                            val remedio = remedios.find { (it as? Map<String, Any>)?.get("nome") == remedioId } as? Map<String,Any>
                            if (remedio != null) {
                                val nome = remedio["nome"]
                                val descricao = remedio["descricao"]
                                val imageUrl1 = remedio["Url"] as? String

                                Log.d("PopupRemedio", "Nome: $nome, Descrição: $descricao, URL: $imageUrl1")

                                // Infla o layout do popup
                                val inflater = LayoutInflater.from(requireContext())
                                val popupView = inflater.inflate(R.layout.popup_remedio, null)

                                // Encontra elementos no layout do popup
                                val nomeTextView: TextView = popupView.findViewById(R.id.nomere)
                                val descricaoTextView: TextView = popupView.findViewById(R.id.descricao1)
                                val imageView1: ImageView = popupView.findViewById(R.id.foto_Remedio)
                                val textViewAviso: TextView = popupView.findViewById(R.id.textViewAviso)

                                // Aplica cor vermelha na primeira letra do aviso
                                val avisoText = textViewAviso.text.toString()
                                val spannableAviso = SpannableString(avisoText)
                                val redColor = ContextCompat.getColor(requireContext(), R.color.vermelho_alerta)
                                spannableAviso.setSpan(
                                    ForegroundColorSpan(redColor),
                                    0,
                                    6,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                textViewAviso.text = spannableAviso

                                // Define dados nos TextViews
                                nomeTextView.text = nome as? String
                                descricaoTextView.text = descricao as? String

                                // Carrega imagem usando Picasso, se a URL não estiver vazia
                                if (!imageUrl1.isNullOrEmpty()) {
                                    Picasso.get().load(imageUrl1).into(imageView1)
                                }

                                // Mostra o popup utilizando AlertDialog
                                val popupWindow = AlertDialog.Builder(requireContext())
                                    .setView(popupView)
                                    .create()

                                // Define o fundo do AlertDialog como transparente
                                popupWindow.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                // Mostra o popup
                                popupWindow.show()

                                // Configura o botão de fechar
                                val btnClose: AppCompatImageButton = popupView.findViewById(R.id.close_button)
                                btnClose.setOnClickListener {
                                    // Fecha o alertDialog
                                    popupWindow.dismiss()
                                }
                            } else {
                                Log.d("Inicio1", "Remédio com ID '$remedioId' não encontrado")
                            }
                        } else {
                            Log.d("Inicio1", "Lista de remédios está vazia ou não encontrada")
                        }
                    } else {
                        Log.d("Inicio1", "Documento do cliente não encontrado")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Inicio1", "Erro ao obter documento do cliente", exception)
                }
        } else {
            Log.d("Inicio1", "Usuário não autenticado")
        }
    }

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
                        // Trate o caso em que o documento do cliente não existe
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                    // Trate o erro ao obter o documento do cliente
                }
        }
    }

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

    private fun atualizarListaRemedios(remedio: Tipo_Remedios) {
        listaInicio.add(remedio)
        adapterListanova.notifyDataSetChanged()
    }

    override fun onItemClick(remedioId: String) {
        mostrarPopupRemedio(remedioId)
    }

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
