package com.gesturex.util

import com.gesturex.data.model.Gesture
import com.gesturex.data.model.TipoGesto
import kotlin.math.abs

class GestureDetectorUtil {
    private val janela = ArrayDeque<Leitura>(30)
    private var ultimaDeteccao = 0L
    private val COOLDOWN = 1800L
    var limiar = 9f

    fun processar(x: Float, y: Float, z: Float, gestos: List<Gesture>): Gesture? {
        if (janela.size >= 30) janela.removeFirst()
        janela.addLast(Leitura(x, y, z))
        if (janela.size < 15) return null
        val agora = System.currentTimeMillis()
        if (agora - ultimaDeteccao < COOLDOWN) return null
        val tipo = detectar() ?: return null
        val gesto = gestos.firstOrNull { it.tipoGesto == tipo } ?: return null
        ultimaDeteccao = agora
        return gesto
    }

    private fun detectar(): String? {
        val lista = janela.toList()
        val maxY = lista.maxOf { it.y }; val minY = lista.minOf { it.y }
        val maxX = lista.maxOf { it.x }; val minX = lista.minOf { it.x }
        val maxZ = lista.maxOf { it.z }; val minZ = lista.minOf { it.z }
        val dY = maxY - minY; val dX = maxX - minX; val dZ = maxZ - minZ
        var invX = 0
        for (i in 1 until lista.size) if (lista[i].x * lista[i-1].x < 0) invX++
        return when {
            invX >= 4 && dX > limiar               -> TipoGesto.AGITAR
            dY > limiar && abs(maxY) > abs(minY)   -> TipoGesto.GIRO_CIMA
            dY > limiar && abs(minY) > abs(maxY)   -> TipoGesto.GIRO_BAIXO
            dX > limiar && abs(minX) > abs(maxX)   -> TipoGesto.INCLINAR_ESQ
            dX > limiar && abs(maxX) > abs(minX)   -> TipoGesto.INCLINAR_DIR
            dZ > limiar                             -> TipoGesto.VIRAR_BAIXO
            else                                   -> null
        }
    }

    fun setSensibilidade(nivel: String) {
        limiar = when (nivel) { "alta" -> 6f; "baixa" -> 13f; else -> 9f }
    }
}
