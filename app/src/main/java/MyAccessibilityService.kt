package com.companyvihva.vihva


import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val source: AccessibilityNodeInfo? = event.source
        if (source != null) {
            // Chama a função para explorar o nó
            exploreNode(source)
        }
    }

    override fun onInterrupt() {
        // Método chamado quando o serviço é interrompido, pode ser deixado vazio ou usado para limpar recursos
    }

    // Função para explorar o nó de acessibilidade e extrair texto
    private fun exploreNode(node: AccessibilityNodeInfo) {
        if (node.childCount == 0) {
            // Verifica se o nó contém texto
            node.text?.let { text ->
                Log.d("MyAccessibilityService", "Texto encontrado: $text")
                // Aqui você pode adicionar lógica para ler ou processar o texto
                processText(text.toString())
            }
        } else {
            // Se o nó tiver filhos, explora cada filho
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let {
                    exploreNode(it)
                }
            }
        }
    }

    // Função para processar o texto encontrado
    private fun processText(text: String) {
        // Exemplo: apenas logar o texto
        Log.i("MyAccessibilityService", "Processando texto: $text")
        // Você pode adicionar mais lógica, como enviar o texto para um servidor, lê-lo em voz alta, etc.
    }
}
