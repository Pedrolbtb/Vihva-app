package com.companyvihva.vihva.com.companyvihva.vihva.model

import java.util.Date

data class tipo_lembrete(
    val titulo: String,
    val data: String,      // Data formatada para exibição
    val dataDate: Date    // Data em formato Date para ordenação
)