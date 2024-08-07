package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.databinding.ItemLembreteBinding
import com.companyvihva.vihva.com.companyvihva.vihva.model.tipo_lembrete

class Adapter_lembrete(private var eventos: List<tipo_lembrete>) :
    RecyclerView.Adapter<Adapter_lembrete.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLembreteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLembreteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = eventos[position]
        holder.binding.titulo.text = evento.titulo
        holder.binding.data.text = evento.data
    }

    override fun getItemCount(): Int = eventos.size

    fun updateEventos(novosEventos: List<tipo_lembrete>) {
        eventos = novosEventos
        notifyDataSetChanged()
    }
}