package com.gesturex.util

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.widget.Toast
import com.gesturex.data.model.AcaoGesto
import com.gesturex.data.model.Gesture

class ActionDispatcher(private val ctx: Context) {
    private var lanternaAtiva = false

    fun executar(g: Gesture) {
        when (g.acaoDestino) {
            AcaoGesto.ABRIR_APP  -> abrirApp(g.appDestino, g.appNome)
            AcaoGesto.LANTERNA   -> lanterna()
            AcaoGesto.SILENCIAR  -> silenciar()
            AcaoGesto.LIGAR_PARA -> ligar(g.contatoNumero, g.contatoNome)
        }
    }

    private fun abrirApp(pkg: String, nome: String) {
        val intent = ctx.packageManager.getLaunchIntentForPackage(pkg)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        } else {
            toast("App $nome não encontrado")
        }
    }

    private fun lanterna() {
        val cm = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            lanternaAtiva = !lanternaAtiva
            cm.setTorchMode(cm.cameraIdList[0], lanternaAtiva)
        } catch (e: Exception) { toast("Erro ao acionar lanterna") }
    }

    private fun silenciar() {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = if (am.ringerMode == AudioManager.RINGER_MODE_SILENT)
            AudioManager.RINGER_MODE_NORMAL else AudioManager.RINGER_MODE_SILENT
    }

    private fun ligar(numero: String, nome: String) {
        if (numero.isBlank()) { toast("Número não configurado"); return }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    private fun toast(msg: String) = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}
