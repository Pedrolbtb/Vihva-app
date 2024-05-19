import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.companyvihva.vihva.R


class Inicio1 : Fragment() {

    ///////socorro///////////
    private lateinit var spinnerDDI: Spinner
    private lateinit var editTextPhone: EditText
    private lateinit var editTextMessage: EditText
    ///////socorro/////////////



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View? {
        // Declare a variável Dialog
        val myDialog: Dialog

        // Inicialize a variável Dialog
        myDialog = Dialog(requireContext())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inicio1, container, false)

        // Encontra o botão de imagem
        val card_diabete = view.findViewById<View>(R.id.card_diabete)

        card_diabete.setOnClickListener {
            myDialog.setContentView(R.layout.popup_descricao)
            myDialog.show()
        }




        // Retorna a view inflada
        return view


    }////fim do onCreate



}
