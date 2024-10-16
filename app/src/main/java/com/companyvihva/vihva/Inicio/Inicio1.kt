package com.companyvihva.vihva.Inicio

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.telephony.SmsManager
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Alarme.ConfigFrequencia
import com.companyvihva.vihva.Configuracoes.Configuracoes
import com.companyvihva.vihva.Login.Login
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.AdicionarDoenca.AdicionarDoenca
import com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter.AdapterDoenca_inicio1
import com.companyvihva.vihva.model.Adapter.AdapterRemedio_inicio1
import com.companyvihva.vihva.model.Tipo_Remedios
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.local.Persistence
import kotlin.math.sqrt

class Inicio1 : Fragment(), SensorEventListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterListanova: AdapterRemedio_inicio1
    private lateinit var adapterDoenca: AdapterDoenca_inicio1
    private lateinit var listaInicio: MutableList<Tipo_Remedios>
    private lateinit var listadoenca: MutableList<Tipo_Remedios>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnSOS: Button
    private lateinit var recyclerview_doenca: RecyclerView
    private lateinit var recyclerViewRemedioAdicionado: RecyclerView
    private lateinit var textview_naotemremedio: TextView
    private lateinit var textview_naotemdoenca: TextView
    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var shakeThreshold = 50f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)

        // Verifique se o usuário está autenticado
        val User = FirebaseAuth.getInstance().currentUser
        if (User == null) {
            // Se não estiver autenticado, redirecione para a tela de login
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Fecha a activity atual se desejar
        }

        textview_naotemremedio = view.findViewById(R.id.textview_naotemremedio)
        recyclerViewRemedioAdicionado = view.findViewById(R.id.Recyclerview_remedioAdicionado)
        recyclerview_doenca = view.findViewById(R.id.recyclerview_doenca)
        textview_naotemdoenca = view.findViewById(R.id.textview_naotemdoenca)


        db = FirebaseFirestore.getInstance()
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Registra o listener do sensor
        acelerometro?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        btnSOS = view.findViewById(R.id.sos)
        btnSOS.setOnClickListener {
            requestPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            )
            enviarSOS()
        }

        val btnAddDoenca: ImageButton = view.findViewById(R.id.image_adddoenca)
        btnAddDoenca.setOnClickListener {
            val intent = Intent(requireContext(), AdicionarDoenca::class.java)
            startActivity(intent)
        }

        setupRecyclerView(view)
        setupRecyclerView2(view)

        fetchRemediosDoUsuario()
        fetchRemediosDoUsuario2()

        val imageLixeiraGlobal: ImageView = view.findViewById(R.id.image_lixeira_global)
        imageLixeiraGlobal.setOnClickListener {
            showConfirmDeleteDialog()
        }

        return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val (x, y, z) = it.values
                val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                if (gForce > shakeThreshold) {
                    enviarSOS()

                    AlertDialog.Builder(requireContext())
                        .setTitle("Mal súbito ou acidente detectado")
                        .setMessage("Detectamos uma queda e o SMS do SOS foi enviado")
                        .setPositiveButton("Ok", null)
                        .create()
                        .show()
                }
            }
        }
    }

    private fun enviarSOS() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val preferences = requireContext().getSharedPreferences(
                    Configuracoes.PREF_NAME, AppCompatActivity.MODE_PRIVATE
                )

                val savedMessage = preferences.getString(
                    Configuracoes.KEY_DEFAULT_MSG, "Mensagem padrão não definida"
                )
                val phone = preferences.getLong(Configuracoes.KEY_PHONE, -1L).toString()
                val smsMessage = "$savedMessage\nhttps://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"

                SmsManager.getDefault().sendTextMessage(
                    phone.toString(), null, smsMessage, null, null
                )

                Toast.makeText(
                    requireContext(),
                    "SMS enviado com a localização e a mensagem salva!",
                    Toast.LENGTH_LONG
                ).show()
            } ?: run {
                Toast.makeText(requireContext(), "Localização não disponível", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erro ao obter localização: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun showConfirmDeleteDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirmação de exclusão")
            setMessage("Tem certeza que deseja excluir todos os medicamentos? Você pode adicioná-los novamente na lista de remédios")
            setPositiveButton("Sim") { _, _ -> deletarArrayRemedios() }
            setNegativeButton("Não", null)
            create()
            show()
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerViewRemedioAdicionado.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerViewRemedioAdicionado.setHasFixedSize(true)

        listaInicio = mutableListOf()
        adapterListanova = AdapterRemedio_inicio1(requireContext(), listaInicio)
        recyclerViewRemedioAdicionado.adapter = adapterListanova
    }

    private fun fetchRemediosDoUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            db.collection("clientes").document(it).get()
                .addOnSuccessListener { document ->
                    document?.let { doc ->
                        val remediosId = doc.get("remedios") as? List<String>
                        if (remediosId.isNullOrEmpty()) {
                            textview_naotemremedio.visibility = View.VISIBLE
                            recyclerViewRemedioAdicionado.visibility = View.GONE
                        } else {
                            textview_naotemremedio.visibility = View.GONE
                            recyclerViewRemedioAdicionado.visibility = View.VISIBLE
                            remediosId.forEach { fetchDadosDoFirebase(it) }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                }
        }
    }

    private fun fetchDadosDoFirebase(remedioId: String) {
        db.collection("remedios").document(remedioId).get()
            .addOnSuccessListener { document ->
                document?.let { doc ->
                    val nome = doc.getString("nome")
                    val tipo = doc.getString("tipo")
                    val url = doc.getString("Url")

                    if (nome != null && tipo != null) {
                        val remedio = Tipo_Remedios(
                            foto = url ?: "", nome = nome, tipo = tipo, documentId = remedioId
                        )
                        atualizarListaRemedios(remedio)
                    }
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

    private fun setupRecyclerView2(view: View) {
        recyclerview_doenca.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerview_doenca.setHasFixedSize(true)

        listadoenca = mutableListOf()
        adapterDoenca = AdapterDoenca_inicio1(requireContext(), listadoenca)
        recyclerview_doenca.adapter = adapterDoenca
    }

    private fun fetchRemediosDoUsuario2() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            db.collection("clientes").document(it).get()
                .addOnSuccessListener { document ->
                    document?.let { doc ->
                        val doencasId = doc.get("doenca") as? List<String>
                        if (doencasId.isNullOrEmpty()) {
                            textview_naotemdoenca.visibility = View.VISIBLE
                            recyclerview_doenca.visibility = View.GONE
                        } else {
                            textview_naotemdoenca.visibility = View.GONE
                            recyclerview_doenca.visibility = View.VISIBLE
                            doencasId.forEach { fetchDadosDoFirebase2(it) }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao obter documento do cliente", e)
                }
        }
    }

    private fun fetchDadosDoFirebase2(doencaId: String) {
        db.collection("doenca").document(doencaId).get()
            .addOnSuccessListener { document ->
                document?.let { doc ->
                    val nome = doc.getString("nome")
                    val tipo = doc.getString("tipo")
                    val url = doc.getString("Url")

                    if (nome != null && tipo != null) {
                        val doenca = Tipo_Remedios(
                            foto = url ?: "", nome = nome, tipo = tipo, documentId = doencaId
                        )
                        atualizarListaDoenca(doenca)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Inicio1", "Erro ao obter documento da doença", e)
            }
    }

    private fun atualizarListaDoenca(doenca: Tipo_Remedios) {
        listadoenca.add(doenca)
        adapterDoenca.notifyDataSetChanged()
    }

    private fun deletarArrayRemedios() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            db.collection("clientes").document(it)
                .update("remedios", FieldValue.delete())
                .addOnSuccessListener {
                    listaInicio.clear()
                    adapterListanova.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Todos os remédios foram excluídos!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("Inicio1", "Erro ao deletar remédios", e)
                }
        }
    }

    private fun requestPermissions(vararg permissions: String) {
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    1
                )
            }
        }
    }
}
