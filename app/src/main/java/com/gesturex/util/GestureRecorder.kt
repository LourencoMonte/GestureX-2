package com.gesturex.util

import com.gesturex.data.model.TipoGesto
import kotlin.math.abs
import kotlin.math.sqrt

data class Leitura(val x: Float, val y: Float, val z: Float)

class GestureRecorder {
    private val leituras = mutableListOf<Leitura>()
    private val leiturasGyro = mutableListOf<LeituraGyro>()

    fun iniciar() {
        leituras.clear()
        leiturasGyro.clear()
    }

    fun adicionar(x: Float, y: Float, z: Float) {
        leituras.add(Leitura(x, y, z))
    }

    fun adicionarGyro(gx: Float, gy: Float, gz: Float) {
        leiturasGyro.add(LeituraGyro(gx, gy, gz))
    }

    fun finalizar(): List<Leitura> = leituras.toList()

    fun detectarTipo(dados: List<Leitura>, limiar: Float = 9f): String {
        if (dados.size < 5) return TipoGesto.PERSONALIZADO
        val maxY = dados.maxOf { it.y }; val minY = dados.minOf { it.y }
        val maxX = dados.maxOf { it.x }; val minX = dados.minOf { it.x }
        val maxZ = dados.maxOf { it.z }; val minZ = dados.minOf { it.z }
        val dY = maxY - minY; val dX = maxX - minX; val dZ = maxZ - minZ
        var invX = 0
        for (i in 1 until dados.size) if (dados[i].x * dados[i-1].x < 0) invX++

        // Fusão com giroscópio: verifica se houve rotação significativa
        val gyroConfirma = verificarGyro()

        return when {
            invX >= 4 && dX > limiar               -> TipoGesto.AGITAR
            dY > limiar && abs(maxY) > abs(minY) && gyroConfirma -> TipoGesto.GIRO_CIMA
            dY > limiar && abs(minY) > abs(maxY) && gyroConfirma -> TipoGesto.GIRO_BAIXO
            dX > limiar && abs(minX) > abs(maxX)   -> TipoGesto.INCLINAR_ESQ
            dX > limiar && abs(maxX) > abs(minX)   -> TipoGesto.INCLINAR_DIR
            dZ > limiar                             -> TipoGesto.VIRAR_BAIXO
            dY > limiar && abs(maxY) > abs(minY)   -> TipoGesto.GIRO_CIMA
            dY > limiar && abs(minY) > abs(maxY)   -> TipoGesto.GIRO_BAIXO
            else                                   -> TipoGesto.PERSONALIZADO
        }
    }

    private fun verificarGyro(): Boolean {
        if (leiturasGyro.size < 3) return true // sem dados, aceita por fallback
        val maxMag = leiturasGyro.maxOf { sqrt(it.gx * it.gx + it.gy * it.gy + it.gz * it.gz) }
        return maxMag > 1.5f
    }
}
