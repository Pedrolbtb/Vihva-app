package com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.model.Remedio2

// AdapterRemedio é uma classe que estende RecyclerView.Adapter e é responsável por
// adaptar os dados da lista de remédios para serem exibidos em um RecyclerView.
class AdapterRemedio(private val context: Context, private val remedios: MutableList<Remedio2>) : RecyclerView.Adapter<AdapterRemedio.RemedioViewHolder>() {

    // RemedioViewHolder é uma classe interna que mantém referências às views de cada item
    // da lista de remédios.
    inner class RemedioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declaração das views dentro do item da lista
        val foto = itemView.findViewById<ImageView>(R.id.fotoremedio)
        val nome = itemView.findViewById<TextView>(R.id.nomeRemedio)
        val descricao = itemView.findViewById<TextView>(R.id.descriçãoRemedio)
    }

    // Este método é responsável por inflar o layout de cada item da lista e criar um novo
    // RemedioViewHolder para representar a visualização de um item na lista.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemedioViewHolder {
        val itemLista = LayoutInflater.from(context).inflate(R.layout.remedio_item, parent, false)
        val holder = RemedioViewHolder(itemLista)
        return holder
    }

    // Este método retorna o número total de itens na lista de remédios.
    override fun getItemCount(): Int = remedios.size

    // Este método é responsável por atualizar o conteúdo de cada item da lista com base na posição.
    override fun onBindViewHolder(holder: RemedioViewHolder, position: Int) {
        // Define a imagem do remédio no ImageView
        holder.foto.setImageResource(remedios[position].foto)
        // Define o nome do remédio no TextView
        holder.nome.text = remedios[position].nome
        // Define a descrição do remédio no TextView
        holder.descricao.text = remedios[position].descricao
    }
}
