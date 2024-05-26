import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Listanew
import com.squareup.picasso.Picasso

class AdapterLista(private val context: Context, private val listas: MutableList<Listanew>) :
    RecyclerView.Adapter<AdapterLista.ListaViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewholder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.hadreeeee, parent, false)
        return ListaViewholder(itemView)
    }

    override fun onBindViewHolder(holder: ListaViewholder, position: Int) {
        val currentItem = listas[position]
        Picasso.get().load(currentItem.foto).into(holder.foto)
        holder.nome.text = currentItem.nome
    }

    override fun getItemCount(): Int = listas.size

    inner class ListaViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: ImageView = itemView.findViewById(R.id.fotoitem)
        val nome: TextView = itemView.findViewById(R.id.nomeitem)
    }
}
