package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.PopupRemedio.DescriçãoDoença
import com.companyvihva.vihva.model.PopupRemedio.DescriçãoRemedio
import com.companyvihva.vihva.model.Tipo_Remedios
import com.squareup.picasso.Picasso

class AdapterDoenca(private val context: Context, private val listas: MutableList<Tipo_Remedios>) :
    RecyclerView.Adapter<AdapterDoenca.ListaViewHolder>() {

    // Método onCreateViewHolder é responsável por inflar o layout de cada item da lista e criar um novo ListaViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.adicionar_doenca, parent, false)
        return ListaViewHolder(itemView)
    }

    // Método onBindViewHolder é responsável por atualizar o conteúdo de cada item da lista com base na posição
    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val currentItem = listas[position]
        // Carrega a imagem do item usando Picasso
        Picasso.get().load(currentItem.foto).into(holder.foto)
        // Define o nome e o tipo do item
        holder.nome.text = currentItem.nome
        holder.tipo.text = currentItem.tipo
    }

    // Método getItemCount retorna o número total de itens na lista
    override fun getItemCount(): Int = listas.size

    // Classe interna ListaViewHolder que mantém referências às views de cada item da lista
    inner class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: ImageView = itemView.findViewById(R.id.fotoitem)
        val nome: TextView = itemView.findViewById(R.id.nomeitem)
        val tipo: TextView = itemView.findViewById(R.id.tipoitem)

        init {
            // Define o clique no item da lista
            itemView.setOnClickListener {
                // Ao clicar em um item da lista, abra a Activity de pop-up correspondente
                val intent = Intent(context, DescriçãoDoença::class.java).apply {
                    putExtra("doencaId", listas[adapterPosition].documentId) // Alterado para acessar o ID do documento
                }
                context.startActivity(intent)
            }
        }
    }
}