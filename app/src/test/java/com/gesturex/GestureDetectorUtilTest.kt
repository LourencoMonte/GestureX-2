package com.gesturex

import com.gesturex.data.model.Gesture
import com.gesturex.data.model.TipoGesto
import com.gesturex.util.GestureDetectorUtil
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para GestureDetectorUtil.
 * Valida a detecção de cada tipo de gesto usando dados simulados do acelerômetro.
 */
class GestureDetectorUtilTest {

    private lateinit var detector: GestureDetectorUtil

    private fun gestoPadrao(tipo: String) = Gesture(
        id = 1, nome = "Teste", tipoGesto = tipo,
        acaoDestino = "lanterna", ativo = true
    )

    /**
     * Helper: alimenta leituras e retorna o primeiro gesto detectado (não-null).
     * Necessário porque após a primeira detecção, o cooldown faz chamadas
     * subsequentes retornarem null.
     */
    private fun alimentarEDetectar(
        leituras: List<Triple<Float, Float, Float>>,
        gestos: List<Gesture>
    ): Gesture? {
        var resultado: Gesture? = null
        for ((x, y, z) in leituras) {
            val r = detector.processar(x, y, z, gestos)
            if (r != null && resultado == null) resultado = r
        }
        return resultado
    }

    @Before
    fun setup() {
        detector = GestureDetectorUtil()
        detector.limiar = 9f
    }

    @Test
    fun `detecta gesto AGITAR com inversoes rapidas no eixo X`() {
        val gestos = listOf(gestoPadrao(TipoGesto.AGITAR))
        val leituras = (0 until 20).map { i ->
            Triple(if (i % 2 == 0) 12f else -12f, 0f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto AGITAR", resultado)
        assertEquals(TipoGesto.AGITAR, resultado!!.tipoGesto)
    }

    @Test
    fun `detecta gesto GIRO_CIMA com aceleracao positiva no Y`() {
        val gestos = listOf(gestoPadrao(TipoGesto.GIRO_CIMA))
        val leituras = (0 until 20).map { i ->
            Triple(0f, if (i < 10) -2f else 12f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto GIRO_CIMA", resultado)
        assertEquals(TipoGesto.GIRO_CIMA, resultado!!.tipoGesto)
    }

    @Test
    fun `detecta gesto GIRO_BAIXO com aceleracao negativa no Y`() {
        val gestos = listOf(gestoPadrao(TipoGesto.GIRO_BAIXO))
        val leituras = (0 until 20).map { i ->
            Triple(0f, if (i < 10) 2f else -12f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto GIRO_BAIXO", resultado)
        assertEquals(TipoGesto.GIRO_BAIXO, resultado!!.tipoGesto)
    }

    @Test
    fun `detecta gesto INCLINAR_ESQ com aceleracao negativa no X`() {
        val gestos = listOf(gestoPadrao(TipoGesto.INCLINAR_ESQ))
        val leituras = (0 until 20).map { i ->
            Triple(if (i < 10) 2f else -12f, 0f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto INCLINAR_ESQ", resultado)
        assertEquals(TipoGesto.INCLINAR_ESQ, resultado!!.tipoGesto)
    }

    @Test
    fun `detecta gesto INCLINAR_DIR com aceleracao positiva no X`() {
        val gestos = listOf(gestoPadrao(TipoGesto.INCLINAR_DIR))
        val leituras = (0 until 20).map { i ->
            Triple(if (i < 10) -2f else 12f, 0f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto INCLINAR_DIR", resultado)
        assertEquals(TipoGesto.INCLINAR_DIR, resultado!!.tipoGesto)
    }

    @Test
    fun `detecta gesto VIRAR_BAIXO com variacao no eixo Z`() {
        val gestos = listOf(gestoPadrao(TipoGesto.VIRAR_BAIXO))
        val leituras = (0 until 20).map { i ->
            Triple(0f, 0f, if (i < 10) 0f else 14f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Deveria detectar gesto VIRAR_BAIXO", resultado)
        assertEquals(TipoGesto.VIRAR_BAIXO, resultado!!.tipoGesto)
    }

    @Test
    fun `nao detecta gesto com leituras fracas abaixo do limiar`() {
        val gestos = listOf(
            gestoPadrao(TipoGesto.AGITAR),
            gestoPadrao(TipoGesto.GIRO_CIMA)
        )
        val leituras = (0 until 20).map { Triple(1f, 1f, 1f) }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNull("Não deveria detectar nenhum gesto com leituras fracas", resultado)
    }

    @Test
    fun `respeita cooldown entre deteccoes`() {
        val gestos = listOf(gestoPadrao(TipoGesto.AGITAR))
        // Primeira detecção
        val leituras1 = (0 until 20).map { i ->
            Triple(if (i % 2 == 0) 12f else -12f, 0f, 0f)
        }
        alimentarEDetectar(leituras1, gestos)
        // Imediatamente depois (dentro do cooldown de 1800ms)
        val leituras2 = (0 until 20).map { i ->
            Triple(if (i % 2 == 0) 12f else -12f, 0f, 0f)
        }
        val resultado = alimentarEDetectar(leituras2, gestos)
        assertNull("Deveria respeitar o cooldown e não detectar novamente", resultado)
    }

    @Test
    fun `sensibilidade alta reduz limiar`() {
        detector.setSensibilidade("alta")
        assertEquals(6f, detector.limiar)
    }

    @Test
    fun `sensibilidade baixa aumenta limiar`() {
        detector.setSensibilidade("baixa")
        assertEquals(13f, detector.limiar)
    }

    @Test
    fun `sensibilidade media usa limiar padrao`() {
        detector.setSensibilidade("media")
        assertEquals(9f, detector.limiar)
    }

    @Test
    fun `retorna null quando gesto nao esta na lista de ativos`() {
        val gestos = listOf(gestoPadrao(TipoGesto.GIRO_CIMA))
        val leituras = (0 until 20).map { i ->
            Triple(if (i % 2 == 0) 12f else -12f, 0f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNull("Não deveria retornar gesto que não está na lista de ativos", resultado)
    }

    @Test
    fun `retorna null com janela insuficiente de leituras`() {
        val gestos = listOf(gestoPadrao(TipoGesto.AGITAR))
        val leituras = (0 until 10).map { Triple(12f, 0f, 0f) }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNull("Não deveria detectar com janela insuficiente", resultado)
    }

    @Test
    fun `giroscopio melhora confianca na deteccao de giro`() {
        val gestos = listOf(gestoPadrao(TipoGesto.GIRO_CIMA))
        // Alimenta dados de giroscópio indicando rotação significativa
        for (i in 0 until 20) {
            detector.processarGiroscopio(0f, 3f, 0f)
        }
        val leituras = (0 until 20).map { i ->
            Triple(0f, if (i < 10) -2f else 12f, 0f)
        }
        val resultado = alimentarEDetectar(leituras, gestos)
        assertNotNull("Com giroscópio confirmando, deve detectar GIRO_CIMA", resultado)
    }
}


