package com.companyvihva.vihva.com.companyvihva.vihva.model.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.companyvihva.vihva.R

data class SolicitacaoAmizade(
    var id: String = "",
    var medicoId: String = "", // ID do médico
    val para: String = "", // ID do paciente
    val status: String = "",
    var nomeSolicitante: String? = "" // Nome do solicitante
)


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
        private val btnAceitar: Button = itemView.findViewById(R.id.btnAceitar)
        private val btnRejeitar: Button = itemView.findViewById(R.id.btnRejeitar)

        fun bind(solicitacao: SolicitacaoAmizade) {
            tvNomeSolicitante.text = solicitacao.nomeSolicitante // Exibe o nome do solicitante
            Log.d("SolicitacaoAmizadeAdapter", "Nome do solicitante: ${solicitacao.nomeSolicitante}, Médico ID: ${solicitacao.medicoId}")
            btnAceitar.setOnClickListener {
                onActionClicked(solicitacao, true)
            }
            btnRejeitar.setOnClickListener {
                onActionClicked(solicitacao, false)
            }
        }
    }
}
