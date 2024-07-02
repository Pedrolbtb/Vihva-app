package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Amigos
import com.squareup.picasso.Picasso

class AdapterListaAmigos(
    private val context: Context,
    private val listaAmizades: MutableList<Amigos>
) : RecyclerView.Adapter<AdapterListaAmigos.AmigosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigosViewHolder {
        val itemListaAmigos = LayoutInflater.from(context).inflate(R.layout.item_lista_amigos, parent, false)
        return AmigosViewHolder(itemListaAmigos)
    }

    override fun getItemCount(): Int = listaAmizades.size

    override fun onBindViewHolder(holder: AmigosViewHolder, position: Int) {
        val currentItem = listaAmizades[position]
        // Carrega a imagem do item usando Picasso
        Picasso.get().load(currentItem.fotoAmigo).into(holder.fotoAmigo)
        // Define o nome do item
        holder.nome.text = currentItem.nome
    }

    inner class AmigosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoAmigo: ImageView = itemView.findViewById(R.id.foto_amigo)
        val nome: TextView = itemView.findViewById(R.id.nome_amigo)

        init {
            //
        }
    }
}
