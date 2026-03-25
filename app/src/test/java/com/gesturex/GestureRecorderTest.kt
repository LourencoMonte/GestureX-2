package com.gesturex

import com.gesturex.data.model.TipoGesto
import com.gesturex.util.GestureRecorder
import com.gesturex.util.Leitura
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para GestureRecorder.
 * Valida a gravação e classificação de gestos a partir de dados do acelerômetro.
 */
class GestureRecorderTest {

    private lateinit var recorder: GestureRecorder

    @Before
    fun setup() {
        recorder = GestureRecorder()
    }

    @Test
    fun `iniciar limpa leituras anteriores`() {
        recorder.adicionar(1f, 2f, 3f)
        recorder.adicionar(4f, 5f, 6f)
        recorder.iniciar()
        val resultado = recorder.finalizar()
        assertTrue("Iniciar deveria limpar leituras", resultado.isEmpty())
    }

    @Test
    fun `adicionar acumula leituras corretamente`() {
        recorder.iniciar()
        recorder.adicionar(1f, 2f, 3f)
        recorder.adicionar(4f, 5f, 6f)
        recorder.adicionar(7f, 8f, 9f)
        val resultado = recorder.finalizar()
        assertEquals(3, resultado.size)
        assertEquals(1f, resultado[0].x)
        assertEquals(5f, resultado[1].y)
        assertEquals(9f, resultado[2].z)
    }

    @Test
    fun `finalizar retorna copia imutavel`() {
        recorder.iniciar()
        recorder.adicionar(1f, 2f, 3f)
        val lista1 = recorder.finalizar()
        recorder.adicionar(4f, 5f, 6f)
        val lista2 = recorder.finalizar()
        assertEquals("Primeira cópia não deveria mudar", 1, lista1.size)
        assertEquals("Segunda cópia com novo dado", 2, lista2.size)
    }

    @Test
    fun `detectarTipo retorna PERSONALIZADO para dados insuficientes`() {
        val dados = listOf(
            Leitura(0f, 0f, 0f),
            Leitura(1f, 1f, 1f)
        )
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.PERSONALIZADO, tipo)
    }

    @Test
    fun `detectarTipo identifica AGITAR com inversoes no X`() {
        val dados = (0 until 20).map { i ->
            val x = if (i % 2 == 0) 12f else -12f
            Leitura(x, 0f, 0f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.AGITAR, tipo)
    }

    @Test
    fun `detectarTipo identifica GIRO_CIMA com Y positivo dominante`() {
        val dados = (0 until 20).map { i ->
            Leitura(0f, if (i < 10) -2f else 12f, 0f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.GIRO_CIMA, tipo)
    }

    @Test
    fun `detectarTipo identifica GIRO_BAIXO com Y negativo dominante`() {
        val dados = (0 until 20).map { i ->
            Leitura(0f, if (i < 10) 2f else -12f, 0f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.GIRO_BAIXO, tipo)
    }

    @Test
    fun `detectarTipo identifica INCLINAR_ESQ com X negativo dominante`() {
        val dados = (0 until 20).map { i ->
            Leitura(if (i < 10) 2f else -12f, 0f, 0f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.INCLINAR_ESQ, tipo)
    }

    @Test
    fun `detectarTipo identifica INCLINAR_DIR com X positivo dominante`() {
        val dados = (0 until 20).map { i ->
            Leitura(if (i < 10) -2f else 12f, 0f, 0f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.INCLINAR_DIR, tipo)
    }

    @Test
    fun `detectarTipo identifica VIRAR_BAIXO com variacao no Z`() {
        val dados = (0 until 20).map { i ->
            Leitura(0f, 0f, if (i < 10) 0f else 14f)
        }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.VIRAR_BAIXO, tipo)
    }

    @Test
    fun `detectarTipo retorna PERSONALIZADO quando nenhum padrao corresponde`() {
        val dados = (0 until 20).map { Leitura(1f, 1f, 1f) }
        val tipo = recorder.detectarTipo(dados)
        assertEquals(TipoGesto.PERSONALIZADO, tipo)
    }

    @Test
    fun `detectarTipo respeita limiar personalizado`() {
        // Com limiar alto (20), variação de 14 não deveria detectar gesto
        val dados = (0 until 20).map { i ->
            Leitura(0f, 0f, if (i < 10) 0f else 14f)
        }
        val tipo = recorder.detectarTipo(dados, limiar = 20f)
        assertEquals(TipoGesto.PERSONALIZADO, tipo)
    }

    @Test
    fun `adicionarGyro acumula leituras de giroscopio`() {
        recorder.iniciar()
        recorder.adicionarGyro(0.5f, 1.0f, 0.2f)
        recorder.adicionarGyro(0.8f, 0.3f, 1.5f)
        // Giroscópio não interfere nas leituras do acelerômetro
        val resultado = recorder.finalizar()
        assertTrue("Leituras do acelerômetro devem estar vazias", resultado.isEmpty())
    }
}

