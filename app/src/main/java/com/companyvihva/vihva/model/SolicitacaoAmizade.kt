package com.companyvihva.vihva.com.companyvihva.vihva.model

data class SolicitacaoAmizade(
    var id: String = "",
    var nomeSolicitante: String = "",
    var sobrenomeSolicitante: String = "", // Adicione este campo
    var fotoSolicitante: String = "",
    var medicoId: String = "",
    var para: String = "",
    var status: String = ""
)