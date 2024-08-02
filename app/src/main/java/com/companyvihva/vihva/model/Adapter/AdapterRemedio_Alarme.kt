package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.Alarme.CriaAlarme
import com.companyvihva.vihva.Alarme.EscolhaFrequencia
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.Remedios_alarme
import com.companyvihva.vihva.model.PopupRemedio.DescriçãoDoença
import com.squareup.picasso.Picasso

class AdapterRemedio_Alarme(
    private val context: Context, private val listasRemedioAlarme: MutableList<Remedios_alarme>
) : RecyclerView.Adapter<AdapterRemedio_Alarme.RemedioAlarmeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemedioAlarmeViewHolder {
        val itemListaAlarme =
            LayoutInflater.from(context).inflate(R.layout.hadreeeee, parent, false)
        return RemedioAlarmeViewHolder(itemListaAlarme)
    }

    override fun getItemCount(): Int = listasRemedioAlarme.size

    override fun onBindViewHolder(holder: RemedioAlarmeViewHolder, position: Int) {
        val currentItem = listasRemedioAlarme[position]
        // Carrega a imagem do item usando Picasso
        Picasso.get().load(currentItem.fotoRemedio).into(holder.fotoRemedio)
        // Define o nome e o tipo do item
        holder.nome.text = currentItem.nome
        holder.tipo.text = currentItem.tipo
    }

    inner class RemedioAlarmeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoRemedio: ImageView = itemView.findViewById(R.id.fotoitem)
        val nome: TextView = itemView.findViewById(R.id.nomeitem)
        val tipo: TextView = itemView.findViewById(R.id.tipoitem)

        init {
            // Define o clique no item da lista
            itemView.setOnClickListener {
                // Ao clicar em um item da lista, abra a Activity de pop-up correspondente
                val intent = Intent(context, EscolhaFrequencia::class.java).apply {
                    putExtra("remedioId", listasRemedioAlarme[adapterPosition].documentId)
                }
                context.startActivity(intent)
            }
        }
    }
}
