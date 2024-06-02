package com.companyvihva.vihva

import android.app.Application
import com.companyvihva.vihva.model.OnRemedioSelectedListener
import com.companyvihva.vihva.model.Tipo_Classe

class MyApplication : Application(), OnRemedioSelectedListener {

    private var remedioListener: OnRemedioSelectedListener? = null

    override fun onCreate() {
        super.onCreate()
        // Aqui você pode inicializar qualquer coisa necessária para o seu aplicativo
    }

    override fun onRemedioSelected(remedio: Tipo_Classe) {
        remedioListener?.onRemedioSelected(remedio)
    }

    fun setOnRemedioSelectedListener(listener: OnRemedioSelectedListener) {
        this.remedioListener = listener
    }

    fun clearRemedioSelectedListener() {
        this.remedioListener = null
    }
}
