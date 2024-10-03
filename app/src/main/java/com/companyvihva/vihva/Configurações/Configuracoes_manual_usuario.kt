package com.companyvihva.vihva.Configurações

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class Configuracoes_manual_usuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.companyvihva.vihva.R.layout.activity_configuracoes_manual_usuario)

        // Referências para os componentes do layout
        val videoView = findViewById<VideoView>(com.companyvihva.vihva.R.id.videoViewDoenca)
        val playButton = findViewById<Button>(com.companyvihva.vihva.R.id.playButtonDoenca)
        val videoViewDois = findViewById<VideoView>(com.companyvihva.vihva.R.id.videoViewRemedio)
        val playButtonDois = findViewById<Button>(com.companyvihva.vihva.R.id.playButtonRemedio)
        val videoViewTres = findViewById<VideoView>(com.companyvihva.vihva.R.id.videoViewExclui)
        val playButtonTres = findViewById<Button>(com.companyvihva.vihva.R.id.playButtonExclui)
        val videoViewQuatro = findViewById<VideoView>(com.companyvihva.vihva.R.id.videoViewEdita)
        val playButtonQuatro = findViewById<Button>(com.companyvihva.vihva.R.id.playButtonEdita)
        val videoViewCinco = findViewById<VideoView>(com.companyvihva.vihva.R.id.videoViewSOS)
        val playButtonCinco = findViewById<Button>(com.companyvihva.vihva.R.id.playButtonSOS)

        // Definir os caminhos dos vídeos
        val videoPath = "android.resource://${packageName}/${com.companyvihva.vihva.R.raw.doencapreta}"
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        val videoPathDois = "android.resource://${packageName}/${com.companyvihva.vihva.R.raw.remediopreta}"
        val uriDois = Uri.parse(videoPathDois)
        videoViewDois.setVideoURI(uriDois)

        val videoPathTres = "android.resource://${packageName}/${com.companyvihva.vihva.R.raw.excluircoisaspreta}"
        val uriTres = Uri.parse(videoPathTres)
        videoViewTres.setVideoURI(uriTres)

        val videoPathQuatro = "android.resource://${packageName}/${com.companyvihva.vihva.R.raw.editarpreta}"
        val uriQuatro = Uri.parse(videoPathQuatro)
        videoViewQuatro.setVideoURI(uriQuatro)

        val videoPathCinco = "android.resource://${packageName}/${com.companyvihva.vihva.R.raw.sospreta}"
        val uriCinco = Uri.parse(videoPathCinco)
        videoViewCinco.setVideoURI(uriCinco)

        // Reproduzir o vídeo ao clicar no botão do primeiro vídeo
        playButton.setOnClickListener {
            videoView.start()
        }

        // Reproduzir o vídeo ao clicar no botão do segundo vídeo
        playButtonDois.setOnClickListener {
            videoViewDois.start()
        }

        // Reproduzir o vídeo ao clicar no botão do terceiro vídeo
        playButtonTres.setOnClickListener {
            videoViewTres.start()
        }

        // Reproduzir o vídeo ao clicar no botão do quarto vídeo
        playButtonQuatro.setOnClickListener {
            videoViewQuatro.start()
        }

        // Reproduzir o vídeo ao clicar no botão do quinto vídeo
        playButtonCinco.setOnClickListener {
            videoViewCinco.start()
        }

        // Parar o vídeo se ele terminar
        videoView.setOnCompletionListener {
            videoView.seekTo(0)
        }
        videoViewDois.setOnCompletionListener {
            videoViewDois.seekTo(0)
        }
        videoViewTres.setOnCompletionListener {
            videoViewTres.seekTo(0)
        }
        videoViewQuatro.setOnCompletionListener {
            videoViewQuatro.seekTo(0)
        }
        videoViewCinco.setOnCompletionListener {
            videoViewCinco.seekTo(0)
        }

        // Listener para fechar a activity ao clicar no botão de voltar
        val btnClose = findViewById<ImageButton>(com.companyvihva.vihva.R.id.btnClose)
        btnClose.setOnClickListener {
            finish()
        }
    }
}
