import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.PopupRemedio
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Listanew
import com.squareup.picasso.Picasso

class AdapterLista(private val context: Context, private val listas: MutableList<Listanew>) :
    RecyclerView.Adapter<AdapterLista.ListaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.hadreeeee, parent, false)
        return ListaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val currentItem = listas[position]
        Picasso.get().load(currentItem.foto).into(holder.foto)
        holder.nome.text = currentItem.nome
        holder.tipo.text = currentItem.tipo
    }

    override fun getItemCount(): Int = listas.size

    inner class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: ImageView = itemView.findViewById(R.id.fotoitem)
        val nome: TextView = itemView.findViewById(R.id.nomeitem)
        val tipo: TextView = itemView.findViewById(R.id.tipoitem)

        init {
            itemView.setOnClickListener {
                // Ao clicar em um item da lista, abra a Activity de pop-up correspondente
                val intent = Intent(context, PopupRemedio::class.java).apply {
                    putExtra("remedioId", listas[adapterPosition].documentId) // Alterado para acessar o ID do documento
                }
                context.startActivity(intent)
            }
        }
    }
}