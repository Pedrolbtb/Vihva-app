package com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Tipo_Classe
import com.squareup.picasso.Picasso

class AdapterRemedio(
    private val context: Context,
    private val remedios: MutableList<Tipo_Classe>,
    private val onItemClickListener: (Tipo_Classe) -> Unit
) : RecyclerView.Adapter<AdapterRemedio.RemedioViewHolder>() {

    // Classe interna RemedioViewHolder que mantém referências às views de cada item da lista de remédios.
    inner class RemedioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declaração das views dentro do item da lista
        val foto = itemView.findViewById<ImageView>(R.id.fotoremedio)
        val nome = itemView.findViewById<TextView>(R.id.nomeRemedio)

        // Método bind para atribuir os valores do Remedio2 aos componentes da view
        fun bind(remedio: Tipo_Classe) {
            // Carrega a imagem do remédio no ImageView usando Picasso
            Picasso.get().load(remedio.foto).into(foto)
            // Define o nome do remédio no TextView
            nome.text = remedio.nome

            // Define o clique no item da lista
            itemView.setOnClickListener {
                onItemClickListener(remedio)
            }
        }
    }

    // Método onCreateViewHolder é responsável por inflar o layout de cada item da lista e criar um novo RemedioViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemedioViewHolder {
        val itemLista = LayoutInflater.from(context).inflate(R.layout.fragment_remedio, parent, false)
        return RemedioViewHolder(itemLista)
    }

    // Método getItemCount retorna o número total de itens na lista de remédios
    override fun getItemCount(): Int = remedios.size

    // Método onBindViewHolder é responsável por atualizar o conteúdo de cada item da lista com base na posição
    override fun onBindViewHolder(holder: RemedioViewHolder, position: Int) {
        // Carrega a imagem do remédio e define o nome
        holder.bind(remedios[position])
    }
}
