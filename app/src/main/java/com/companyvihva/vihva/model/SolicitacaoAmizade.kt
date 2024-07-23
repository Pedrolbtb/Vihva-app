package com.companyvihva.vihva.com.companyvihva.vihva.model

data class SolicitacaoAmizade(
    var id: String = "",
    var medicoId: String = "", // ID do médico
    val para: String = "", // ID do paciente
    val status: String = "",
    var nomeSolicitante: String? = "" // Nome do solicitante
)