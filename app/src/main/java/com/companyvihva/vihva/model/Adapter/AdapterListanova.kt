package com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Remedios
import com.squareup.picasso.Picasso

class AdapterListanova(
    private val context: Context,
    private val listas: MutableList<Tipo_Remedios>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterListanova.ListaViewHolder>() {

    // Interface para lidar com cliques nos itens do RecyclerView
    interface OnItemClickListener {
        fun onItemClick(remedioId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.hadreeeee, parent, false)
        return ListaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val currentItem = listas[position]
        Picasso.get().load(currentItem.foto).into(holder.foto)
        holder.nome.text = currentItem.nome
        holder.tipo.text = currentItem.tipo

        holder.bind(currentItem.documentId, itemClickListener) // Passa itemClickListener para o bind
    }


    override fun getItemCount(): Int = listas.size

    inner class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: ImageView = itemView.findViewById(R.id.fotoitem)
        val nome: TextView = itemView.findViewById(R.id.nomeitem)
        val tipo: TextView = itemView.findViewById(R.id.tipoitem)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val remedioId = listas[position].documentId
                    itemClickListener.onItemClick(remedioId)
                }
            }
        }

        fun bind(remedioId: String, itemClickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(remedioId)
            }
        }
    }
}
