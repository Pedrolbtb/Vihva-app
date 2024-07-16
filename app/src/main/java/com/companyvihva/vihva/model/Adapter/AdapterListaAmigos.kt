package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Inicio.Perfil_medico.Perfil_medico
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
        // Verifica se a URL da imagem não está vazia antes de carregá-la
        if (!currentItem.fotoAmigo.isNullOrEmpty()) {
            Picasso.get().load(currentItem.fotoAmigo).into(holder.fotoAmigo)
        } else {
            // Define uma imagem padrão se a URL estiver vazia
            holder.fotoAmigo.setImageResource(R.drawable.adicionarfoto)
        }
        // Define o nome do item
        holder.nome.text = currentItem.nome
    }

    inner class AmigosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoAmigo: ImageView = itemView.findViewById(R.id.foto_amigo)
        val nome: TextView = itemView.findViewById(R.id.nome_amigo)

        init {
            // Define o clique no item da lista
            itemView.setOnClickListener {
                // Ao clicar em um item da lista, abra a Activity de descrição correspondente
                val intent = Intent(context, Perfil_medico::class.java).apply {
                    // Aqui você passa o ID do amigo usando o mesmo nome de extra
                    putExtra("amigoId", listaAmizades[adapterPosition].documentId)
                }
                context.startActivity(intent)
            }
        }
    }
}