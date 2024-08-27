import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.medico_spinner
import com.squareup.picasso.Picasso

class MedicoAdapter(context: Context, private val medicos: List<medico_spinner>) :
    ArrayAdapter<medico_spinner>(context, R.layout.spinner_item, medicos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.spinner_item, parent, false)

        val imageView = rowView.findViewById<ImageView>(R.id.medicoImageView)
        val textView = rowView.findViewById<TextView>(R.id.medicoTextView)

        val medico = medicos[position]
        textView.text = medico.nome

        // Verifica se a URL da imagem está vazia ou nula
        if (medico.fotoUrl.isNotEmpty()) {
            Picasso.get()
                .load(medico.fotoUrl)
                .placeholder(R.drawable.adicionarfoto) // Imagem de placeholder
                .error(R.drawable.adicionarfoto) // Imagem de erro
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.adicionarfoto) // Imagem padrão caso URL esteja vazia
        }

        return rowView
    }
}
