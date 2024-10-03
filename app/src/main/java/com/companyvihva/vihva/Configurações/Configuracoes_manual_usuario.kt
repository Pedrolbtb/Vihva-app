package com.companyvihva.vihva.Configurações

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.VideoView
import com.companyvihva.vihva.R

class Configuracoes_manual_usuario : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_manual_usuario)

        // Referências para os componentes do layout
        val videoView = findViewById<VideoView>(R.id.videoViewDoenca)
        val playButton = findViewById<Button>(R.id.playButtonDoenca)

        // Definir o caminho do vídeo (substitua pela URI do seu vídeo)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.doencapreta
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        // Reproduzir o vídeo ao clicar no botão
        playButton.setOnClickListener {
            videoView.start()
        }

        // Parar o vídeo se ele terminar
        videoView.setOnCompletionListener {
            videoView.seekTo(0)
        }

        // Listener para fechar a activity ao clicar no botão de voltar
        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener {
            finish()
        }
    }
}
