package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R
import com.companyvihva.vihva.com.companyvihva.vihva.model.SolicitacaoAmizade
import com.squareup.picasso.Picasso

class SolicitacaoAmizadeAdapter(
    private val onActionClicked: (SolicitacaoAmizade, Boolean) -> Unit
) : RecyclerView.Adapter<SolicitacaoAmizadeAdapter.ViewHolder>() {

    private var solicitacoes: List<SolicitacaoAmizade> = emptyList()

    fun submitList(newList: List<SolicitacaoAmizade>) {
        solicitacoes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_solicitacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val solicitacao = solicitacoes[position]
        holder.bind(solicitacao)
    }

    override fun getItemCount(): Int = solicitacoes.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNomeSolicitante: TextView = itemView.findViewById(R.id.tvNomeSolicitante)
        private val tvSobrenomeSolicitante: TextView = itemView.findViewById(R.id.tvSobrenomeSolicitante) // Novo campo para sobrenome
        private val imgSolicitante: ImageView = itemView.findViewById(R.id.imgSolicitante)
        private val btnAceitar: Button = itemView.findViewById(R.id.btnAceitar)
        private val btnRejeitar: Button = itemView.findViewById(R.id.btnRejeitar)

        fun bind(solicitacao: SolicitacaoAmizade) {
            // Atualize o nome e sobrenome do solicitante
            tvNomeSolicitante.text = solicitacao.nomeSolicitante
            tvSobrenomeSolicitante.text = solicitacao.sobrenomeSolicitante // Exibe o sobrenome

            Log.d("SolicitacaoAmizadeAdapter", "Nome do solicitante: ${solicitacao.nomeSolicitante}, Sobrenome: ${solicitacao.sobrenomeSolicitante}, Médico ID: ${solicitacao.medicoId}")

            // Carrega a imagem do solicitante usando Picasso
            val fotoUrl = solicitacao.fotoSolicitante ?: ""
            if (fotoUrl.isNotEmpty()) {
                Picasso.get().load(fotoUrl).into(imgSolicitante)
            } else {
                imgSolicitante.setImageResource(R.drawable.adicionarfoto) // Imagem padrão se não houver foto
            }

            btnAceitar.setOnClickListener {
                onActionClicked(solicitacao, true)
            }
            btnRejeitar.setOnClickListener {
                onActionClicked(solicitacao, false)
            }
        }
    }
}
