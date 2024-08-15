package com.companyvihva.vihva

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_alarme

import com.companyvihva.vihva.model.Descrição_alarme

class Adapter_alarme(
    private val listaAlarmes: List<tipo_alarme>,
    private val context: Context
) : RecyclerView.Adapter<Adapter_alarme.TipoAlarmeViewHolder>() {

    inner class TipoAlarmeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDescricao: TextView = itemView.findViewById(R.id.descricao)

        init {
            itemView.setOnClickListener {
                val descricao = listaAlarmes[adapterPosition].descricao
                val intent = Intent(context, Descrição_alarme::class.java).apply {
                    putExtra("ALARME_DESCRICAO", descricao)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoAlarmeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarme, parent, false)
        return TipoAlarmeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipoAlarmeViewHolder, position: Int) {
        val alarme = listaAlarmes[position]
        holder.textViewDescricao.text = alarme.descricao
    }

    override fun getItemCount(): Int {
        return listaAlarmes.size
    }
}
