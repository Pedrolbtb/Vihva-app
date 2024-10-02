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
        val medico = medicos[position]

        // Verifica se é a opção "Nenhum médico selecionado"
        val layoutId = if (medico.nome == "Nenhum médico selecionado") {
            R.layout.spinner_item_semfoto // Layout para "Nenhum médico selecionado"
        } else {
            R.layout.spinner_item // Layout padrão para médicos
        }

        val rowView = inflater.inflate(layoutId, parent, false)

        // Preenche os dados apenas se não for "Nenhum médico selecionado"
        if (medico.nome != "Nenhum médico selecionado") {
            val imageView = rowView.findViewById<ImageView>(R.id.medicoImageView)
            val textView = rowView.findViewById<TextView>(R.id.medicoTextView)

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
        } else {
            // Caso seja "Nenhum médico selecionado", apenas define o texto
            val textView = rowView.findViewById<TextView>(R.id.medicoTextView)
            textView.text = medico.nome

            // Verifica se o ImageView existe antes de ocultá-lo
            val imageView = rowView.findViewById<ImageView>(R.id.medicoImageView)
            imageView?.visibility = View.GONE
        }

        return rowView
    }
}